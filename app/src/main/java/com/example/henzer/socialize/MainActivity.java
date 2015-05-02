package com.example.henzer.socialize;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.henzer.socialize.Controller.AddNewUser;
import com.example.henzer.socialize.GCMClient.GCMHelper;
import com.example.henzer.socialize.Models.Person;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends Activity{
    // Este es el numero de proyecto para el Google Cloud Messaging (GCM). Este nunca cambia y es estatico.
    private static final String PROJECT_NUMBER = "194566212765";
    public static final String TAG = "MainActivity";

    public static List<UserData> friends = new ArrayList<>();
    public static UserData userLogin;

    public static final String MyPREFERENCES = "MyPrefs";
    public static final String name = "nameKey";
    public static final String pass = "passwordKey";
    private SharedPreferences sharedpreferences;
    private EditText username, password;

    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private ProfileTracker profileTracker;
    private AccessTokenTracker tokenTracker;
    private ProgressDialog pDialog;



    private FacebookCallback<LoginResult> facebookCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            Log.i("PROFILE: ",Profile.getCurrentProfile().getName());
            Profile profile = Profile.getCurrentProfile();
            try {
                userLogin = new UserData(profile.getId(), profile.getName(), new URL("http://graph.facebook.com/" + profile.getId() + "/picture?type=large"));
            }catch(Exception e){
                Log.e(TAG, e.getLocalizedMessage());
            }
            // https://developers.facebook.com/docs/reference/android/current/class/GraphResponse/
            new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/friends", null, HttpMethod.GET, new GraphRequest.Callback() {
                @Override
                public void onCompleted(GraphResponse response) {
                    try {
                        JSONObject objectResponse = response.getJSONObject();
                        JSONArray objectData = (JSONArray) objectResponse.get("data");
                        for (int i=0; i<objectData.length(); i++){
                            JSONObject objectUser = (JSONObject) objectData.get(i);
                            String id = (String) objectUser.get("id");
                            String name = (String) objectUser.get("name");

                            // http://stackoverflow.com/questions/5841710/get-user-image-from-facebook-graph-api
                            // http://stackoverflow.com/questions/23559736/android-skimagedecoderfactory-returned-null-error
                            String path = "http://graph.facebook.com/"+id+"/picture?type=large";
                            URL pathURL = new URL(path);

                            Log.i("Friend "+i,id+" = "+name);
                            Log.i("Friend URL "+i,path.toString());
                            UserData contact = new UserData(id,name,pathURL);
                            friends.add(contact);
                        }

                        Log.i("ACTUAL USER",userLogin.toString());
                        Log.i("ACTUAL FRIENDS", friends.toString());
                        GetGCM();
                    }catch(Exception e){e.printStackTrace();}
                }
            }).executeAsync();
        }

        @Override
        public void onCancel() {
            Log.d("Login","Login Canceled");
        }

        @Override
        public void onError(FacebookException e) {
            e.printStackTrace();
            Log.d("Login","Login Error");
            Log.e("MainActivity", e.getLocalizedMessage());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        username = (EditText) findViewById(R.id.editText1);
        password = (EditText) findViewById(R.id.editText2);

        FacebookSdk.sdkInitialize(this.getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_facebook_button);
        LoginManager.getInstance().registerCallback(callbackManager, facebookCallback);
        setContentView(R.layout.activity_main);
    }

    public void loginwithfb(View view){
        if(AccessToken.getCurrentAccessToken()==null) {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends", "email"));
        }else{
            LoginManager.getInstance().logOut();
        }
    }

    @Override
    protected  void onDestroy(){
        super.onDestroy();
        LoginManager.getInstance().logOut();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (callbackManager.onActivityResult(requestCode, resultCode, data)) return;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("Methods","I passed onResume()");
        if(AccessToken.getCurrentAccessToken()!=null){
            //Log.e("Methods","I resume onResume()");
            //Intent i = new Intent(this,HomeActivity.class);
            //startActivity(i);
        }
    }

    public static List<UserData> getFriends(){
        return friends;
    }

    public void login(View view) {
        Editor editor = sharedpreferences.edit();
        String u = username.getText().toString();
        String p = password.getText().toString();
        editor.putString(name, u);
        editor.putString(pass, p);
        editor.commit();
        GetGCM();

        //Intent i = new Intent(this, com.example. henzer.socialize.HomeActivity.class);
        //startActivity(i);
    }

    private void GetGCM() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    GCMHelper gcmRegistrationHelper = new GCMHelper(getApplicationContext());
                    String gcmRegID = gcmRegistrationHelper.GCMRegister(PROJECT_NUMBER);
                    msg = gcmRegID;
                    Log.i("GCM", gcmRegID);
                    return msg;
                } catch (IOException e) {
                    return null;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
            @Override
            public void onPostExecute(String idMSG) {
                AddNewUser addNewUser = new AddNewUser(MainActivity.this);
                Person nuevo = new Person();
                nuevo.setId(userLogin.getId());
                nuevo.setName(userLogin.getName());
                nuevo.setId_phone(idMSG);
                nuevo.setPhoto(userLogin.getUrl().toString());
                nuevo.setState("A");
                Log.i("NUEVO: ", nuevo.toString());
                try {
                    Boolean resp = addNewUser.execute(nuevo).get();
                    if(resp){
                        SessionData s = new SessionData(userLogin,friends);
                        Intent i = new Intent(MainActivity.this,HomeActivity.class);
                        Log.i("DATA",s.toString());
                        i.putExtra("data",s);
                        startActivity(i);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }
}
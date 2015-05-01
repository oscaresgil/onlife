package com.example.henzer.socialize;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.henzer.socialize.GCMClient.GCMHelper;
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
import java.util.List;

public class MainActivity extends Activity {
    // Este es el numero de proyecto para el Google Cloud Messaging (GCM). Este nunca cambia y es estatico.
    private static final String PROJECT_NUMBER = "194566212765";

    public static List<UserData> friends = new ArrayList<>();
    public static UserData userLogin;

    public static final String MyPREFERENCES = "MyPrefs";
    public static final String name = "nameKey";
    public static final String pass = "passwordKey";
    private SharedPreferences sharedpreferences;
    private EditText username, password;

    private CallbackManager callbackManager;
    private ProfileTracker profileTracker;
    private AccessTokenTracker tokenTracker;
    private FacebookCallback<LoginResult> facebookCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            Log.i("PROFILE: ",Profile.getCurrentProfile().getName());
            Profile profile = Profile.getCurrentProfile();
            try {
                userLogin = new UserData(profile.getId(), profile.getName(), new URL("http://graph.facebook.com/" + profile.getId() + "/picture?type=large"));
            }catch(Exception e){}
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
                        Intent i = new Intent(MainActivity.this,HomeActivity.class);
                        Log.i("ACTUAL USER",userLogin.toString());
                        Log.i("ACTUAL FRIENDS", friends.toString());
                        SessionData s = new SessionData(userLogin,friends);
                        Log.i("DATA",s.toString());
                        i.putExtra("data",s);
                        startActivity(i);
                    }catch(Exception e){e.printStackTrace();}
                }
            }).executeAsync();

            Log.i("Friends Array ",friends.toString());
            GetGCM();
            Log.d("Login","Login Successful");
        }

        @Override
        public void onCancel() {
            Log.d("Login","Login Canceled");
        }

        @Override
        public void onError(FacebookException e) {
            Log.d("Login","Login Error");
        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_main);
        username = (EditText) findViewById(R.id.editText1);
        password = (EditText) findViewById(R.id.editText2);
    }

    public void setupTokenTracker(){
        tokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken newAccessToken) {
                Log.d("Token Tracker 1",""+oldAccessToken);
                Log.d("Token Tracker 2",""+newAccessToken);
                tokenTracker.stopTracking();
                AccessToken.setCurrentAccessToken(newAccessToken);
            }
        };
        tokenTracker.startTracking();
    }
    public void setupProfileTracker(){
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                Log.d("Profile Tracker 1",""+oldProfile);
                Log.d("Profile Tracker 2",""+newProfile);
                profileTracker.stopTracking();
                Profile.setCurrentProfile(newProfile);
            }
        };
        profileTracker.startTracking();
    }

    public void loginwithfb(View view){
        callbackManager = CallbackManager.Factory.create();
        setupTokenTracker();
        setupProfileTracker();

        LoginButton fbButton = (LoginButton) view.findViewById(R.id.login_facebook_button);
        fbButton.setReadPermissions("public_profile", "user_friends", "email");
        fbButton.registerCallback(callbackManager, facebookCallback);
    }

    @Override
    protected void onStop(){
        super.onDestroy();
    }

    @Override
    protected  void onDestroy(){
        super.onDestroy();
        LoginManager.getInstance().logOut();
        tokenTracker.stopTracking();
        profileTracker.stopTracking();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (callbackManager.onActivityResult(requestCode, resultCode, data)) return;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("Methods","I passed onResume()");
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        if (sharedpreferences.contains(name)) {
            if (sharedpreferences.contains(pass)) {
                Intent i = new Intent(this,HomeActivity.class);

                startActivity(i);
            }
        }
        else if(AccessToken.getCurrentAccessToken()!=null){
            Log.i("Methods","I resume onResume()");
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
        friends.add(new UserData("123456789","Juan Prueba",null));
        friends.add(new UserData("12345674219","Juan Prueba2",null));
        friends.add(new UserData("123456789","Juan Prueba3 ",null));

        Intent i = new Intent(this, com.example. henzer.socialize.HomeActivity.class);
        startActivity(i);
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
                } catch (IOException e) {
                    msg = "Error : " + e.getMessage();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return msg;
            }

            /*
            * En este metodo se deberia de almacenar en la base de datos este numero de ID del dispositivo
             */
            @Override
            public void onPostExecute(String msg) {
            }
        }.execute();
    }
}
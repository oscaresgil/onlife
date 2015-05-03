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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.henzer.socialize.Controller.LoadAllInformation;
import com.example.henzer.socialize.GCMClient.GCMHelper;
import com.example.henzer.socialize.Models.Person;
import com.example.henzer.socialize.Models.SessionData;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends Activity{
    // Este es el numero de proyecto para el Google Cloud Messaging (GCM). Este nunca cambia y es estatico.
    private static final String PROJECT_NUMBER = "194566212765";
    public static final String TAG = "MainActivity";

    public List<Person> friends = new ArrayList();
    public Person userLogin;

    public static final String MyPREFERENCES = "MyPrefs";
    public static final String name = "nameKey";
    public static final String pass = "passwordKey";
    private SharedPreferences sharedpreferences;
    private EditText username, password;

    private Button loginFB;
    private CallbackManager callbackManager;
    private ProfileTracker profileTracker;
    private AccessTokenTracker tokenTracker;
    private ProgressDialog pDialog;



    private FacebookCallback<LoginResult> facebookCallback = new FacebookCallback<LoginResult>() {
        private ProfileTracker mProfileTracker;

        @Override
        public void onSuccess(LoginResult loginResult) {
            mProfileTracker = new ProfileTracker() {
                @Override
                protected void onCurrentProfileChanged(Profile pr, Profile pr2) {
                    Log.i("PROFILE: ",Profile.getCurrentProfile().getName());
                    Profile profile = Profile.getCurrentProfile();
                    try {
                        userLogin = new Person(profile.getId(), null, profile.getName(), "http://graph.facebook.com/" + profile.getId() + "/picture?type=large", "A");
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
                                    Person contact = new Person(id, null, name, pathURL.toString(), "A");
                                    friends.add(contact);
                                }
                                Collections.sort(friends, new nameComparator());
                                Log.i("ACTUAL USER",userLogin.toString());
                                Log.i("ACTUAL FRIENDS", friends.toString());
                                GetGCM();
                            }catch(Exception e){e.printStackTrace();}
                        }
                    }).executeAsync();
                    mProfileTracker.stopTracking();
                }
            };
            mProfileTracker.startTracking();
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

        setContentView(R.layout.activity_main);
        loginFB = (Button) findViewById(R.id.loginFB);

        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, facebookCallback);
    }

    public void loginwithfb(View view){
        friends = new ArrayList<>();
        if(AccessToken.getCurrentAccessToken()==null) {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends", "email"));
            loginFB.setText("Log out");
        }else{
            LoginManager.getInstance().logOut();
            loginFB.setText("Login with facebook");
        }
    }

    /*@Override
    protected  void onDestroy(){
        super.onDestroy();
        //LoginManager.getInstance().logOut();
    }*/

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
            SharedPreferences prefe=getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            try {
                JSONObject mySession = new JSONObject(prefe.getString("session", ""));
                JSONObject me = mySession.getJSONObject("me");
                JSONArray fr = mySession.getJSONArray("friends");
                userLogin = new Person(me.getString("id"), me.getString("id_phone"), me.getString("name"), me.getString("photo"), me.getString("state"));
                friends = new ArrayList();
                Person friend = null;
                for(int i = 0; i<fr.length(); i++){
                    JSONObject f = fr.getJSONObject(i);
                    friend = new Person(f.getString("id"), f.getString("id_phone"), f.getString("name"), f.getString("photo"), f.getString("state"));
                    Log.i("FRIEND AFTER CLOSING",friend.toString());
                    friends.add(friend);
                }

                SessionData s = new SessionData(userLogin,friends);
                Intent i = new Intent(MainActivity.this,HomeActivity.class);
                Log.i("DATA",s.toString());
                i.putExtra("data",s);
                startActivity(i);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    class nameComparator implements Comparator<Person> {
        @Override
        public int compare(Person person1, Person person2) {
            return person1.getName().compareTo(person2.getName());
        }
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
                //AddNewUser addNewUser = new AddNewUser(MainActivity.this);
                userLogin.setId_phone(idMSG);
                LoadAllInformation load = new LoadAllInformation(MainActivity.this);
                List<Person> enviados = new ArrayList<Person>();
                enviados.add(userLogin);
                enviados.addAll(friends);
                JSONObject data = null;
                try {
                    data = load.execute(enviados).get();
                    Log.e(TAG, data.toString());

                    boolean error = data.getBoolean("error");
                    String mensaje = data.getString("message");
                    if(error == false){
                        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                        Editor editor = sharedpreferences.edit();
                        editor.putString("session", data.toString());
                        editor.commit();

                        SessionData s = new SessionData(userLogin,friends);
                        Intent i = new Intent(MainActivity.this,HomeActivity.class);
                        Log.i("DATA",s.toString());
                        i.putExtra("data",s);
                        startActivity(i);
                    }else{
                        Toast.makeText(MainActivity.this, mensaje, Toast.LENGTH_SHORT).show();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }
}
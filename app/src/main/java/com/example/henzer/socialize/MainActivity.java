package com.example.henzer.socialize;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.henzer.socialize.BlockActivity.DeviceAdmin;
import com.example.henzer.socialize.Controller.LoadAllInformation;
import com.example.henzer.socialize.GCMClient.GCMHelper;
import com.example.henzer.socialize.Models.Group;
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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
    private static final int ACTIVATION_REQUEST = 47;

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

    ComponentName myDeviceAdmin;
    DevicePolicyManager devicePolicyManager;
    private boolean isAdminActive;

    private ProgressDialog progressDialog;


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
                  //  progressDialog = ProgressDialog.show(MainActivity.this,"Please Wait","Loading Friends",true);
                   // progressDialog.setCancelable(true);
                    Bundle params = new Bundle();
                    params.putString("fields","id,name");
                    new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/friends", params, HttpMethod.GET, new GraphRequest.Callback() {
                        @Override
                        public void onCompleted(GraphResponse response) {
                            try {
                                JSONObject objectResponse = response.getJSONObject();
                                JSONArray objectData = (JSONArray) objectResponse.get("data");
                                String [] ids = new String[objectData.length()];
                                Log.i("DATA", objectData.toString());
                                for (int i=0; i<objectData.length(); i++){
                                    JSONObject objectUser = (JSONObject) objectData.get(i);
                                    String id = (String) objectUser.get("id");
                                    ids[i] = id;
                                    String name = (String) objectUser.get("name");

                                    // http://stackoverflow.com/questions/5841710/get-user-image-from-facebook-graph-api
                                    // http://stackoverflow.com/questions/23559736/android-skimagedecoderfactory-returned-null-error
                                    //String path = "http://graph.facebook.com/"+id+"/picture?type=large";
                                    String path = "https://graph.facebook.com/" + id + "/picture?width=900&height=900";
                                    //Bitmap img = null;
                                    URL pathURL = new URL(path);
                                    //guardarImagen(MainActivity.this, id, new DownloadImageTask().execute(id).get());

                                    Log.i("Friend "+i,id+" = "+eliminarTilde(name));
                                    Log.i("Friend URL "+i,path.toString());
                                    Person contact = new Person(id, null, eliminarTilde(name), pathURL.toString(), "A");
                                    friends.add(contact);
                                }

                                new DownloadImageTask().execute(ids);

                                Collections.sort(friends, new nameComparator());
                                Log.i("ACTUAL USER",userLogin.toString());
                                Log.i("ACTUAL FRIENDS", friends.toString());
                                GetGCM();
                                //progressDialog.dismiss();
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
        // Launch the activity to have the user enable our admin.
        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);

        myDeviceAdmin = new ComponentName(this, DeviceAdmin.class);
        isAdminActive = devicePolicyManager.isAdminActive(myDeviceAdmin);
        System.out.println(isAdminActive);
        if (!isAdminActive) {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, myDeviceAdmin);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    this.getString(R.string.device_admin_description));
            startActivityForResult(intent, ACTIVATION_REQUEST);
        }


        /*devicePolicyManager.removeActiveAdmin(myDeviceAdmin);
        enableDeviceCapabilitiesArea(false);
        mAdminActive = false;
        }*/

        FacebookSdk.sdkInitialize(this.getApplicationContext());

        setContentView(R.layout.activity_main);
        loginFB = (Button) findViewById(R.id.loginFB);

        callbackManager = CallbackManager.Factory.create();
        if (isNetworkAvailable()) {
            LoginManager.getInstance().registerCallback(callbackManager, facebookCallback);
        }else{
            Toast.makeText(this,"No connection",Toast.LENGTH_LONG).show();
        }
    }

    public String eliminarTilde(String input) {
        String output = input;
        for (int i=0; i<output.length(); i++){
            if ((int)output.charAt(i) == 237){
                output = output.replace(output.charAt(i),'i');
            }
            else if((int)output.charAt(i) == 243){
                output = output.replace(output.charAt(i),'o');
            }
            else if((int)output.charAt(i) == 250){
                output = output.replace(output.charAt(i),'u');
            }
            else if((int)output.charAt(i) == 225){
                output = output.replace(output.charAt(i),'a');
            }
            else if((int)output.charAt(i) == 233){
                output = output.replace(output.charAt(i),'e');
            }
        }
        return output;
    }

    public void loginWithFB(View view){
        if (isNetworkAvailable()) {
            friends = new ArrayList<>();
            if (AccessToken.getCurrentAccessToken() == null) {
                try {
                    LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends", "email"));
                    loginFB.setText("Log out");
                } catch (Exception ex) {
                    Toast.makeText(this, "There was a problem.", Toast.LENGTH_SHORT).show();
                }
            } else {
                try {
                    LoginManager.getInstance().logOut();
                    loginFB.setText("Login with facebook");
                    SharedPreferences sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.clear();
                    editor.commit();
                } catch (Exception ex) {
                    Toast.makeText(this, "There was a problem.", Toast.LENGTH_SHORT).show();
                }
            }
        }else{
            Toast.makeText(this,"No connection",Toast.LENGTH_LONG).show();
        }
    }

    /*public void lock(View view){
        if (isAdminActive)
            devicePolicyManager.lockNow();
        Log.d("mDPM", devicePolicyManager.toString());
    }*/

    /*@Override
    protected  void onDestroy(){
        super.onDestroy();
        //LoginManager.getInstance().logOut();
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ACTIVATION_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    Log.i("DeviceAdminSample", "Administration enabled!");
                } else {
                    Log.i("DeviceAdminSample", "Administration enable FAILED!");
                }
                return;
        }
        super.onActivityResult(requestCode, resultCode, data);
        if (callbackManager.onActivityResult(requestCode, resultCode, data)) return;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("Methods", "I passed onResume()");
        if(AccessToken.getCurrentAccessToken()!=null){
            gotoHome();
        }
    }
    class nameComparator implements Comparator<Person> {
        @Override
        public int compare(Person person1, Person person2) {
            return person1.getName().compareTo(person2.getName());
        }
    }

    private String guardarImagen(Context context, String name, Bitmap image){
        ContextWrapper cw = new ContextWrapper(context);
        File dirImages = cw.getDir("Profiles", Context.MODE_PRIVATE);
        File myPath = new File(dirImages, name+".png");

        FileOutputStream fos = null;
        try{
            fos = new FileOutputStream(myPath);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.flush();
        }catch (Exception e){e.printStackTrace();}
        Log.i("IMAGE SAVED","PATH: "+myPath);
        return myPath.getAbsolutePath();
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
                List<Person> enviados = new ArrayList();
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
                        data.put("groups", new JSONArray());
                        editor.putString("session", data.toString());
                        editor.commit();
                        gotoHome();
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

    private class DownloadImageTask extends AsyncTask<String, Void, Void> {

        com.afollestad.materialdialogs.MaterialDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new MaterialDialog.Builder(MainActivity.this)
                    .titleColorRes(R.color.black)
                    .contentColorRes(R.color.black)
                    .widgetColorRes(R.color.white)
                    .dividerColor(Color.WHITE)
                    .backgroundColorRes(R.color.orange_light)
                    .title(R.string.title_dialog)
                    .content(R.string.message_dialog)
                    .progress(true, 0)
                    .show();
        }

        protected Void doInBackground(String... urls) {
            try {
                for (String userID: urls){
                    Log.i("Actual BackGround User",userID);
                    String urlStr = "https://graph.facebook.com/" + userID + "/picture?width=700&height=700";

                    HttpClient client = new DefaultHttpClient();
                    HttpGet request = new HttpGet(urlStr);
                    HttpResponse response;
                    try {
                        response = (HttpResponse)client.execute(request);
                        HttpEntity entity = response.getEntity();
                        BufferedHttpEntity bufferedEntity = new BufferedHttpEntity(entity);
                        InputStream inputStream = bufferedEntity.getContent();
                        guardarImagen(MainActivity.this,userID,BitmapFactory.decodeStream(inputStream));
                    } catch (ClientProtocolException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }catch(Exception e){e.printStackTrace();}
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void gotoHome(){
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
                System.out.println(f.getString("name"));
                friend = new Person(f.getString("id"), f.getString("id_phone"), eliminarTilde(f.getString("name")), f.getString("photo"), f.getString("state"));
                Log.i("FRIEND AFTER CLOSING",friend.toString());
                friends.add(friend);
            }
            List<Group> groups = new ArrayList();
            if(mySession.getJSONArray("groups")!=null){
                JSONArray myGroups = mySession.getJSONArray("groups");
                for(int i = 0; i<myGroups.length(); i++){
                    JSONObject element = myGroups.getJSONObject(i);
                    JSONArray people = element.getJSONArray("people");
                    List<Person> peop = new ArrayList();
                    for(int j=0; j<people.length(); j++){
                        JSONObject per = people.getJSONObject(j);
                        peop.add(new Person(per.getString("id"), per.getString("id_phone"), per.getString("name"), per.getString("photo"), per.getString("state")));
                    }
                    Group g = new Group(element.getInt("id"), element.getString("name"), peop, element.getString("photo"), element.getInt("limit"), element.getString("state"));
                    groups.add(g);
                }
            }
            SessionData s = new SessionData(userLogin,friends,groups);
            Intent i = new Intent(MainActivity.this,HomeActivity.class);
            Log.i("DATA",s.toString());
            i.putExtra("data",s);
            startActivity(i);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
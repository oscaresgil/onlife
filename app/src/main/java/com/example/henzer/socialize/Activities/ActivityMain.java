package com.example.henzer.socialize.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import com.example.henzer.socialize.Models.ModelPerson;
import com.example.henzer.socialize.R;
import com.example.henzer.socialize.Tasks.TaskAddNewUser;
import com.example.henzer.socialize.Tasks.TaskFacebookFriendRequest;
import com.example.henzer.socialize.Tasks.TaskGetGCM;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.Gson;
import com.kenny.snackbar.SnackBar;

import java.util.ArrayList;
import java.util.List;

import static com.example.henzer.socialize.Controller.StaticMethods.activateDeviceAdmin;
import static com.example.henzer.socialize.Controller.StaticMethods.isNetworkAvailable;

public class ActivityMain extends Activity{
    public static final String PROJECT_NUMBER = "194566212765";
    public static final String SERVER_URL = "http://104.236.74.55/onlife/person.php";
    public static final String TAG = "ActivityMain";
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String name = "nameKey";

    private List<ModelPerson> friends = new ArrayList();
    private ModelPerson userLogin;

    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private SharedPreferences sharedPreferences;

    private Bundle infoFB;

    private ProfileTracker mProfileTracker;


    private FacebookCallback<LoginResult> facebookCallback = new FacebookCallback<LoginResult>() {


        @Override
        public void onSuccess(LoginResult loginResult) {
            Log.i("Login", "Login Success");
            //mProfileTracker.startTracking();
        }

        @Override
        public void onCancel() {
            Log.d("Login", "Login Canceled");

        }

        @Override
        public void onError(FacebookException e) {
            e.printStackTrace();
            SnackBar.show(ActivityMain.this, R.string.no_server);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Launch the activity to have the user enable our admin.
        activateDeviceAdmin(this);

        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        if (!sharedPreferences.contains("idGcm")){
            TaskGetGCM gcm = new TaskGetGCM(this);
            gcm.execute();
        }

        FacebookSdk.sdkInitialize(this.getApplicationContext());

        setContentView(R.layout.activity_main);
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("public_profile", "user_friends", "email");

        LoginManager.getInstance().logOut();
        SharedPreferences preferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        preferences.edit().clear().commit();

        mProfileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile profile) {

                Gson gson = new Gson();
                sharedPreferences = getSharedPreferences(ActivityMain.MyPREFERENCES, Context.MODE_PRIVATE);
                userLogin = new ModelPerson(profile.getId(), sharedPreferences.getString("idGcm",""), profile.getName(), "http://graph.facebook.com/" + profile.getId() + "/picture?type=large", "A");
                sharedPreferences.edit().putString("userLogin", gson.toJson(userLogin)).commit();
                TaskAddNewUser taskAddNewUser = new TaskAddNewUser();
                taskAddNewUser.execute(userLogin);

                Bundle params = new Bundle();
                params.putString("fields", "id,name");
                new GraphRequest(AccessToken.getCurrentAccessToken(),"/me/friends",params,HttpMethod.GET, new TaskFacebookFriendRequest(ActivityMain.this,TAG)).executeAsync();
                mProfileTracker.stopTracking();
            }
        };
        mProfileTracker.startTracking();

        callbackManager = CallbackManager.Factory.create();
        if (isNetworkAvailable(ActivityMain.this)) {
            loginButton.registerCallback(callbackManager, facebookCallback);
        }else{
            SnackBar.show(ActivityMain.this, R.string.no_connection, R.string.button_change_connection, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                }
            });
        }
        Log.i("Carga", "Todo bien hasta ahora");
    }

/*    public void loginWithFB(View view){
        if (isNetworkAvailable(ActivityMain.this)) {
            friends = new ArrayList<>();
            if (AccessToken.getCurrentAccessToken() == null) {
                try {
                    LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends", "email"));
                    Log.i("METODO", "Entro a loginButton");
                    //loginButton.setText(R.string.action_logout);
                } catch (Exception ex) {
                    //Toast.makeText(this, "There was a problem.", Toast.LENGTH_SHORT).show();
                }
            } else {
                try {
                    LoginManager.getInstance().logOut();
                    Log.i("METODO", "Entroa loginButton null");
                    //loginButton.setText(R.string.login_facebook);
                    SharedPreferences sharedpreferences = getSharedPreferences(ActivityMain.MyPREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.clear();
                    editor.commit();
                } catch (Exception ex) {
                    //Toast.makeText(this, "There was a problem.", Toast.LENGTH_SHORT).show();
                }
            }
        }else{
            SnackBar.show(ActivityMain.this, R.string.no_connection, R.string.button_change_connection, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                }
            });
        }
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*switch (requestCode) {
            case StaticMethods.ACTIVATION_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    Log.i("DeviceAdminSample", "Administration enabled!");
                } else {
                    Log.i("DeviceAdminSample", "Administration enable FAILED!");
                }
                return;
        }*/
        super.onActivityResult(requestCode, resultCode, data);
        if (callbackManager.onActivityResult(requestCode, resultCode, data)) return;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("Methods", "I passed onResume()");
        if (!isNetworkAvailable(ActivityMain.this)) {
            SnackBar.show(ActivityMain.this, R.string.no_connection, R.string.button_change_connection, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                }
            });
        }
        if(sharedPreferences.contains("session")){
            gotoHome();
        }
    }

    /*public void GetGCM() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    GcmHelper gcmRegistrationHelper = new GcmHelper(getApplicationContext());
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
                TaskLoadAllInformation load = new TaskLoadAllInformation(ActivityMain.this);
                List<ModelPerson> enviados = new ArrayList();
                enviados.add(userLogin);
                enviados.addAll(friends);
                JSONObject data = null;
                try {
                    data = load.execute(enviados).get();
                    Log.e(TAG, data.toString());

                    boolean error = data.getBoolean("error");
                    String mensaje = data.getString("message");
                    if(!error){
                        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                        Editor editor = sharedpreferences.edit();
                        data.put("activity_groups", new JSONArray());
                        editor.putString("session", data.toString());
                        editor.commit();

                        gotoHome();
                    }else{
                        SnackBar.show(ActivityMain.this, mensaje);
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
    }*/


    public void gotoHome(){

        Intent home = new Intent(ActivityMain.this,ActivityHome.class);
        startActivity(home);
    }
}
package com.example.henzer.socialize.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import com.example.henzer.socialize.Models.ModelGroup;
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
import com.facebook.internal.WebDialog;
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
    public static final String MyPREFERENCES = "OnlifePrefs";
    public static final String name = "nameKey";

    private List<ModelPerson> friends = new ArrayList<>();
    private ModelPerson userLogin;

    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private SharedPreferences sharedPreferences;

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

        // Review first instalation
        if(!sharedPreferences.contains("onboarding_complete")) {
            Intent onboarding = new Intent(this, ActivityOnboarding.class);
            startActivity(onboarding);
            finish();
        }

        // Get phone GCM
        if (!sharedPreferences.contains("idGcm")){
            TaskGetGCM gcm = new TaskGetGCM(this);
            gcm.execute();
        }

        FacebookSdk.sdkInitialize(this.getApplicationContext());

        setContentView(R.layout.activity_main);
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("public_profile", "user_friends", "email");

        /*LoginManager.getInstance().logOut();
        SharedPreferences preferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        preferences.edit().clear().commit();*/

        mProfileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile profile) {
                Log.e(TAG, "Profile Changed");
                if (profile!=null) {
                    Gson gson = new Gson();
                    sharedPreferences = getSharedPreferences(ActivityMain.MyPREFERENCES, Context.MODE_PRIVATE);
                    userLogin = new ModelPerson(profile.getId(), sharedPreferences.getString("gcmId", ""), profile.getName(), "http://graph.facebook.com/" + profile.getId() + "/picture?", "A");
                    sharedPreferences.edit().putString("userLogin", gson.toJson(userLogin)).apply();
                    sharedPreferences.edit().putString("friends", gson.toJson(new ArrayList<ModelPerson>())).apply();
                    sharedPreferences.edit().putString("groups", gson.toJson(new ArrayList<ModelGroup>())).apply();
                    TaskAddNewUser taskAddNewUser = new TaskAddNewUser();
                    taskAddNewUser.execute(userLogin);

                    Bundle params = new Bundle();
                    params.putString("fields", "id,name");
                    new GraphRequest(AccessToken.getCurrentAccessToken(),"/me/friends",params,HttpMethod.GET, new TaskFacebookFriendRequest(ActivityMain.this,TAG,userLogin)).executeAsync();
                    mProfileTracker.stopTracking();
                }
            }
        };

        callbackManager = CallbackManager.Factory.create();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (callbackManager.onActivityResult(requestCode, resultCode, data)) return;
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume()");

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
        }else{
            mProfileTracker.startTracking();
        }
    }

    public void gotoHome(){

        Intent home = new Intent(ActivityMain.this,ActivityHome.class);
        startActivity(home);
    }
}
package com.objective4.app.onlife.Activities;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.gson.Gson;
import com.objective4.app.onlife.Models.ModelGroup;
import com.objective4.app.onlife.Models.ModelPerson;
import com.objective4.app.onlife.R;
import com.objective4.app.onlife.Tasks.TaskAddNewUser;
import com.objective4.app.onlife.Tasks.TaskFacebookFriendRequest;
import com.objective4.app.onlife.Tasks.TaskGetGCM;

import java.util.ArrayList;

import static com.objective4.app.onlife.Controller.StaticMethods.activateDeviceAdmin;
import static com.objective4.app.onlife.Controller.StaticMethods.isNetworkAvailable;

public class ActivityMain extends Activity{
    public static final String PROJECT_NUMBER = "194566212765";
    public static final String SERVER_URL = "http://104.236.74.55/onlife/person.php";
    public static final String TAG = "ActivityMain";
    public static final String MyPREFERENCES = "OnlifePrefs";
    public static final String name = "nameKey";

    private ModelPerson userLogin;

    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private SharedPreferences sharedPreferences;

    private ProfileTracker mProfileTracker;

    /*private FacebookCallback<LoginResult> facebookCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
        }

        @Override
        public void onCancel() {
        }

        @Override
        public void onError(FacebookException e) {
            e.printStackTrace();
            SnackBar.show(ActivityMain.this, R.string.no_server);
        }
    };*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Launch the activity to have the user enable our admin.
        activateDeviceAdmin(this);

        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        // Review first instalation
        if(!sharedPreferences.contains("onboarding_complete")) {
            startActivity(new Intent(this, ActivityOnboarding.class));
            finish();
        }

        // Get phone GCM
        if ("".equals(sharedPreferences.getString("idGcm",""))){
            new TaskGetGCM(this).execute();
        }

        FacebookSdk.sdkInitialize(this.getApplicationContext());

        setContentView(R.layout.activity_main);
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("public_profile", "user_friends", "email");

        mProfileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile profile) {
                if (profile!=null && !sharedPreferences.getBoolean("session",false)) {
                    Gson gson = new Gson();
                    sharedPreferences = getSharedPreferences(ActivityMain.MyPREFERENCES, Context.MODE_PRIVATE);
                    userLogin = new ModelPerson(profile.getId(), sharedPreferences.getString("gcmId", ""), profile.getName(), "http://graph.facebook.com/" + profile.getId() + "/picture?", "A");
                    sharedPreferences.edit().putBoolean("first_login", true).apply();
                    sharedPreferences.edit().putString("userLogin", gson.toJson(userLogin)).apply();
                    sharedPreferences.edit().putString("friends", gson.toJson(new ArrayList<ModelPerson>())).apply();
                    sharedPreferences.edit().putString("groups", gson.toJson(new ArrayList<ModelGroup>())).apply();
                    new TaskAddNewUser(ActivityMain.this).execute(userLogin);

                    Bundle params = new Bundle();
                    params.putString("fields", "id,name");
                    params.putString("limit", "5000");
                    new GraphRequest(AccessToken.getCurrentAccessToken(),"/me/friends",params,HttpMethod.GET, new TaskFacebookFriendRequest(ActivityMain.this,TAG,userLogin)).executeAsync();
                    mProfileTracker.stopTracking();
                }
                else if(sharedPreferences.getBoolean("session",false)) gotoHome();
            }
        };

        callbackManager = CallbackManager.Factory.create();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isNetworkAvailable(this)) {
            if (sharedPreferences.getBoolean("update_playservice", false)) {
                int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
                if (resultCode == ConnectionResult.SUCCESS) {
                    new TaskGetGCM(this).execute();
                } else {
                    new AlertDialogWrapper.Builder(this)
                            .setTitle(getResources().getString(R.string.update_playservices))
                            .setMessage(getResources().getString(R.string.update_forced))
                            .setCancelable(false)
                            .setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String appPackageName = GooglePlayServicesUtil.GOOGLE_PLAY_SERVICES_PACKAGE;
                                    try {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                    } catch (ActivityNotFoundException e) {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                    }
                                }
                            }).show();
                }
            } else {
                if (sharedPreferences.getBoolean("session", false)) {
                    gotoHome();
                } else {
                    mProfileTracker.startTracking();
                }
            }
        }else{
            new AlertDialogWrapper.Builder(this)
                    .setTitle(getResources().getString(R.string.no_connection))
                    .setMessage(getResources().getString(R.string.no_connection_connect))
                    .setCancelable(false)
                    .setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            System.exit(0);
                        }
                    }).show();
        }
    }

    public void privacyPolicyAction(View view){
        Intent i = new Intent(this,ActivityPrivacyPolicy.class);
        startActivity(i);
    }

    public void gotoHome(){
        Intent home = new Intent(ActivityMain.this,ActivityHome.class);
        startActivity(home);
    }
}
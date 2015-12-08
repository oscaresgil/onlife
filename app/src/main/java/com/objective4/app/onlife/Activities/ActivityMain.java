package com.objective4.app.onlife.Activities;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginResult;
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

import org.json.JSONObject;

import java.util.ArrayList;

import static com.objective4.app.onlife.Controller.StaticMethods.activateDeviceAdmin;
import static com.objective4.app.onlife.Controller.StaticMethods.deactivateDeviceAdmin;
import static com.objective4.app.onlife.Controller.StaticMethods.makeSnackbar;

public class ActivityMain extends AppCompatActivity{
    public static final String PROJECT_NUMBER = "194566212765";
    public static final String MyPREFERENCES = "OnlifePrefs";
    public static final String SERVER_URL = "http://api.onlife-app.com/";

    private ModelPerson userLogin;
    private Dialog dialog;
    private boolean gcmFlag = false;
    private LoginButton loginButton;

    private CallbackManager callbackManager;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activateDeviceAdmin(this);

        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        if(!sharedPreferences.contains("onboarding_complete")) {
            startActivity(new Intent(this, ActivityOnboarding.class));
            finish();
        }

        sharedPreferences.edit().remove("idGcm");
        if ("".equals(sharedPreferences.getString("idGcm",""))){
            if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
                new TaskGetGCM(this).execute();
                gcmFlag = false;
            }else{
                setDialogGCM();
                gcmFlag = true;
            }
        }

        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.ActivityMain_ToolBar));
        setTitle("");

        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("public_profile", "user_friends", "email");
        callbackManager = CallbackManager.Factory.create();
        try {
            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(final LoginResult loginResult) {
                    GraphRequest request = GraphRequest.newMeRequest(
                            loginResult.getAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject object, GraphResponse response) {
                                    try {
                                        Gson gson = new Gson();
                                        sharedPreferences = getSharedPreferences(ActivityMain.MyPREFERENCES, Context.MODE_PRIVATE);

                                        object.put("id_phone", sharedPreferences.getString("gcmId", ""));
                                        object.put("age", (new JSONObject(object.getString("age_range"))).getInt("min"));
                                        object.put("tag", "newUser");
                                        object.put("location", "");
                                        object.remove("age_range");
                                        new TaskAddNewUser(ActivityMain.this).execute(object);

                                        loginButton.setEnabled(false);
                                        userLogin = new ModelPerson(object.getString("id"), object.getString("name"));

                                        sharedPreferences.edit().putBoolean("first_login", true).apply();
                                        sharedPreferences.edit().putString("userLogin", gson.toJson(userLogin)).apply();
                                        sharedPreferences.edit().putString("friends", gson.toJson(new ArrayList<ModelPerson>())).apply();
                                        sharedPreferences.edit().putString("groups", gson.toJson(new ArrayList<ModelGroup>())).apply();
                                        sharedPreferences.edit().putBoolean("session", true).apply();

                                        Bundle params = new Bundle();
                                        params.putString("fields", "id,name");
                                        params.putString("limit", "5000");
                                        new GraphRequest(loginResult.getAccessToken(), "/me/friends", params, HttpMethod.GET, new TaskFacebookFriendRequest(ActivityMain.this, userLogin)).executeAsync();
                                    } catch (Exception ignored) {
                                        makeSnackbar(ActivityMain.this, findViewById(R.id.login_button), R.string.error, Snackbar.LENGTH_LONG);
                                    }
                                }
                            });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,name,gender,email,age_range");
                    request.setParameters(parameters);
                    request.executeAsync();
                }

                @Override
                public void onCancel() {
                    loginButton.setEnabled(true);
                }

                @Override
                public void onError(FacebookException error) {
                    error.printStackTrace();
                    makeSnackbar(ActivityMain.this, findViewById(R.id.login_button), R.string.error, Snackbar.LENGTH_LONG);
                }
            });
        }catch (Exception e){
            makeSnackbar(ActivityMain.this, findViewById(R.id.login_button), R.string.error, Snackbar.LENGTH_LONG);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.MenuMain_Uninstall_app){
            new MaterialDialog.Builder(ActivityMain.this)
                    .title(getResources().getString(R.string.settings_option_uninstall) + "?")
                    .content(R.string.really_delete)
                    .positiveText(R.string.yes)
                    .positiveColorRes(R.color.accent)
                    .negativeText(R.string.no)
                    .negativeColorRes(R.color.black)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                            deactivateDeviceAdmin(ActivityMain.this);

                            Intent intent = new Intent(Intent.ACTION_DELETE);
                            intent.setData(Uri.parse("package:" + getPackageName()));
                            startActivity(intent);

                            dialog.dismiss();
                            dialog.cancel();
                        }
                    }).show();
        }
        return false;
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
        if (gcmFlag){
            setDialogGCM();
        }
    }

    public void privacyPolicyAction(View view){
        Intent i = new Intent(this,ActivityPrivacyPolicy.class);
        startActivity(i);
    }

    public void gotoHome() {
        if (!ActivityHome.isRunning){
            Intent home = new Intent(ActivityMain.this, ActivityHome.class);
            startActivity(home);
            finish();
        }
    }

    public void setDialogGCM(){
        if (dialog==null || !dialog.isShowing())
            dialog = new AlertDialogWrapper.Builder(this)
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
}
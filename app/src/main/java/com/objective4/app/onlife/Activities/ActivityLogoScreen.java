package com.objective4.app.onlife.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.eftimoff.androipathview.PathView;
import com.facebook.FacebookSdk;
import com.objective4.app.onlife.R;
import com.objective4.app.onlife.Tasks.TaskCheckVersion;

import java.util.Timer;
import java.util.TimerTask;

public class ActivityLogoScreen extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        SharedPreferences sharedPreferences = getSharedPreferences(ActivityMain.MyPREFERENCES, Context.MODE_PRIVATE);
        new TaskCheckVersion(this).execute();
        boolean sessionTag = sharedPreferences.getBoolean("session", false);
        if (!sessionTag){
            setContentView(R.layout.activity_logo_screen);

            PathView pathView = (PathView) findViewById(R.id.ActivityLogoScreen_PathView);
            pathView.getPathAnimator()
                    .delay(getResources().getInteger(R.integer.activity_logo_screen_path_animator_delay))
                    .duration(getResources().getInteger(R.integer.activity_logo_screen_path_animator_duration))
                    .start();
            pathView.setFillAfter(true);
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    Intent mainIntent = new Intent().setClass(ActivityLogoScreen.this,ActivityMain.class);
                    startActivity(mainIntent);
                    finish();
                }
            };

            Timer timer = new Timer();
            timer.schedule(task, getResources().getInteger(R.integer.activity_logo_screen_splash_screen_time));

        }else{
            Intent homeIntent = new Intent().setClass(ActivityLogoScreen.this,ActivityHome.class);
            startActivity(homeIntent);
            finish();
        }
    }
}

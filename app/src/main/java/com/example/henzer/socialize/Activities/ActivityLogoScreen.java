package com.example.henzer.socialize.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.eftimoff.androipathview.PathView;
import com.example.henzer.socialize.R;

import java.util.Timer;
import java.util.TimerTask;

public class ActivityLogoScreen extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e("ActivityLogoScreen","OnCreate()");
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
        timer.schedule(task,getResources().getInteger(R.integer.activity_logo_screen_splash_screen_time));
    }
}

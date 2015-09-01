package com.example.henzer.socialize.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Path;
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
                .delay(300)
                .duration(800)
                .start();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Intent mainIntent = new Intent().setClass(ActivityLogoScreen.this,ActivityMain.class);
                startActivity(mainIntent);
                finish();
            }
        };

        Timer timer = new Timer();
        timer.schedule(task,getResources().getInteger(R.integer.splash_screen_time));
    }
}

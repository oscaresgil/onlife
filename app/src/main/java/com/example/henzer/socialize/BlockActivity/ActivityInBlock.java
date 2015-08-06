package com.example.henzer.socialize.BlockActivity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.henzer.socialize.Activities.ActivityGoogleMaps;
import com.example.henzer.socialize.R;
import com.skyfishjy.library.RippleBackground;

import java.text.DecimalFormat;

public class ActivityInBlock extends Activity{

    private String user;
    private double latitude;
    private double longitude;
    private double distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_block);

        user = getIntent().getStringExtra("user");
        latitude = getIntent().getDoubleExtra("latitude",0.0);
        longitude = getIntent().getDoubleExtra("longitude",0.0);
        String message = getIntent().getStringExtra("message");
        distance = getIntent().getDoubleExtra("distance",0.0);

        TextView userView = (TextView) findViewById(R.id.ActivityInBlock_TextViewUser);
        TextView messageView = (TextView) findViewById(R.id.ActivityInBlock_TextViewMessage);
        TextView distanceView = (TextView) findViewById(R.id.ActivityInBlock_TextViewDistance);

        userView.setText(user);
        messageView.setText("\""+message+"\"");
        DecimalFormat df = new DecimalFormat("#.###");
        distanceView.setText(getResources().getString(R.string.distance)+" "+df.format(distance)+"m");

        final RippleBackground rippleBackground = (RippleBackground) findViewById(R.id.ActivityInBlock_BlockAnimation);
        rippleBackground.startRippleAnimation();
        ImageView imageView = (ImageView) findViewById(R.id.ActivityInBlock_ImageBlock);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rippleBackground.startRippleAnimation();
                finish();
            }
        });
    }
    public void showLocation(View v){
        Intent i = new Intent(ActivityInBlock.this, ActivityGoogleMaps.class);
        i.putExtra("user",user);
        i.putExtra("longitude",longitude);
        i.putExtra("latitude",latitude);
        startActivity(i);
    }
}

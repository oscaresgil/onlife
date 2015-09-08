package com.example.henzer.socialize.BlockActivity;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.applovin.adview.AppLovinInterstitialAd;
import com.applovin.sdk.AppLovinSdk;
import com.example.henzer.socialize.R;
import com.skyfishjy.library.RippleBackground;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class ActivityInBlock extends Activity{

    public final static String TAG = "ActivityInBlock";

    private String user;
    private String message;
    private String gifName;
    private boolean flagForAds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.flagForAds=true;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_block);

        user = getIntent().getStringExtra("user");
        message = getIntent().getStringExtra("message");
        gifName = getIntent().getStringExtra("gif");

        Log.i(TAG,"User: "+user+". Message: "+message+". GifName: "+gifName);

        TextView userView = (TextView) findViewById(R.id.ActivityInBlock_TextViewUser);
        TextView messageView = (TextView) findViewById(R.id.ActivityInBlock_TextViewMessage);

        userView.setText(user);
        messageView.setText("\""+message+"\"");
        if (!gifName.equals("")) {
            try {
                GifImageView gifImageView = (GifImageView) findViewById(R.id.ActivityInBlock_GifImageView);
                int resourceId = getResources().getIdentifier(gifName, "drawable", getPackageName());
                GifDrawable gif = new GifDrawable(getResources(), resourceId);
                gifImageView.setImageDrawable(gif);
                gifImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


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
        rippleBackground.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                rippleBackground.startRippleAnimation();
                finish();
                return false;
            }
        });
        rippleBackground.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                rippleBackground.startRippleAnimation();
                finish();
                return false;
            }
        });
        imageView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                rippleBackground.startRippleAnimation();
                finish();
                return false;
            }
        });

        // Inicializar el sdk de AppLovin
        AppLovinSdk.initializeSdk(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(flagForAds){
            if(AppLovinInterstitialAd.isAdReadyToDisplay(this)){
                // An ad is available to display.  It's safe to call show.
                AppLovinInterstitialAd.show(this);
                flagForAds=false;

            }
            else{
                Log.i("No funciona", "AppLovin");
                // No ad is available to display.  Perform failover logic...
            }

        }

    }
}

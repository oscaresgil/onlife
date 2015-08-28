package com.example.henzer.socialize.BlockActivity;


import android.app.Activity;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.henzer.socialize.R;
import com.skyfishjy.library.RippleBackground;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class ActivityInBlock extends Activity{

    private String user;
    private String message;
    private String gifName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_block);

        user = getIntent().getStringExtra("user");
        message = getIntent().getStringExtra("message");
        gifName = getIntent().getStringExtra("gif");

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
    }
    /*public void showLocation(View v){
        Intent i = new Intent(ActivityInBlock.this, ActivityGoogleMaps.class);
        i.putExtra("user",user);
        i.putExtra("longitude",longitude);
        i.putExtra("latitude",latitude);
        startActivity(i);
    }*/
}

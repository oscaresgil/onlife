package com.example.henzer.socialize.BlockActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.henzer.socialize.R;
import com.skyfishjy.library.RippleBackground;

public class ActivityInBlock extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.in_block_layout);

        String user = getIntent().getStringExtra("user");
        String message = getIntent().getStringExtra("message");
        double distance = getIntent().getDoubleExtra("distance",0.0);

        TextView userView = (TextView) findViewById(R.id.userName);
        TextView messageView = (TextView) findViewById(R.id.messageOnBlock);
        TextView distanceView = (TextView) findViewById(R.id.distance);

        userView.setText(user);
        messageView.setText("\""+message+"\"");
        distanceView.setText(getResources().getString(R.string.distance)+" "+distance+"m");

        final RippleBackground rippleBackground = (RippleBackground) findViewById(R.id.blockAnimation);
        rippleBackground.startRippleAnimation();
        ImageView imageView = (ImageView) findViewById(R.id.centerImage);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rippleBackground.startRippleAnimation();
                finish();
            }
        });
    }
}

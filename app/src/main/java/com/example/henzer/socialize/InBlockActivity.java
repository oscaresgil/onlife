package com.example.henzer.socialize;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.skyfishjy.library.RippleBackground;

/**
 * Created by Boris on 03/06/2015.
 */
public class InBlockActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.in_block_layout);
        String message = getIntent().getStringExtra("message");

        TextView textView = (TextView) findViewById(R.id.messageOnBlock);
        textView.setTextColor(getResources().getColor(R.color.black));
        textView.setText(message);
        textView.setTextSize(30);

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

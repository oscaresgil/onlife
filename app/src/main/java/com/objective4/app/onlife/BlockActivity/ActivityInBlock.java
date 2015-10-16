package com.objective4.app.onlife.BlockActivity;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.applovin.adview.AppLovinInterstitialAd;
import com.applovin.sdk.AppLovinSdk;
import com.objective4.app.onlife.Models.ModelMessages;
import com.objective4.app.onlife.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ActivityInBlock extends Activity{
    private int numPage;
    private String user;
    private String message;
    private String emoticonName;
    private List<ModelMessages> messages;
    private String[] colors = {"#1abc9c" ,"#2ecc71", "#3498db", "#9b59b6", "#34495e", "#16a085", "#27ae60", "#2980b9", "#8e44ad", "#2c3e50", "#f1c40f", "#e67e22", "#e74c3c", "#95a5a6", "#f39c12", "#d35400", "#c0392b", "#bdc3c7", "#7f8c8d"};
    private boolean flagForAds;

    private TextView textViewMessage,textViewUser,textViewNumber;
    private RelativeLayout relativeLayout;
    private ImageView emoticonImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppLovinSdk.initializeSdk(this);
        this.flagForAds=true;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_block);

        numPage = 0;
        textViewNumber = (TextView) findViewById(R.id.ActivityInBlock_TextViewNumberMessage);
        textViewUser = (TextView) findViewById(R.id.ActivityInBlock_TextViewName);
        textViewMessage = (TextView) findViewById(R.id.ActivityInBlock_TextViewMessage);
        emoticonImageView = (ImageView) findViewById(R.id.ActivityInBlock_EmoticonImageView);

        textViewNumber.setOnClickListener(new OnClickListenerMessage());
        textViewUser.setOnClickListener(new OnClickListenerMessage());
        textViewMessage.setOnClickListener(new OnClickListenerMessage());

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ActivityInBlock_LinearLayoutMessage);
        linearLayout.setOnClickListener(new OnClickListenerMessage());
        relativeLayout = (RelativeLayout) findViewById(R.id.ActivityInBlock_RelativeLayoutMain);
        relativeLayout.setOnClickListener(new OnClickListenerMessage());
        relativeLayout.setBackgroundColor(Color.parseColor(colors[new Random().nextInt(20)]));

        user = getIntent().getStringExtra("user");
        message = getIntent().getStringExtra("message");
        emoticonName = getIntent().getStringExtra("gif");

        messages = new ArrayList<>();
        messages.add(new ModelMessages(user, message, emoticonName, colors[0]));

        textViewNumber.setText(String.format("%d/%d", numPage + 1, messages.size()));
        textViewUser.setText(user);
        textViewMessage.setText(message);

        if (!"".equals(emoticonName)) {
            int resourceId = getResources().getIdentifier(emoticonName, "drawable", getPackageName());
            emoticonImageView.setImageDrawable(getResources().getDrawable(resourceId));
        }

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
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        user = intent.getStringExtra("user");
        message = intent.getStringExtra("message");
        emoticonName = intent.getStringExtra("gif");

        messages.add(new ModelMessages(user, message, emoticonName, colors[new Random().nextInt(20)]));
        textViewNumber.setText(String.format("%d/%d", numPage + 1, messages.size()));
    }

    class OnClickListenerMessage implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            numPage++;
            if (numPage>=messages.size()){
                finish();
                System.exit(0);
            }else {
                ModelMessages m = messages.get(numPage);
                textViewNumber.setText(String.format("%d/%d", numPage + 1, messages.size()));
                textViewUser.setText(m.getUserName());
                textViewMessage.setText(m.getMessage());
                relativeLayout.setBackgroundColor(Color.parseColor(m.getColor()));
            }
        }
    }
}

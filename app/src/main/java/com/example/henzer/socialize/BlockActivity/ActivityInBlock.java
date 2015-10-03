package com.example.henzer.socialize.BlockActivity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.applovin.adview.AppLovinInterstitialAd;
import com.applovin.sdk.AppLovinSdk;
import com.example.henzer.socialize.Adapters.AdapterMessageInBlock;
import com.example.henzer.socialize.Models.ModelMessages;
import com.example.henzer.socialize.R;

import java.util.ArrayList;
import java.util.List;

public class ActivityInBlock extends Activity{

    private ListView listView;
    private AdapterMessageInBlock adapter;
    private List<ModelMessages> messages;

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

        messages = new ArrayList<>();
        messages.add(new ModelMessages(user,message,gifName));

        listView = (ListView) findViewById(R.id.ActivityInBlock_ListView);
        adapter = new AdapterMessageInBlock(this,R.layout.layout_messages_in_block,messages);
        listView.setAdapter(adapter);

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
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        user = intent.getStringExtra("user");
        message = intent.getStringExtra("message");
        gifName = intent.getStringExtra("gif");

        adapter.add(new ModelMessages(user,message,gifName));
        adapter.notifyDataSetChanged();
    }

    public void removeActivity(View v){
        finish();
    }
}

package com.objective4.app.onlife.BlockActivity;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
    private int[] colors;
    private int[] colorsDarks;
    private TextView textViewMessage,textViewUser,textViewNumber;
    private RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_block);
        int alpha = 230;
        colors  = new int[]{Color.argb(alpha, 26, 188, 156), Color.argb(alpha,46, 204, 113), Color.argb(alpha,52, 152, 219), Color.argb(alpha,155, 89, 182), Color.argb(alpha,52, 73, 94), Color.argb(alpha,39, 174, 96), Color.argb(alpha,22, 160, 133), Color.argb(alpha,41, 128, 185), Color.argb(alpha,142, 68, 173), Color.argb(alpha,44, 62, 80), Color.argb(alpha,241, 196, 15), Color.argb(alpha,230, 126, 34), Color.argb(alpha,231, 76, 60), Color.argb(alpha,149, 165, 166), Color.argb(alpha,243, 156, 18), Color.argb(alpha,211, 84, 0), Color.argb(alpha,192, 57, 43), Color.argb(alpha,189, 195, 199), Color.argb(alpha,127, 140, 141)};
        alpha = 255;
        colorsDarks  = new int[]{Color.argb(alpha, 26, 188, 156), Color.argb(alpha,46, 204, 113), Color.argb(alpha,52, 152, 219), Color.argb(alpha,155, 89, 182), Color.argb(alpha,52, 73, 94), Color.argb(alpha,39, 174, 96), Color.argb(alpha,22, 160, 133), Color.argb(alpha,41, 128, 185), Color.argb(alpha,142, 68, 173), Color.argb(alpha,44, 62, 80), Color.argb(alpha,241, 196, 15), Color.argb(alpha,230, 126, 34), Color.argb(alpha,231, 76, 60), Color.argb(alpha,149, 165, 166), Color.argb(alpha,243, 156, 18), Color.argb(alpha,211, 84, 0), Color.argb(alpha,192, 57, 43), Color.argb(alpha,189, 195, 199), Color.argb(alpha,127, 140, 141)};

        numPage = 0;

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ActivityInBlock_LinearLayoutMessage);
        relativeLayout = (RelativeLayout) findViewById(R.id.ActivityInBlock_RelativeLayoutMain);

        textViewNumber = (TextView) findViewById(R.id.ActivityInBlock_TextViewNumberMessage);
        textViewNumber.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/oldrepublic.ttf"));
        textViewUser = (TextView) findViewById(R.id.ActivityInBlock_TextViewName);
        textViewUser.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/oldrepublic.ttf"));
        textViewMessage = (TextView) findViewById(R.id.ActivityInBlock_TextViewMessage);
        textViewMessage.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/oldrepublic.ttf"));
        ImageView emoticonImageView = (ImageView) findViewById(R.id.ActivityInBlock_EmoticonImageView);

        textViewNumber.setOnClickListener(new OnClickListenerMessage());
        textViewUser.setOnClickListener(new OnClickListenerMessage());
        textViewMessage.setOnClickListener(new OnClickListenerMessage());
        emoticonImageView.setOnClickListener(new OnClickListenerMessage());
        linearLayout.setOnClickListener(new OnClickListenerMessage());
        relativeLayout.setOnClickListener(new OnClickListenerMessage());

        int numb = new Random().nextInt(20);
        relativeLayout.setBackgroundColor(colors[numb]);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(colorsDarks[numb]);
        }

        user = getIntent().getStringExtra("user");
        message = getIntent().getStringExtra("message");
        emoticonName = getIntent().getStringExtra("gif");

        messages = new ArrayList<>();
        messages.add(new ModelMessages(user, message, emoticonName, colors[0], colorsDarks[0]));

        textViewNumber.setText(String.format("%d/%d", numPage + 1, messages.size()));
        textViewUser.setText(user);
        textViewMessage.setText(message);

        if (!"".equals(emoticonName)) {
            int resourceId = getResources().getIdentifier(emoticonName, "drawable", getPackageName());
            if (resourceId!=0) emoticonImageView.setImageDrawable(getResources().getDrawable(resourceId));
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        user = intent.getStringExtra("user");
        message = intent.getStringExtra("message");
        emoticonName = intent.getStringExtra("gif");

        int numb = new Random().nextInt(20);
        messages.add(new ModelMessages(user, message, emoticonName, colors[numb],colorsDarks[numb]));
        textViewNumber.setText(String.format("%d/%d", numPage + 1, messages.size()));
    }

    class OnClickListenerMessage implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            numPage++;
            if (numPage>=messages.size()){
                finish();
            }else {
                ModelMessages m = messages.get(numPage);
                textViewNumber.setText(String.format("%d/%d", numPage + 1, messages.size()));
                textViewUser.setText(m.getUserName());
                textViewMessage.setText(m.getMessage());
                relativeLayout.setBackgroundColor(m.getColor());
                if (Build.VERSION.SDK_INT >= 21) {
                    getWindow().setStatusBarColor(m.getColorDark());
                }
            }
        }
    }

}
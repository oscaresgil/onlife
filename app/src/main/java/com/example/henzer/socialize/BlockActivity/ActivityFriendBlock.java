package com.example.henzer.socialize.BlockActivity;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.henzer.socialize.Listeners.MessageFocusChangedListener;
import com.example.henzer.socialize.Listeners.TextWatcherListener;
import com.example.henzer.socialize.Models.ModelPerson;
import com.example.henzer.socialize.R;
import com.example.henzer.socialize.Tasks.TaskGPS;
import com.example.henzer.socialize.Tasks.TaskSendNotification;
import com.kenny.snackbar.SnackBar;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrPosition;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import net.steamcrafted.loadtoast.LoadToast;

import java.util.ArrayList;
import java.util.List;

import static com.example.henzer.socialize.Controller.StaticMethods.isNetworkAvailable;
import static com.example.henzer.socialize.Controller.StaticMethods.loadImagePath;

public class ActivityFriendBlock extends AppCompatActivity {
    public static final String TAG = "ActivityFriendBlock";
    private ModelPerson friend,actualUser;

    private MaterialEditText messageTextView;
    private TextView maxCharsView;
    private int maximumChars = 30, actualChar = 0;
    private GridView gridView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SlidrConfig config = new SlidrConfig.Builder().primaryColor(getResources().getColor(R.color.orange)).secondaryColor(getResources().getColor(R.color.orange_light)).position(SlidrPosition.LEFT).sensitivity(0.4f).build();
        Slidr.attach(this, config);

        setContentView(R.layout.activity_friend_block);

        Toolbar toolbar = (Toolbar) findViewById(R.id.ActivityFriendBlock_ToolBar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        friend = (ModelPerson)getIntent().getSerializableExtra("data");
        actualUser = (ModelPerson) getIntent().getSerializableExtra("actualuser");

        CollapsingToolbarLayout collapser = (CollapsingToolbarLayout) findViewById(R.id.ActivityFriendBlock_CollapsingToolBarLayout);
        collapser.setTitle(friend.getName());
        collapser.setCollapsedTitleTextColor(getResources().getColor(R.color.white));

        Picasso.with(this).load(loadImagePath(this, friend.getId())).into((ImageView) findViewById(R.id.ActivityFriendBlock_ImageViewContact));

        maxCharsView = (TextView) findViewById(R.id.ActivityFriendBlock_TextViewMaxCharacters);
        messageTextView = (MaterialEditText) findViewById(R.id.ActivityFriendBlock_EditTextMessage);
        messageTextView.setOnFocusChangeListener(new MessageFocusChangedListener(this,messageTextView));
        messageTextView.addTextChangedListener(new TextWatcherListener(this,maxCharsView));

        /*gridView = (GridView) findViewById(R.id.ActivityFriendBlock_GridLayout);

        final List<String> gifNames = setGifNames();

        gridView.setAdapter(new AdapterEmoticon(this,gifNames));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SnackBar.show(ActivityFriendBlock_2.this,gifNames.get(position));
            }
        });*/
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        InputMethodManager imm = (InputMethodManager)getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(messageTextView.getWindowToken(), 0);
        overridePendingTransition(R.animator.push_left_inverted, R.animator.push_right_inverted);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(messageTextView.getWindowToken(), 0);
        overridePendingTransition(R.animator.push_left_inverted, R.animator.push_right_inverted);
        return true;
    }

    private List<String> setGifNames(){
        List<String> gifNames = new ArrayList<>();
        for (int j=1; j<10; j++){
            gifNames.add("gif"+j);
        }
        return gifNames;
    }

    public void emoji(View view){
        if (gridView.getVisibility() == View.GONE){
            gridView.setVisibility(View.VISIBLE);
        }else{
            gridView.setVisibility(View.GONE);
        }
    }

    public void block(View view) {
        if (isNetworkAvailable(this)) {
            InputMethodManager imm = (InputMethodManager) getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(messageTextView.getWindowToken(), 0);

            if (actualChar <= 30) {
                try {
                    new TaskGPS(this,TAG).execute();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    SnackBar.show(ActivityFriendBlock.this, R.string.error);
                }
            } else {
                SnackBar.show(ActivityFriendBlock.this, R.string.message_max_characters, R.string.button_change_text_message, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        messageTextView.requestFocus();
                        InputMethodManager imm = (InputMethodManager) getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(messageTextView, 0);
                    }
                });
            }
        } else {

            SnackBar.show(ActivityFriendBlock.this, R.string.no_connection, R.string.button_change_connection, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                }
            });
        }
    }

    public void blockContact(Location location, LoadToast toast){
        SnackBar.show(ActivityFriendBlock.this, location.getLatitude() + "," + location.getLongitude());
        TaskSendNotification gcm = new TaskSendNotification(ActivityFriendBlock.this, actualUser.getName(), messageTextView.getText().toString(), location.getLatitude(), location.getLongitude(),toast);
        gcm.execute(friend);
    }

    /*class TextWatcherListener implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            actualChar = s.length();
            if (actualChar > 30) {
                maxCharsView.setTextColor(getResources().getColor(R.color.red));
                maxCharsView.setText(actualChar + "/" + maximumChars);
            } else {
                maxCharsView.setTextColor(getResources().getColor(R.color.black));
                maxCharsView.setText(actualChar + "/" + maximumChars);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }*/

    /*class MessageFocusChanged implements View.OnFocusChangeListener{
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {
                InputMethodManager imm = (InputMethodManager)getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(messageTextView.getWindowToken(), 0);
                messageTextView.setFocusable(false);
                messageTextView.clearFocus();
            }
        }
    }*/


}
package com.example.henzer.socialize.BlockActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.henzer.socialize.Models.Person;
import com.example.henzer.socialize.R;
import com.example.henzer.socialize.Tasks.TaskGPS;
import com.example.henzer.socialize.Tasks.TaskSendNotification;
import com.kenny.snackbar.SnackBar;
import com.melnykov.fab.FloatingActionButton;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrPosition;
import com.rengwuxian.materialedittext.MaterialEditText;

import net.steamcrafted.loadtoast.LoadToast;

import static com.example.henzer.socialize.Controller.StaticMethods.isNetworkAvailable;
import static com.example.henzer.socialize.Controller.StaticMethods.loadImage;

public class ActivityFriendBlock extends ActionBarActivity {
    public static final String TAG = "ActivityFriendBlock";
    private Person actualUser;
    private Person friend;
    private MaterialEditText messageTextView;
    private TextView maxCharsView;
    private int maximumChars = 30, actualChar = 0;
    private FloatingActionButton fab;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        friend = (Person)i.getSerializableExtra("data");
        actualUser = (Person) i.getSerializableExtra("actualuser");

        SlidrConfig config = new SlidrConfig.Builder()
                .primaryColor(getResources().getColor(R.color.orange))
                .secondaryColor(getResources().getColor(R.color.orange_light))
                .position(SlidrPosition.LEFT)
                .sensitivity(0.4f)
                .build();

        Slidr.attach(this, config);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_48dp);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.orange_light)));
        actionBar.setTitle((Html.fromHtml("<b><font color=\""+getResources().getColor(R.color.black)+"\">" + friend.getName()+ "</font></b>")));

        setContentView(R.layout.activity_friend_block);

        fab = (FloatingActionButton) findViewById(R.id.ActivityFriendBlock_ButtonBlock);
        Animation blinkAnim = AnimationUtils.loadAnimation(ActivityFriendBlock.this,R.anim.blink);
        fab.startAnimation(blinkAnim);
        fab.bringToFront();

        ImageView picture = (ImageView) findViewById(R.id.ActivityFriendBlock_ImageViewContact);
        picture.setImageBitmap(loadImage(this, friend.getId()));
        picture.setScaleType(ImageView.ScaleType.CENTER_CROP);

        maxCharsView = (TextView) findViewById(R.id.ActivityFriendBlock_TextViewMaxCharacters);
        messageTextView = (MaterialEditText) findViewById(R.id.ActivityFriendBlock_EditTextMessage);
        messageTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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
        });
        messageTextView.addTextChangedListener(new TextWatcher() {
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
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        RelativeLayout linearLayout = (RelativeLayout) findViewById(R.id.ActivityFriendBlock_RelativeLayoutContact);
        linearLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager)getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(messageTextView.getWindowToken(), 0);
                return false;
            }
        });
    }

    @Override
    protected void onDestroy() {
        InputMethodManager imm = (InputMethodManager)getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(messageTextView.getWindowToken(), 0);
        super.onDestroy();
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

    public void block(View view) {
        if (isNetworkAvailable(this)) {
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

}

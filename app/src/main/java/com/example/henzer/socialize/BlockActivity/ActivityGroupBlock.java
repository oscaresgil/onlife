package com.example.henzer.socialize.BlockActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.henzer.socialize.Activities.ActivityGroupInformation;
import com.example.henzer.socialize.Fragments.FragmentGroups;
import com.example.henzer.socialize.Tasks.TaskSendNotification;
import com.example.henzer.socialize.Tasks.TaskGPS;
import com.example.henzer.socialize.Models.Group;
import com.example.henzer.socialize.Models.Person;
import com.example.henzer.socialize.R;
import com.kenny.snackbar.SnackBar;
import com.melnykov.fab.FloatingActionButton;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrPosition;
import com.rengwuxian.materialedittext.MaterialEditText;

import net.steamcrafted.loadtoast.LoadToast;

import java.io.Serializable;
import java.util.List;

import static com.example.henzer.socialize.Controller.StaticMethods.loadImage;

public class ActivityGroupBlock extends ActionBarActivity {
    public static final String TAG = "ActivityGroupBlock";
    private String nameGroup;
    private int maximumChars = 30, actualChar = 0;

    private Person actualUser;
    private Group group;
    private List<Person> friendsInGroup;

    private ImageView avatar;
    private TextView maxCharsView;
    private MaterialEditText messageTextView;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SlidrConfig config = new SlidrConfig.Builder()
                .primaryColor(getResources().getColor(R.color.orange))
                .secondaryColor(getResources().getColor(R.color.orange_light))
                .position(SlidrPosition.LEFT)
                .sensitivity(0.4f)
                .build();

        Slidr.attach(this, config);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.orange_light)));
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_48dp);
        setContentView(R.layout.activity_group_block);

        fab = (FloatingActionButton) findViewById(R.id.ActivityGroupBlock_ButtonBlock);
        Animation blinkAnim = AnimationUtils.loadAnimation(ActivityGroupBlock.this, R.anim.blink);
        fab.startAnimation(blinkAnim);
        fab.bringToFront();

        Intent i = getIntent();
        nameGroup = i.getStringExtra("name");
        group = (Group) i.getSerializableExtra("data");
        actualUser = (Person) i.getSerializableExtra("user");

        avatar = (ImageView) findViewById(R.id.avatar_group);
        avatar.setImageBitmap(loadImage(this, group.getName()));
        avatar.setScaleType(ImageView.ScaleType.CENTER_CROP);

        friendsInGroup = group.getFriendsInGroup();
        actionBar.setTitle((Html.fromHtml("<b><font color=\"#000000\">" + nameGroup + "</font></b>")));

        maxCharsView = (TextView) findViewById(R.id.maxCharactersGroup);
        messageTextView = (MaterialEditText) findViewById(R.id.messageGroup);
        final Handler handler = new Handler();
        messageTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(messageTextView, InputMethodManager.SHOW_IMPLICIT);
                    }

                }, 500000);
            }
        });
        messageTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
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
            public void afterTextChanged(Editable s) {}
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.ActivityGroupBlock_ButtonBlock);
        fab.bringToFront();
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
    protected void onResume() {
        super.onResume();
        RelativeLayout linearLayout = (RelativeLayout) findViewById(R.id.layout_groups);
        linearLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_in_group, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.information_button){
            Intent intent = new Intent(this,ActivityGroupInformation.class);
            intent.putExtra("data",(Serializable)friendsInGroup);
            intent.putExtra("user",actualUser);
            startActivity(intent);
            overridePendingTransition(R.animator.push_right, R.animator.push_left);
        }
        else if(i == R.id.delete_group){
            new MaterialDialog.Builder(this)
                .title(R.string.delete)
                .content(R.string.really_delete)
                .positiveText(R.string.yes)
                .positiveColorRes(R.color.orange_light)
                .negativeText(R.string.no)
                .negativeColorRes(R.color.red)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        FragmentGroups.removeGroup(group);
                        dialog.dismiss();
                        dialog.cancel();

                        new MaterialDialog.Builder(ActivityGroupBlock.this)
                            .title("Group " + group.getName() + " deleted!")
                            .positiveText(R.string.yes)
                            .positiveColorRes(R.color.orange_light)
                            .callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    InputMethodManager imm = (InputMethodManager) getSystemService(
                                            Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(messageTextView.getWindowToken(), 0);
                                    finish();
                                    overridePendingTransition(R.animator.push_left_inverted, R.animator.push_right_inverted);
                                }
                            })
                            .show();
                    }
                }).show();
        }
        else {
            InputMethodManager imm = (InputMethodManager)getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(messageTextView.getWindowToken(), 0);
            finish();
            overridePendingTransition(R.animator.push_left_inverted, R.animator.push_right_inverted);
        }
        return true;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void block(View view){
        if (isNetworkAvailable()) {
            if (actualChar <= 30) {
                try {
                    new TaskGPS(this,TAG).execute();
                } catch (Exception ex) {
                    SnackBar.show(ActivityGroupBlock.this, R.string.error);
                }
            } else {
                SnackBar.show(ActivityGroupBlock.this, R.string.message_max_characters, R.string.button_change_text_message, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        messageTextView.requestFocus();
                        InputMethodManager imm = (InputMethodManager) getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(messageTextView, 0);
                    }
                });
            }
        }else{
            SnackBar.show(ActivityGroupBlock.this, R.string.no_connection, R.string.button_change_connection, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                }
            });
        }
    }

    public void blockGroup(Location location, LoadToast toast){
        SnackBar.show(ActivityGroupBlock.this, location.getLatitude() + "," + location.getLongitude());
        TaskSendNotification gcm = new TaskSendNotification(ActivityGroupBlock.this, actualUser.getName(), messageTextView.getText().toString(), location.getLatitude(), location.getLongitude(), toast);
        gcm.execute(friendsInGroup.toArray(new Person[friendsInGroup.size()]));
    }
}

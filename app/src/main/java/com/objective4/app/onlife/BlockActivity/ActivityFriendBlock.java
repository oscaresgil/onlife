package com.objective4.app.onlife.BlockActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.objective4.app.onlife.Models.ModelPerson;
import com.objective4.app.onlife.Models.ModelSessionData;
import com.objective4.app.onlife.R;
import com.objective4.app.onlife.Tasks.TaskSendNotification;
import com.objective4.app.onlife.Tasks.TaskSimpleImageDownload;

import net.soulwolf.widget.ratiolayout.widget.RatioImageView;

import java.util.Calendar;

import static com.objective4.app.onlife.Controller.StaticMethods.activateDeviceAdmin;
import static com.objective4.app.onlife.Controller.StaticMethods.animationEnd;
import static com.objective4.app.onlife.Controller.StaticMethods.checkDeviceAdmin;
import static com.objective4.app.onlife.Controller.StaticMethods.hideSoftKeyboard;
import static com.objective4.app.onlife.Controller.StaticMethods.imageInDisk;
import static com.objective4.app.onlife.Controller.StaticMethods.isNetworkAvailable;
import static com.objective4.app.onlife.Controller.StaticMethods.loadImage;
import static com.objective4.app.onlife.Controller.StaticMethods.makeSnackbar;
import static com.objective4.app.onlife.Controller.StaticMethods.showSoftKeyboard;

public class ActivityFriendBlock extends ActivityBlockBase<ModelPerson> {
    private RoundCornerProgressBar progressBar;
    private FloatingActionButton blockFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actualObject = ModelSessionData.getInstance().getFriends().get(actualObject.getId());
        collapser.setTitle(actualObject.getName());

        findViewById(R.id.ActivityBlockBase_RadioButton).setVisibility(View.VISIBLE);

        if (actualObject.getState().equals("I")){
            visibility.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_visibility_off));
        }else if(actualObject.getState().equals("A")){
            visibility.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.ic_action_visibility_on));
        }

        progressBar = (RoundCornerProgressBar) findViewById(R.id.ActivityBlockBase_ProgressBarTime);
        progressBar.setMax(getResources().getInteger(R.integer.block_time_remaining));

        RatioImageView avatar = (RatioImageView) findViewById(R.id.ActivityBlockBase_ImageViewContact);
        if (actualObject.refreshImageBig() || !imageInDisk(this,actualObject.getId()+"_"+getResources().getInteger(R.integer.adapter_contact_size_large))){
            if (!actualObject.refreshImage() && imageInDisk(this,actualObject.getId()+"_"+getResources().getInteger(R.integer.adapter_contact_size_little))) avatar.setImageBitmap(loadImage(this,actualObject.getId()+"_"+getResources().getInteger(R.integer.adapter_contact_size_little)));
            else avatar.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.loading_friend_icon));
            new TaskSimpleImageDownload(this,avatar,getResources().getInteger(R.integer.adapter_contact_size_large),progressBar).execute(actualObject);
            actualObject.setRefreshImageBig(false);
        }else{
            Bitmap bitmap = loadImage(this,actualObject.getId()+"_"+getResources().getInteger(R.integer.adapter_contact_size_large));
            //http://code.tutsplus.com/es/tutorials/coloring-android-apps-with-palette--cms-24096
            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {
                    if (palette.getDarkVibrantSwatch() != null) progressBar.setProgressBackgroundColor(palette.getDarkVibrantSwatch().getRgb());
                }
            });
            avatar.setImageBitmap(loadImage(this,actualObject.getId()+"_"+getResources().getInteger(R.integer.adapter_contact_size_large)));
        }

        messageTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    block(v);
                }
                return true;
            }
        });

        blockFab = (FloatingActionButton)findViewById(R.id.ActivityBlockBase_ButtonBlock);

        long actualTime = Calendar.getInstance().getTimeInMillis();
        if (actualTime - actualObject.getLastBlockedTime() < getResources().getInteger(R.integer.block_time_remaining) && !ModelSessionData.getInstance().getUser().getId().equals(actualObject.getId())) {
            setTimer();
        }else{
            progressBar.setProgress(getResources().getInteger(R.integer.block_time_remaining));
        }
    }

    @Override
    protected void onResume() {
        registerReceiver(broadcastReceiver, new IntentFilter("com.objective4.app.onlife.Fragments.Social.FragmentContacts"));
        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        hideSoftKeyboard(this, messageTextView);
        if (emoticonFlag){
            hideEmoticon();
        }else{
            finish();
            hideSoftKeyboard(this, messageTextView);
            animationEnd(this);
        }
        return super.onOptionsItemSelected(item);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            String state = extras.getString("state");
            String id = extras.getString("id");
            if (actualObject.getId().equals(id)){
                actualObject.setState(state);
                if ("I".equals(state)){
                    visibility.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_visibility_off));
                }else if("A".equals(state)){
                    visibility.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.ic_action_visibility_on));
                }
            }
        }
    };

    public void block(View view) {
        hideSoftKeyboard(this,messageTextView);
        if (isNetworkAvailable(this)) {
            if (listenerTextWatcher.getActualChar() < getResources().getInteger(R.integer.message_max_chars)+1) {
                boolean devAdmin = checkDeviceAdmin(this);
                if (actualObject.getState().equals("A") && devAdmin) {
                    try {
                        new TaskSendNotification(ActivityFriendBlock.this, messageTextView.getText().toString(), emoticonName).execute(actualObject);
                        messageTextView.setText("");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        makeSnackbar(ActivityFriendBlock.this, messageTextView, R.string.error, Snackbar.LENGTH_SHORT);
                    }
                }else if(!devAdmin){
                    activateDeviceAdmin(this);
                }else{
                    makeSnackbar(this, messageTextView, R.string.friend_inactive, Snackbar.LENGTH_SHORT);
                }
            } else {
                makeSnackbar(this, messageTextView, R.string.message_max_characters, Snackbar.LENGTH_SHORT, R.string.button_change_text_message, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        messageTextView.requestFocus();
                        showSoftKeyboard(ActivityFriendBlock.this, messageTextView);
                    }
                });
            }
        } else {
            makeSnackbar(this, messageTextView, R.string.no_connection, Snackbar.LENGTH_LONG, R.string.button_change_connection, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                }
            });
        }
    }

    public void setTimer() {
        blockFab.animate().alpha(0).setInterpolator(new AccelerateInterpolator()).start();
        nestedScrollView.setNestedScrollingEnabled(false);
        nestedScrollView.setSmoothScrollingEnabled(false);
        blockFab.setClickable(false);
        emoticonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeSnackbar(ActivityFriendBlock.this,v,R.string.dialog_please_wait,Snackbar.LENGTH_LONG);
            }
        });

        long actualTime = Calendar.getInstance().getTimeInMillis();
        int time = (int) (actualTime - actualObject.getLastBlockedTime());
        int dif = getResources().getInteger(R.integer.block_time_remaining) - time;
        progressBar.setProgress(time);

        new CountDownTimer(dif, 1000) {
            public void onTick(long millisUntilFinished) {
                int actual = (int) millisUntilFinished;
                progressBar.setProgress(getResources().getInteger(R.integer.block_time_remaining) - actual);
            }

            public void onFinish() {
                progressBar.setProgress(getResources().getInteger(R.integer.block_time_remaining));
                blockFab.animate().alpha(1).setInterpolator(new DecelerateInterpolator()).start();
                nestedScrollView.setNestedScrollingEnabled(true);
                nestedScrollView.setSmoothScrollingEnabled(true);
                blockFab.setClickable(true);
                emoticonButton.setOnClickListener(new EmoticonClick());
            }
        }.start();
    }
}
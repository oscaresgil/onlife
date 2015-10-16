package com.objective4.app.onlife.BlockActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.kenny.snackbar.SnackBar;
import com.objective4.app.onlife.Models.ModelPerson;
import com.objective4.app.onlife.Models.ModelSessionData;
import com.objective4.app.onlife.R;
import com.objective4.app.onlife.Tasks.TaskSendNotification;
import com.objective4.app.onlife.Tasks.TaskSimpleImageDownload;

import net.soulwolf.widget.ratiolayout.widget.RatioImageView;

import static com.objective4.app.onlife.Controller.StaticMethods.activateDeviceAdmin;
import static com.objective4.app.onlife.Controller.StaticMethods.animationEnd;
import static com.objective4.app.onlife.Controller.StaticMethods.checkDeviceAdmin;
import static com.objective4.app.onlife.Controller.StaticMethods.hideSoftKeyboard;
import static com.objective4.app.onlife.Controller.StaticMethods.imageInDisk;
import static com.objective4.app.onlife.Controller.StaticMethods.isNetworkAvailable;
import static com.objective4.app.onlife.Controller.StaticMethods.loadImage;
import static com.objective4.app.onlife.Controller.StaticMethods.showSoftKeyboard;

public class ActivityFriendBlock extends ActivityBlockBase<ModelPerson> {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        collapser.setTitle(actualObject.getName());

        findViewById(R.id.ActivityBlockBase_RadioButton).setVisibility(View.VISIBLE);

        int position = getIntent().getIntExtra("position",-1);

        if (actualObject.getState().equals("I")){
            visibility.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_visibility_off_2));
        }else if(actualObject.getState().equals("A")){
            visibility.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.ic_action_visibility_on));
        }

        RatioImageView avatar = (RatioImageView) findViewById(R.id.ActivityBlockBase_ImageViewContact);
        if (actualObject.refreshImageBig() || !imageInDisk(this,actualObject.getId()+"_"+getResources().getInteger(R.integer.adapter_contact_size_large))){
            if (imageInDisk(this,actualObject.getId()+"_"+getResources().getInteger(R.integer.adapter_contact_size_little)))
                avatar.setImageBitmap(loadImage(this,actualObject.getId()+"_"+getResources().getInteger(R.integer.adapter_contact_size_little)));
            new TaskSimpleImageDownload(this,avatar,getResources().getInteger(R.integer.adapter_contact_size_large)).execute(actualObject);
            ModelSessionData.getInstance().getFriends().get(position).setRefreshImageBig(false);
        }else{
            avatar.setImageBitmap(loadImage(this,actualObject.getId()+"_"+getResources().getInteger(R.integer.adapter_contact_size_large)));
        }

        messageTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE){
                    block(v);
                }
                return true;
            }
        });
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
                    visibility.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_visibility_off_2));
                }else if("A".equals(state)){
                    visibility.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.ic_action_visibility_on));
                }
            }
        }
    };

    public void block(View view) {
        if (isNetworkAvailable(this)) {
            hideSoftKeyboard(this,messageTextView);
            if (listenerTextWatcher.getActualChar() < 31) {
                boolean devAdmin = checkDeviceAdmin(this);
                if (actualObject.getState().equals("A") && devAdmin) {
                    try {
                        new TaskSendNotification(ActivityFriendBlock.this, actualUser.getName(), messageTextView.getText().toString(), emoticonName).execute(actualObject);
                        messageTextView.setText("");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        SnackBar.show(ActivityFriendBlock.this, R.string.error);
                    }
                }else if(!devAdmin){
                    activateDeviceAdmin(this);
                }else{
                    SnackBar.show(ActivityFriendBlock.this,R.string.friend_inactive);
                }
            } else {
                SnackBar.show(ActivityFriendBlock.this, R.string.message_max_characters, R.string.button_change_text_message, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        messageTextView.requestFocus();
                        showSoftKeyboard(ActivityFriendBlock.this,messageTextView);
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
}
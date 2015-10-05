package com.objective4.app.onlife.BlockActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.objective4.app.onlife.Adapters.AdapterEmoticon;
import com.objective4.app.onlife.Listeners.ListenerMessageFocusChanged;
import com.objective4.app.onlife.Listeners.ListenerTextWatcher;
import com.objective4.app.onlife.Models.ModelPerson;
import com.objective4.app.onlife.R;
import com.objective4.app.onlife.Tasks.TaskSendNotification;
import com.kenny.snackbar.SnackBar;
import com.rengwuxian.materialedittext.MaterialEditText;

import net.soulwolf.widget.ratiolayout.widget.RatioImageView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

import static com.objective4.app.onlife.Controller.StaticMethods.activateDeviceAdmin;
import static com.objective4.app.onlife.Controller.StaticMethods.animationEnd;
import static com.objective4.app.onlife.Controller.StaticMethods.checkDeviceAdmin;
import static com.objective4.app.onlife.Controller.StaticMethods.hideSoftKeyboard;
import static com.objective4.app.onlife.Controller.StaticMethods.isNetworkAvailable;
import static com.objective4.app.onlife.Controller.StaticMethods.loadImage;
import static com.objective4.app.onlife.Controller.StaticMethods.setGifNames;
import static com.objective4.app.onlife.Controller.StaticMethods.setSlidr;
import static com.objective4.app.onlife.Controller.StaticMethods.showSoftKeyboard;

public class ActivityFriendBlock extends AppCompatActivity {
    private ModelPerson friend,actualUser;

    private ImageView visibility;
    private MaterialEditText messageTextView;
    private TextView maxCharsView;
    private GridView gridView;

    private ListenerTextWatcher listenerTextWatcher;
    private String gifName="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setSlidr(this);

        setContentView(R.layout.activity_friend_block);

        Toolbar toolbar = (Toolbar) findViewById(R.id.ActivityFriendBlock_ToolBar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        }

        friend = (ModelPerson)getIntent().getSerializableExtra("data");
        actualUser = (ModelPerson) getIntent().getSerializableExtra("actualuser");

        CollapsingToolbarLayout collapser = (CollapsingToolbarLayout) findViewById(R.id.ActivityFriendBlock_CollapsingToolBarLayout);
        collapser.setTitle(friend.getName());
        collapser.setCollapsedTitleTextColor(getResources().getColor(R.color.white));
        collapser.setExpandedTitleColor(getResources().getColor(R.color.white));

        NestedScrollView nestedScrollView = (NestedScrollView) findViewById(R.id.ActivityFriendBlock_ScrollView);
        nestedScrollView.setNestedScrollingEnabled(false);

        visibility= (ImageView) findViewById(R.id.ActivityFriendBlock_RadioButton);
        if (friend.getState().equals("I")){
            visibility.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_visibility_off_2));
        }else if(friend.getState().equals("A")){
            visibility.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.ic_action_visibility_on));
        }

        RatioImageView avatar = (RatioImageView) findViewById(R.id.ActivityFriendBlock_ImageViewContact);
        avatar.setImageBitmap(loadImage(this,friend.getId()+"_"+getResources().getInteger(R.integer.adapter_contact_size_large)));

        maxCharsView = (TextView) findViewById(R.id.ActivityFriendBlock_TextViewMaxCharacters);
        messageTextView = (MaterialEditText) findViewById(R.id.ActivityFriendBlock_EditTextMessage);
        messageTextView.setOnFocusChangeListener(new ListenerMessageFocusChanged(this,messageTextView));
        listenerTextWatcher = new ListenerTextWatcher(this,maxCharsView,messageTextView);

        messageTextView.addTextChangedListener(listenerTextWatcher);

        gridView = (GridView) findViewById(R.id.ActivityFriendBlock_GridLayout);

        FloatingActionButton fabGif = (FloatingActionButton) findViewById(R.id.ActivityFriendBlock_FABEmoticon);
        fabGif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gridView.getVisibility() != View.VISIBLE) {
                    gridView.setVisibility(View.VISIBLE);
                    final List<String> gifNames = setGifNames();

                    gridView.setAdapter(new AdapterEmoticon(ActivityFriendBlock.this,gifNames));
                    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            try{
                                gifName = gifNames.get(position);
                                final GifImageView gifImageView = (GifImageView) findViewById(R.id.ActivityFriendBlock_GifImage);
                                int resourceId = getResources().getIdentifier(gifName, "drawable", getPackageName());
                                GifDrawable gif = new GifDrawable(getResources(), resourceId);
                                gifImageView.setImageDrawable(gif);
                                gifImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

                                gifImageView.setOnLongClickListener(new View.OnLongClickListener() {
                                    @Override
                                    public boolean onLongClick(View v) {
                                        gifImageView.setImageBitmap(null);
                                        gifName = "";
                                        return false;
                                    }
                                });
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            gridView.setVisibility(View.GONE);
                        }
                    });

                }else{
                    gridView.setVisibility(View.GONE);
                    List<String> array = new ArrayList<>();
                    gridView.setAdapter(new AdapterEmoticon(ActivityFriendBlock.this,array));
                }
            }
        });
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            String state = extras.getString("state");
            String id = extras.getString("id");
            if (friend.getId().equals(id)){
                friend.setState(state);
                if (state.equals("I")){
                    visibility.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_visibility_off_2));
                }else if(state.equals("A")){
                    visibility.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.ic_action_visibility_on));
                }
            }
        }
    };

    @Override
    protected void onResume() {
        registerReceiver(broadcastReceiver, new IntentFilter("com.objective4.app.onlife.Fragments.FragmentContacts"));
        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        hideSoftKeyboard(this, messageTextView);
        animationEnd(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        hideSoftKeyboard(this,messageTextView);
        finish();
        animationEnd(this);
        return super.onOptionsItemSelected(item);
    }

    public void block(View view) {
        if (isNetworkAvailable(this)) {
            hideSoftKeyboard(this,messageTextView);
            if (listenerTextWatcher.getActualChar() <= 30) {
                boolean devAdmin = checkDeviceAdmin(this);
                if (friend.getState().equals("A") && devAdmin) {
                    try {
                        long actualTime = Calendar.getInstance().getTimeInMillis();
                        if (actualTime - friend.getLastBlockedTime() > getResources().getInteger(R.integer.block_time_remaining)){
                            new TaskSendNotification(ActivityFriendBlock.this, actualUser.getName(), messageTextView.getText().toString(), gifName).execute(friend);
                            friend.setLastBlockedTime(actualTime);
                        }else{
                            SnackBar.show(ActivityFriendBlock.this,getResources().getString(R.string.toast_not_time_yet)+" "+((getResources().getInteger(R.integer.block_time_remaining)-(actualTime - friend.getLastBlockedTime()))/1000)+" s");
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        SnackBar.show(ActivityFriendBlock.this, R.string.error);
                    }
                }else if(!devAdmin){
                    SnackBar.show(this,R.string.in_block_device_admin_not_activated);
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
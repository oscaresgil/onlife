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
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.objective4.app.onlife.Adapters.AdapterFragmentEmoticon;
import com.objective4.app.onlife.Listeners.ListenerMessageFocusChanged;
import com.objective4.app.onlife.Listeners.ListenerTextWatcher;
import com.objective4.app.onlife.Models.ModelPerson;
import com.objective4.app.onlife.R;
import com.objective4.app.onlife.Tasks.TaskSendNotification;
import com.kenny.snackbar.SnackBar;
import com.objective4.app.onlife.Tasks.TaskSimpleImageDownload;
import com.r0adkll.slidr.model.SlidrInterface;
import com.rengwuxian.materialedittext.MaterialEditText;

import net.soulwolf.widget.ratiolayout.widget.RatioImageView;

import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;

import static com.objective4.app.onlife.Controller.StaticMethods.activateDeviceAdmin;
import static com.objective4.app.onlife.Controller.StaticMethods.animationEnd;
import static com.objective4.app.onlife.Controller.StaticMethods.checkDeviceAdmin;
import static com.objective4.app.onlife.Controller.StaticMethods.getRelativeTop;
import static com.objective4.app.onlife.Controller.StaticMethods.hideSoftKeyboard;
import static com.objective4.app.onlife.Controller.StaticMethods.imageInDisk;
import static com.objective4.app.onlife.Controller.StaticMethods.isNetworkAvailable;
import static com.objective4.app.onlife.Controller.StaticMethods.loadImage;
import static com.objective4.app.onlife.Controller.StaticMethods.setSlidr;
import static com.objective4.app.onlife.Controller.StaticMethods.showSoftKeyboard;

public class ActivityFriendBlock extends AppCompatActivity {
    private ModelPerson friend,actualUser;

    private NestedScrollView nestedScrollView;
    private ImageView visibility;
    private MaterialEditText messageTextView;
    private TextView maxCharsView;
    private LinearLayout emoticonLayout;
    private MaterialTabHost tabHost;
    private ViewPager viewPager;
    private SlidrInterface slidrInterface;
    private boolean emoticonFlag=false;
    private CollapsingToolbarLayout collapser;

    private ListenerTextWatcher listenerTextWatcher;
    private String gifName="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        slidrInterface = setSlidr(this);
        setContentView(R.layout.activity_friend_block);

        Toolbar toolbar = (Toolbar) findViewById(R.id.ActivityFriendBlock_ToolBar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        }

        friend = (ModelPerson)getIntent().getSerializableExtra("data");
        actualUser = (ModelPerson) getIntent().getSerializableExtra("actualuser");
        emoticonFlag = false;

        RatioImageView avatar = (RatioImageView) findViewById(R.id.ActivityFriendBlock_ImageViewContact);
        if (friend.refreshImageBig() || !imageInDisk(this,friend.getId()+"_"+getResources().getInteger(R.integer.adapter_contact_size_large))){
            if (imageInDisk(this,friend.getId()+"_"+getResources().getInteger(R.integer.adapter_contact_size_little)))
                avatar.setImageBitmap(loadImage(this,friend.getId()+"_"+getResources().getInteger(R.integer.adapter_contact_size_little)));
            friend.setRefreshImageBig(false);
            new TaskSimpleImageDownload(this,avatar,getResources().getInteger(R.integer.adapter_contact_size_large)).execute(friend);
        }else{
            avatar.setImageBitmap(loadImage(this,friend.getId()+"_"+getResources().getInteger(R.integer.adapter_contact_size_large)));
        }

        emoticonLayout = (LinearLayout) findViewById(R.id.ActivityFriendBlock_LayoutEmoticon);
        tabHost = (MaterialTabHost) findViewById(R.id.ActivityFriendBlock_TabHost);
        viewPager = (ViewPager) findViewById(R.id.ActivityFriendBlock_ViewPager);

        collapser = (CollapsingToolbarLayout) findViewById(R.id.ActivityFriendBlock_CollapsingToolBarLayout);
        collapser.setTitle(friend.getName());
        collapser.setCollapsedTitleTextColor(getResources().getColor(R.color.white));
        collapser.setExpandedTitleColor(getResources().getColor(R.color.white));

        nestedScrollView = (NestedScrollView) findViewById(R.id.ActivityFriendBlock_ScrollView);

        visibility= (ImageView) findViewById(R.id.ActivityFriendBlock_RadioButton);
        if (friend.getState().equals("I")){
            visibility.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_visibility_off_2));
        }else if(friend.getState().equals("A")){
            visibility.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.ic_action_visibility_on));
        }

        maxCharsView = (TextView) findViewById(R.id.ActivityFriendBlock_TextViewMaxCharacters);
        messageTextView = (MaterialEditText) findViewById(R.id.ActivityFriendBlock_EditTextMessage);
        messageTextView.setOnFocusChangeListener(new ListenerMessageFocusChanged(this,messageTextView));
        listenerTextWatcher = new ListenerTextWatcher(this, maxCharsView,messageTextView);

        messageTextView.addTextChangedListener(listenerTextWatcher);

        ImageButton emoticonButton = (ImageButton) findViewById(R.id.ActivityFriendBlock_EmoticonButton);
        emoticonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emoticonLayout.getVisibility() == View.GONE) {
                    nestedScrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            nestedScrollView.smoothScrollTo(0, getRelativeTop(tabHost));
                        }
                    });
                    showEmoticon();
                } else {
                    hideEmoticon();
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
                if ("I".equals(state)){
                    visibility.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_visibility_off_2));
                }else if("A".equals(state)){
                    visibility.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.ic_action_visibility_on));
                }
            }
        }
    };

    protected void setEmoticonTab(){
        AdapterFragmentEmoticon adapter = new AdapterFragmentEmoticon(getSupportFragmentManager(),this);
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                tabHost.setSelectedNavigationItem(position);
            }
        });

        for (int i=0; i<adapter.getCount(); i++){
            tabHost.addTab(tabHost.newTab().setIcon(getResources().getDrawable(R.drawable.ic_insert_emoticon_black_24dp)).setTabListener(new MaterialTabListener() {
                @Override
                public void onTabSelected(MaterialTab tab) {
                    viewPager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabReselected(MaterialTab tab) {
                }

                @Override
                public void onTabUnselected(MaterialTab tab) {

                }
            }));
        }
    }

    public void setImage(String emoticonName){
        hideEmoticon();
        ImageView emoticon = (ImageView) findViewById(R.id.ActivityFriendBlock_EmoticonImage);
        int resourceId = getResources().getIdentifier(emoticonName, "drawable", getPackageName());
        emoticon.setImageDrawable(getResources().getDrawable(resourceId));
    }

    public void showEmoticon(){
        nestedScrollView.setNestedScrollingEnabled(true);
        hideSoftKeyboard(ActivityFriendBlock.this, messageTextView);
        slidrInterface.lock();
        setEmoticonTab();
        emoticonFlag=true;
        emoticonLayout.setVisibility(View.VISIBLE);
        nestedScrollView.fullScroll(ScrollView.FOCUS_DOWN);
        nestedScrollView.post(new Runnable() {
            @Override
            public void run() {
                nestedScrollView.smoothScrollTo(0, getRelativeTop(tabHost));
            }
        });
    }

    public void hideEmoticon(){
        emoticonFlag = false;
        slidrInterface.unlock();
        nestedScrollView.setSmoothScrollingEnabled(true);
        nestedScrollView.fullScroll(ScrollView.FOCUS_UP);
        emoticonLayout.setVisibility(View.GONE);
    }

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
        if (emoticonFlag){
            hideEmoticon();
        }else{
            super.onBackPressed();
            hideSoftKeyboard(this, messageTextView);
            animationEnd(this);
        }
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

    public void block(View view) {
        if (isNetworkAvailable(this)) {
            hideSoftKeyboard(this,messageTextView);
            if (listenerTextWatcher.getActualChar() < 31) {
                boolean devAdmin = checkDeviceAdmin(this);
                if (friend.getState().equals("A") && devAdmin) {
                    try {
                        new TaskSendNotification(ActivityFriendBlock.this, actualUser.getName(), messageTextView.getText().toString(), gifName).execute(friend);
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
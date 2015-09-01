package com.example.henzer.socialize.BlockActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.henzer.socialize.Activities.ActivityLogoScreen;
import com.example.henzer.socialize.Adapters.AdapterEmoticon;
import com.example.henzer.socialize.Listeners.MessageFocusChangedListener;
import com.example.henzer.socialize.Listeners.TextWatcherListener;
import com.example.henzer.socialize.Models.ModelPerson;
import com.example.henzer.socialize.R;
import com.example.henzer.socialize.Tasks.TaskSendNotification;
import com.kenny.snackbar.SnackBar;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrPosition;
import com.rengwuxian.materialedittext.MaterialEditText;

import net.soulwolf.widget.ratiolayout.widget.RatioImageView;

import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

import static com.example.henzer.socialize.Controller.StaticMethods.animationEnd;
import static com.example.henzer.socialize.Controller.StaticMethods.hideSoftKeyboard;
import static com.example.henzer.socialize.Controller.StaticMethods.isNetworkAvailable;
import static com.example.henzer.socialize.Controller.StaticMethods.loadImage;
import static com.example.henzer.socialize.Controller.StaticMethods.setGifNames;
import static com.example.henzer.socialize.Controller.StaticMethods.showSoftKeyboard;

public class ActivityFriendBlock extends AppCompatActivity {
    public static final String TAG = "ActivityFriendBlock";
    private ModelPerson friend,actualUser;

    private MaterialEditText messageTextView;
    private TextView maxCharsView;
    private GridView gridView;

    private TextWatcherListener textWatcherListener;
    private String gifName="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "onCreate()");

        SlidrConfig config = new SlidrConfig.Builder()
                .primaryColor(getResources().getColor(R.color.orange))
                .secondaryColor(getResources().getColor(R.color.orange_light))
                .position(SlidrPosition.LEFT)
                .sensitivity(0.4f)
                .build();
        Slidr.attach(this, config);

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

        RatioImageView avatar = (RatioImageView) findViewById(R.id.ActivityFriendBlock_ImageViewContact);
        avatar.setImageBitmap(loadImage(this,friend.getId()));

        maxCharsView = (TextView) findViewById(R.id.ActivityFriendBlock_TextViewMaxCharacters);
        messageTextView = (MaterialEditText) findViewById(R.id.ActivityFriendBlock_EditTextMessage);
        messageTextView.setOnFocusChangeListener(new MessageFocusChangedListener(this,messageTextView));
        textWatcherListener = new TextWatcherListener(this,maxCharsView,messageTextView);

        messageTextView.addTextChangedListener(textWatcherListener);

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.i(TAG, "onBackPressed()");
        hideSoftKeyboard(this,messageTextView);
        animationEnd(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "onOptionsItemSelected(): Back button");
        hideSoftKeyboard(this,messageTextView);
        finish();
        animationEnd(this);
        return super.onOptionsItemSelected(item);
    }

    public void block(View view) {
        Intent i = new Intent(this,ActivityLogoScreen.class);
        startActivity(i);
        if (isNetworkAvailable(this)) {
            hideSoftKeyboard(this,messageTextView);
            if (textWatcherListener.getActualChar() <= 30) {
                try {
                    Log.i(TAG, "Block: "+friend.getName()+". Actual User: "+actualUser.getName()+" Message: "+messageTextView.getText().toString()+". Gif: "+gifName);
                    new TaskSendNotification(ActivityFriendBlock.this, actualUser.getName(), messageTextView.getText().toString(),gifName).execute(friend);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    SnackBar.show(ActivityFriendBlock.this, R.string.error);
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
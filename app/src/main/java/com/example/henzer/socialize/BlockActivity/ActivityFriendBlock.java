package com.example.henzer.socialize.BlockActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

import static com.example.henzer.socialize.Controller.StaticMethods.isNetworkAvailable;
import static com.example.henzer.socialize.Controller.StaticMethods.loadImagePath;
import static com.example.henzer.socialize.Controller.StaticMethods.setGifNames;

public class ActivityFriendBlock extends AppCompatActivity {
    public static final String TAG = "ActivityFriendBlock";
    private ModelPerson friend,actualUser;

    private MaterialEditText messageTextView;
    private TextView maxCharsView;
    private int actualChar = 0;
    private GridView gridView;

    private TextWatcherListener textWatcherListener;

    private String gifName="";

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
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        }

        friend = (ModelPerson)getIntent().getSerializableExtra("data");
        actualUser = (ModelPerson) getIntent().getSerializableExtra("actualuser");

        CollapsingToolbarLayout collapser = (CollapsingToolbarLayout) findViewById(R.id.ActivityFriendBlock_CollapsingToolBarLayout);
        collapser.setTitle(friend.getName());
        collapser.setCollapsedTitleTextColor(getResources().getColor(R.color.white));
        collapser.setExpandedTitleColor(getResources().getColor(R.color.white));

        Picasso.with(this).load(loadImagePath(this, friend.getId())).into((ImageView) findViewById(R.id.ActivityFriendBlock_ImageViewContact));

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
            InputMethodManager imm = (InputMethodManager) getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(messageTextView.getWindowToken(), 0);

            if (textWatcherListener.getActualChar() <= 30) {
                try {
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
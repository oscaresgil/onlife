package com.objective4.app.onlife.BlockActivity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.objective4.app.onlife.Adapters.AdapterFragmentEmoticon;
import com.objective4.app.onlife.Listeners.ListenerTextWatcher;
import com.objective4.app.onlife.Models.ModelPerson;
import com.objective4.app.onlife.Models.ModelSessionData;
import com.objective4.app.onlife.R;
import com.r0adkll.slidr.model.SlidrInterface;

import static com.objective4.app.onlife.Controller.StaticMethods.animationEnd;
import static com.objective4.app.onlife.Controller.StaticMethods.hideSoftKeyboard;
import static com.objective4.app.onlife.Controller.StaticMethods.setSlidr;

public class ActivityBlockBase<T> extends AppCompatActivity {
    protected ModelPerson actualUser;
    protected T actualObject;

    protected CollapsingToolbarLayout collapser;
    protected NestedScrollView nestedScrollView;
    protected LinearLayout emoticonLayout;

    protected ImageView visibility;
    protected ImageView emoticon;

    protected EditText messageTextView;
    protected ListenerTextWatcher listenerTextWatcher;
    protected TextView maxCharsView;
    protected ImageButton emoticonButton;

    protected boolean emoticonFlag = false;
    protected TabLayout tabHost;
    protected ViewPager viewPager;
    protected SlidrInterface slidrInterface;
    protected String emoticonName="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block_base);

        slidrInterface = setSlidr(this);
        setSupportActionBar((Toolbar) findViewById(R.id.ActivityBlockBase_ToolBar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        }

        actualUser = ModelSessionData.getInstance().getUser();
        actualObject = (T) getIntent().getSerializableExtra("data");
        emoticonFlag = false;

        collapser = (CollapsingToolbarLayout) findViewById(R.id.ActivityBlockBase_CollapsingToolBarLayout);
        collapser.setCollapsedTitleTextColor(getResources().getColor(R.color.white));
        collapser.setExpandedTitleColor(getResources().getColor(R.color.white));
        nestedScrollView = (NestedScrollView) findViewById(R.id.ActivityBlockBase_ScrollView);

        visibility = (ImageView) findViewById(R.id.ActivityBlockBase_RadioButton);
        emoticon = (ImageView) findViewById(R.id.ActivityBlockBase_EmoticonImage);

        emoticonLayout = (LinearLayout) findViewById(R.id.ActivityBlockBase_LayoutEmoticon);
        tabHost = (TabLayout) findViewById(R.id.ActivityBlockBase_TabHost);
        viewPager = (ViewPager) findViewById(R.id.ActivityBlockBase_ViewPager);
        viewPager.setAdapter(null);

        maxCharsView = (TextView) findViewById(R.id.ActivityBlockBase_TextViewMaxCharacters);
        maxCharsView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/oldrepublic.ttf"));
        messageTextView = (EditText) findViewById(R.id.ActivityBlockBase_EditTextMessage);
        messageTextView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/oldrepublic.ttf"));
        listenerTextWatcher = new ListenerTextWatcher(this, maxCharsView, messageTextView);
        messageTextView.addTextChangedListener(listenerTextWatcher);

        emoticonButton = (ImageButton) findViewById(R.id.ActivityBlockBase_EmoticonButton);
        emoticonButton.setOnClickListener(new EmoticonClick());
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

    protected void setEmoticonTab(){
        if (viewPager.getAdapter() == null){

            AdapterFragmentEmoticon adapter = new AdapterFragmentEmoticon(getSupportFragmentManager(),this);
            viewPager.setAdapter(adapter);
            tabHost.setupWithViewPager(viewPager);
        }
    }

    public void setImage(String emoticonName){
        this.emoticonName = emoticonName;
        hideEmoticon();
        int resourceId  = getResources().getIdentifier(emoticonName, "drawable", getPackageName());
        emoticon.setImageDrawable(ContextCompat.getDrawable(this,resourceId));
    }

    protected void showEmoticon(){
        nestedScrollView.setNestedScrollingEnabled(true);
        hideSoftKeyboard(this, messageTextView);
        slidrInterface.lock();
        setEmoticonTab();
        emoticonFlag=true;

        emoticonLayout.setVisibility(View.VISIBLE);
    }

    protected void hideEmoticon(){
        emoticonFlag = false;
        slidrInterface.unlock();
        nestedScrollView.setSmoothScrollingEnabled(true);
        emoticonLayout.setVisibility(View.GONE);
    }

    class EmoticonClick implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if (emoticonLayout.getVisibility() == View.GONE) {
                showEmoticon();
            } else {
                hideEmoticon();
            }
        }
    }

}

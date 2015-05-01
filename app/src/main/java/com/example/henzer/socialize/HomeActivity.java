package com.example.henzer.socialize;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.AccessToken;
import com.facebook.Profile;
import com.facebook.login.LoginManager;


/**
 * Created by Oscar on 4/26/2015.
 */

public class HomeActivity extends ActionBarActivity {
    private SessionData sessionData;

    public HomeActivity(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);
        sessionData = (SessionData)getIntent().getExtras().getSerializable("data");

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        SampleFragmentPagerAdapter pgAdapter = new SampleFragmentPagerAdapter(getSupportFragmentManager(), HomeActivity.this);
        pgAdapter.setSessionData(sessionData);

        viewPager.setAdapter(pgAdapter);
        // Give the SlidingTabLayout the ViewPager
        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        // Center the tabs in the layout
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void logout(MenuItem item) {
        SharedPreferences sharedpreferences = getSharedPreferences
                (MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.commit();
        if (LoginManager.getInstance()!=null) {
            LoginManager.getInstance().logOut();
            AccessToken.setCurrentAccessToken(null);
            Profile.setCurrentProfile(null);
        }
        HomeActivity.this.finish();
    }

    public void exit(MenuItem item){
        moveTaskToBack(true);
        HomeActivity.this.finish();
    }
}

package com.example.henzer.socialize;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem;
import com.example.henzer.socialize.Adapters.SampleFragmentPagerAdapter;
import com.example.henzer.socialize.Models.Person;
import com.example.henzer.socialize.Models.SessionData;
import com.facebook.AccessToken;
import com.facebook.Profile;
import com.facebook.login.LoginManager;

import java.util.List;


/**
 * Created by Oscar on 4/26/2015.
 */

public class HomeActivity extends ActionBarActivity {
    private SessionData sessionData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);
        sessionData = (SessionData)getIntent().getExtras().getSerializable("data");

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.orange_light)));
        getSupportActionBar().setTitle((Html.fromHtml("<b><font color=\"#000000\">" + getString(R.string.title_home_page) + "</font></b>")));

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        SampleFragmentPagerAdapter pgAdapter = new SampleFragmentPagerAdapter(getSupportFragmentManager(), HomeActivity.this);
        pgAdapter.setSessionData(sessionData);

        viewPager.setAdapter(pgAdapter);
        // Give the SlidingTabLayout the ViewPager
        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        slidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.orange_light);
            }
        });
        // Center the tabs in the layout
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        //menu.findItem(R.id.addGroup).setVisible(false);
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

    @Override
    public void onBackPressed() {
    }

    public void settings(MenuItem item){
        MaterialSimpleListAdapter materialAdapter = new MaterialSimpleListAdapter(this);
        materialAdapter.add(new MaterialSimpleListItem.Builder(this)
                .content(R.string.gps_option)
                .icon(R.drawable.ic_launcher)
                .build());
        materialAdapter.add(new MaterialSimpleListItem.Builder(this)
                .content(R.string.select_friends_option)
                .icon(R.drawable.ic_launcher)
                .build());
        MaterialDialog.Builder materialDialog = new MaterialDialog.Builder(this)
                .title(R.string.options)
                .titleColorRes(R.color.orange_light)
                .adapter(materialAdapter, new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog materialDialog, View view, int which, CharSequence charSequence) {
                        if (which == 0) {
                            Toast.makeText(HomeActivity.this, "GPS Selected", Toast.LENGTH_SHORT).show();
                        } else if (which == 1) {
                            List<Person> friends = sessionData.getFriends();
                            for (Person f: friends){
                                f.setSelected(true);
                            }
                            Intent intent = new Intent(HomeActivity.this, SelectContactsActivity.class);
                            intent.putExtra("data", sessionData);
                            startActivity(intent);
                            materialDialog.cancel();
                            friends = sessionData.getFriends();
                            for (Person f: friends){
                                f.setSelected(false);
                            }
                        }
                    }
                });
        materialDialog.show();
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

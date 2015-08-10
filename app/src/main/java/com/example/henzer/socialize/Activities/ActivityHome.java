package com.example.henzer.socialize.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem;
import com.example.henzer.socialize.Adapters.AdapterFragmentPager;
import com.example.henzer.socialize.Layouts.LayoutSlidingTab;
import com.example.henzer.socialize.Models.ModelPerson;
import com.example.henzer.socialize.Models.ModelSessionData;
import com.example.henzer.socialize.R;
import com.facebook.AccessToken;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.kenny.snackbar.SnackBar;

import java.util.List;


/**
 * Created by Oscar on 4/26/2015.
 */

public class ActivityHome extends ActionBarActivity {
    private ModelSessionData modelSessionData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        modelSessionData = (ModelSessionData)getIntent().getExtras().getSerializable("data");

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.orange_light)));
        getSupportActionBar().setTitle((Html.fromHtml("<b><font color=\"#000000\">" + getString(R.string.app_name) + "</font></b>")));

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.ActivityHome_ViewPager);
        AdapterFragmentPager pgAdapter = new AdapterFragmentPager(getSupportFragmentManager(), ActivityHome.this);
        pgAdapter.setModelSessionData(modelSessionData);

        viewPager.setAdapter(pgAdapter);
        // Give the SlidingTabLayout the ViewPager
        LayoutSlidingTab layoutSlidingTab = (LayoutSlidingTab) findViewById(R.id.ActivityHome_SlindingTabs);
        layoutSlidingTab.setCustomTabColorizer(new LayoutSlidingTab.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.orange_light);
            }
        });
        // Center the tabs in the layout
        layoutSlidingTab.setDistributeEvenly(true);
        layoutSlidingTab.setViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void settings(MenuItem item){
        MaterialSimpleListAdapter materialAdapter = new MaterialSimpleListAdapter(this);
        materialAdapter.add(new MaterialSimpleListItem.Builder(this)
                .content(R.string.settings_option_gps)
                .icon(R.drawable.ic_device_gps_fixed)
                .build());
        materialAdapter.add(new MaterialSimpleListItem.Builder(this)
                .content(R.string.settings_option_select_friends)
                .icon(R.drawable.ic_social_group_add)
                .build());
        MaterialDialog.Builder materialDialog = new MaterialDialog.Builder(this)
                .title(R.string.settings_options)
                .titleColorRes(R.color.orange_light)
                .adapter(materialAdapter, new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog materialDialog, View view, int which, CharSequence charSequence) {
                        if (which == 0) {
                            Toast.makeText(ActivityHome.this, "GPS Selected", Toast.LENGTH_SHORT).show();
                        } else if (which == 1) {

                            List<ModelPerson> friends = modelSessionData.getFriends();
                            for (ModelPerson f: friends){
                                f.setSelected(false);
                            }

                            Intent intent = new Intent(ActivityHome.this, ActivitySelectContacts.class);
                            intent.putExtra("data", modelSessionData);
                            startActivity(intent);
                            materialDialog.cancel();

                            friends = modelSessionData.getFriends();
                            for (ModelPerson f: friends){
                                f.setSelected(false);
                                Log.i("Friend Home Selected", f.isHomeSelected() + "");
                            }
                        }
                    }
                });
        materialDialog.show();
    }

    public void logout(MenuItem item) {
        SharedPreferences sharedpreferences = getSharedPreferences
                (ActivityMain.MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.commit();
        if (LoginManager.getInstance()!=null) {
            LoginManager.getInstance().logOut();
            AccessToken.setCurrentAccessToken(null);
            Profile.setCurrentProfile(null);
        }
        ActivityHome.this.finish();
    }
}

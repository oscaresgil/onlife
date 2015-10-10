package com.objective4.app.onlife.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem;
import com.objective4.app.onlife.Adapters.AdapterContact;
import com.objective4.app.onlife.Adapters.AdapterFragmentPager;
import com.objective4.app.onlife.Adapters.AdapterGroup;
import com.objective4.app.onlife.Layouts.LayoutSlidingTab;
import com.objective4.app.onlife.Models.ModelGroup;
import com.objective4.app.onlife.Models.ModelPerson;
import com.objective4.app.onlife.Models.ModelSessionData;
import com.objective4.app.onlife.R;
import com.objective4.app.onlife.Services.ServicePhoneState;
import com.objective4.app.onlife.Tasks.TaskChangeState;
import com.objective4.app.onlife.Tasks.TaskGetFriends;
import com.facebook.login.LoginManager;
import com.facebook.share.model.AppInviteContent;
import com.facebook.share.widget.AppInviteDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import static com.objective4.app.onlife.Controller.StaticMethods.activateDeviceAdmin;
import static com.objective4.app.onlife.Controller.StaticMethods.deactivateDeviceAdmin;
import static com.objective4.app.onlife.Controller.StaticMethods.delDirImages;

public class ActivityHome extends ActionBarActivity {
    private ModelPerson userLogin;
    private SharedPreferences sharedPreferences;
    private AdapterContact adapterContact;
    private AdapterGroup adapterGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        startService(new Intent(this, ServicePhoneState.class));
        activateDeviceAdmin(this);

        sharedPreferences = getSharedPreferences(ActivityMain.MyPREFERENCES, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        userLogin = gson.fromJson(sharedPreferences.getString("userLogin", ""), ModelPerson.class);
        List<ModelPerson> friends = gson.fromJson(sharedPreferences.getString("friends", ""), (new TypeToken<ArrayList<ModelPerson>>() {}.getType()));
        List<ModelGroup> groups = gson.fromJson(sharedPreferences.getString("groups", ""), (new TypeToken<ArrayList<ModelGroup>>() {}.getType()));

        if (sharedPreferences.getBoolean("first_login",false)){
            new TaskGetFriends(this, true).execute(userLogin.getId());
            sharedPreferences.edit().putBoolean("first_login",false).apply();
        }

        ModelSessionData.initInstance(userLogin, friends, groups);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.orange_light)));
        getSupportActionBar().setTitle((Html.fromHtml("<b><font color=\"#000000\">" + getString(R.string.app_name) + "</font></b>")));

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.ActivityHome_ViewPager);
        AdapterFragmentPager pgAdapter = new AdapterFragmentPager(getSupportFragmentManager(), ActivityHome.this);

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

        sharedPreferences.edit().putBoolean("session", true).commit();

        adapterContact = new AdapterContact(this,R.layout.layout_contact,ModelSessionData.getInstance().getFriends());
        adapterGroup = new AdapterGroup(this,R.layout.layout_groups,ModelSessionData.getInstance().getModelGroups());

    }

    @Override
    protected void onPause() {
        super.onPause();
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

    @Override
    protected void onDestroy() {
        Gson gson = new Gson();
        sharedPreferences.edit().putString("userLogin",gson.toJson(ModelSessionData.getInstance().getUser())).apply();
        sharedPreferences.edit().putString("friends",gson.toJson(ModelSessionData.getInstance().getFriends())).apply();
        sharedPreferences.edit().putString("groups",gson.toJson(ModelSessionData.getInstance().getModelGroups())).apply();
        super.onDestroy();

    }

    public void invite_friends(MenuItem item) {
        String appLinkUrl, previewImageUrl;

        appLinkUrl = "https://fb.me/1707393996160728";
        previewImageUrl = "http://www.onlife-app.com/myapplink/logo.png";

        if (AppInviteDialog.canShow()) {
            AppInviteContent content = new AppInviteContent.Builder()
                    .setApplinkUrl(appLinkUrl)
                    .setPreviewImageUrl(previewImageUrl)
                    .build();
            AppInviteDialog.show(this, content);
        }
    }

    public void settings(MenuItem item){
        final MaterialSimpleListAdapter materialAdapter = new MaterialSimpleListAdapter(this);
        materialAdapter.add(new MaterialSimpleListItem.Builder(this)
                .content(R.string.settings_option_uninstall)
                .icon(R.drawable.ic_phonelink_erase_black_48dp)
                .build());
        MaterialDialog.Builder materialDialog = new MaterialDialog.Builder(this)
                .title(R.string.settings_options)
                .titleColorRes(R.color.orange_light)
                .adapter(materialAdapter, new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(final MaterialDialog materialDialog, View view, int which, CharSequence charSequence) {
                        if (which == 0) {
                            new MaterialDialog.Builder(ActivityHome.this)
                                    .title(getResources().getString(R.string.settings_option_uninstall)+"?")
                                    .content(R.string.really_delete)
                                    .positiveText(R.string.yes)
                                    .positiveColorRes(R.color.orange_light)
                                    .negativeText(R.string.no)
                                    .negativeColorRes(R.color.red)
                                    .callback(new MaterialDialog.ButtonCallback() {
                                        @Override
                                        public void onPositive(MaterialDialog dialog) {
                                            deactivateDeviceAdmin(ActivityHome.this);

                                            Intent intent = new Intent(Intent.ACTION_DELETE);
                                            intent.setData(Uri.parse("package:" + getPackageName()));
                                            startActivity(intent);

                                            logout(null);

                                            dialog.dismiss();
                                            dialog.cancel();
                                            materialDialog.dismiss();
                                        }
                                    }).show();
                        }
                    }
                });
        materialDialog.show();
    }

    public void logout(MenuItem item) {
        sharedPreferences.edit().remove("session").apply();
        sharedPreferences.edit().remove("friends").apply();
        sharedPreferences.edit().remove("groups").apply();
        sharedPreferences.edit().remove("userLogin").apply();
        delDirImages(this,ModelSessionData.getInstance().getFriends(),ModelSessionData.getInstance().getModelGroups());
        ModelSessionData.getInstance().clear();
        LoginManager.getInstance().logOut();
        new TaskChangeState().execute(userLogin.getId(), "O");
        finish();
    }

    public AdapterContact getAdapterContact() {
        return adapterContact;
    }

    public AdapterGroup getAdapterGroup() {
        return adapterGroup;
    }

    public void setAdapterContact(AdapterContact adapterContact) {
        this.adapterContact = adapterContact;
    }
}

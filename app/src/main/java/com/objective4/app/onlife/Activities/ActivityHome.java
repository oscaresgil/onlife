package com.objective4.app.onlife.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem;
import com.facebook.login.LoginManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.objective4.app.onlife.Adapters.AdapterBaseElements;
import com.objective4.app.onlife.Adapters.AdapterFragmentPager;
import com.objective4.app.onlife.Fragments.Social.FragmentGroups;
import com.objective4.app.onlife.Models.ModelGroup;
import com.objective4.app.onlife.Models.ModelPerson;
import com.objective4.app.onlife.Models.ModelSessionData;
import com.objective4.app.onlife.R;
import com.objective4.app.onlife.Services.ServicePhoneState;
import com.objective4.app.onlife.Tasks.TaskChangeState;
import com.objective4.app.onlife.Tasks.TaskGetFriends;

import java.util.ArrayList;
import java.util.List;

import static com.objective4.app.onlife.Controller.StaticMethods.activateDeviceAdmin;
import static com.objective4.app.onlife.Controller.StaticMethods.deactivateDeviceAdmin;
import static com.objective4.app.onlife.Controller.StaticMethods.delDirImages;
import static com.objective4.app.onlife.Controller.StaticMethods.getModelGroupIndex;
import static com.objective4.app.onlife.Controller.StaticMethods.inviteFacebookFriends;

public class ActivityHome extends AppCompatActivity{
    private ModelPerson userLogin;
    private SharedPreferences sharedPreferences;
    private AdapterBaseElements adapterContact;
    private AdapterBaseElements adapterGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setSupportActionBar((Toolbar) findViewById(R.id.ActivityHome_ToolBar));
        startService(new Intent(this, ServicePhoneState.class));
        activateDeviceAdmin(this);

        sharedPreferences = getSharedPreferences(ActivityMain.MyPREFERENCES, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        userLogin = gson.fromJson(sharedPreferences.getString("userLogin", ""), ModelPerson.class);
        List<ModelPerson> friends = gson.fromJson(sharedPreferences.getString("friends", ""), (new TypeToken<ArrayList<ModelPerson>>() {
        }.getType()));
        List<ModelGroup> groups = gson.fromJson(sharedPreferences.getString("groups", ""), (new TypeToken<ArrayList<ModelGroup>>() {
        }.getType()));

        if (sharedPreferences.getBoolean("first_login", false)) {
            new TaskGetFriends(this, true).execute(userLogin.getId());
            sharedPreferences.edit().putBoolean("first_login", false).apply();
        }

        ModelSessionData.initInstance(userLogin, friends, groups);

        ViewPager viewPager = (ViewPager) findViewById(R.id.ActivityHome_ViewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.ActivityHome_SlindingTabs);
        viewPager.setAdapter(new AdapterFragmentPager(getSupportFragmentManager(), this));
        tabLayout.setupWithViewPager(viewPager);

        sharedPreferences.edit().putBoolean("session", true).apply();
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
        return item.getItemId() == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        onTrimMemory(TRIM_MEMORY_UI_HIDDEN);
    }

    @Override
    protected void onStop() {
        super.onStop();
        onTrimMemory(TRIM_MEMORY_UI_HIDDEN);
    }

    @Override
    protected void onDestroy() {
        Gson gson = new Gson();
        sharedPreferences.edit().putString("userLogin", gson.toJson(ModelSessionData.getInstance().getUser())).apply();
        sharedPreferences.edit().putString("friends", gson.toJson(ModelSessionData.getInstance().getFriends())).apply();
        sharedPreferences.edit().putString("groups", gson.toJson(ModelSessionData.getInstance().getModelGroups())).apply();
        super.onDestroy();

    }

    public void invite_friends(MenuItem item) {
        inviteFacebookFriends(this);
    }

    public void settings(MenuItem item) {
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
                                    .title(getResources().getString(R.string.settings_option_uninstall) + "?")
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
        delDirImages(this, ModelSessionData.getInstance().getFriends(), ModelSessionData.getInstance().getModelGroups());
        ModelSessionData.getInstance().clear();
        LoginManager.getInstance().logOut();
        new TaskChangeState().execute(userLogin.getId(), "O");
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == FragmentGroups.GROUP_BLOCK_ACTIVITY_ID) {
                String tag = data.getStringExtra("tag");
                if ("image".equals(tag)){
                    int position = data.getIntExtra("position",-1);
                    getAdapterGroup().notifyItemChanged(position);
                }else if("delete".equals(tag)){
                    ModelGroup delG = (ModelGroup) data.getSerializableExtra("delete_group");
                    int pos = getModelGroupIndex(delG, ModelSessionData.getInstance().getModelGroups());
                    ModelSessionData.getInstance().getModelGroups().remove(pos);
                    getAdapterGroup().notifyItemRemoved(pos);
                }
            }
            else if (requestCode == FragmentGroups.CREATE_GROUP_ACTIVITY_ID) {
                ModelGroup newG = (ModelGroup) data.getSerializableExtra("new_group");
                List<ModelGroup> groups = ModelSessionData.getInstance().getModelGroups();
                groups.add(newG);
                getAdapterGroup().notifyItemInserted(groups.size());
            }
        }
    }

    public AdapterBaseElements getAdapterContact() {
        return adapterContact;
    }

    public void setAdapterContact(AdapterBaseElements adapterContact) {
        this.adapterContact = adapterContact;
    }

    public AdapterBaseElements getAdapterGroup() {
        return adapterGroup;
    }

    public void setAdapterGroup(AdapterBaseElements adapterGroup) {
        this.adapterGroup = adapterGroup;
    }
}

package com.objective4.app.onlife.Activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.objective4.app.onlife.Adapters.AdapterBaseElements;
import com.objective4.app.onlife.Adapters.AdapterFragmentPager;
import com.objective4.app.onlife.Fragments.Social.FragmentContacts;
import com.objective4.app.onlife.Fragments.Social.FragmentGroups;
import com.objective4.app.onlife.Models.ModelGroup;
import com.objective4.app.onlife.Models.ModelPerson;
import com.objective4.app.onlife.Models.ModelSessionData;
import com.objective4.app.onlife.R;
import com.objective4.app.onlife.Services.ServicePhoneState;
import com.objective4.app.onlife.Tasks.TaskChangeState;
import com.objective4.app.onlife.Tasks.TaskGetFriends;
import com.objective4.app.onlife.Tasks.TaskGetState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.objective4.app.onlife.Controller.StaticMethods.activateDeviceAdmin;
import static com.objective4.app.onlife.Controller.StaticMethods.deactivateDeviceAdmin;
import static com.objective4.app.onlife.Controller.StaticMethods.delDirImages;
import static com.objective4.app.onlife.Controller.StaticMethods.getModelGroupIndex;
import static com.objective4.app.onlife.Controller.StaticMethods.inviteFacebookFriends;
import static com.objective4.app.onlife.Controller.StaticMethods.makeSnackbar;
import static com.objective4.app.onlife.Controller.StaticMethods.setHashToList;
import static com.objective4.app.onlife.Controller.StaticMethods.setListToHash;
import static com.objective4.app.onlife.Controller.StaticMethods.unSelectFriends;

public class ActivityHome extends AppCompatActivity implements ViewPager.OnPageChangeListener{
    public static boolean isRunning=false;

    private ModelPerson userLogin;
    private SharedPreferences sharedPreferences;
    private AdapterBaseElements adapterContact;
    private AdapterBaseElements adapterGroup;
    private Dialog updateForcedDialog,updateOptionalDialog;
    private TabLayout tabLayout;

    private int fragmentPage = 0;

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

        List<ModelPerson> friends = new ArrayList<>();
        List<ModelGroup> groups = new ArrayList<>();
        String friendsString = sharedPreferences.getString("friends", "");
        if ("".equals(friendsString) || "{}".equals(friendsString)){
            new TaskGetFriends(this, true).execute(userLogin.getId());
        }else {
            friends = gson.fromJson(friendsString, (new TypeToken<ArrayList<ModelPerson>>() {
            }.getType()));
            unSelectFriends(friends);
            groups = gson.fromJson(sharedPreferences.getString("groups", ""), (new TypeToken<ArrayList<ModelGroup>>() {
            }.getType()));
        }

        HashMap<String,ModelPerson> hashMap = setListToHash(friends);

        if (sharedPreferences.getBoolean("first_login", false)) {
            new TaskGetFriends(this, true).execute(userLogin.getId());
        }

        ModelSessionData.initInstance(userLogin, hashMap, groups);

        new TaskGetState(this).execute(ModelSessionData.getInstance().getUser().getId());

        ViewPager viewPager = (ViewPager) findViewById(R.id.ActivityHome_ViewPager);
        tabLayout = (TabLayout) findViewById(R.id.ActivityHome_SlindingTabs);
        viewPager.setAdapter(new AdapterFragmentPager(getSupportFragmentManager(), this));
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(this);

        if (sharedPreferences.getInt("update_key",0)==1){
            setDialogUpdate(1);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isRunning = true;
        registerReceiver(broadcastReceiver, new IntentFilter("com.objective4.app.onlife.Activities.ActivityHome"));
        /*if (sharedPreferences.contains("update_key")){
            int val = sharedPreferences.getInt("update_key",0);
            setDialogUpdate(val);
        }*/
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return item.getItemId() == R.id.MenuHome_ActionSettings || super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        isRunning = false;
        onTrimMemory(TRIM_MEMORY_UI_HIDDEN);
    }

    @Override
    protected void onStop() {
        super.onStop();
        isRunning = false;
        onTrimMemory(TRIM_MEMORY_UI_HIDDEN);
        try{
            unregisterReceiver(broadcastReceiver);
        }catch(Exception ignored){}
    }

    @Override
    protected void onDestroy() {
        Gson gson = new Gson();
        sharedPreferences.edit().putString("userLogin", gson.toJson(ModelSessionData.getInstance().getUser())).apply();
        sharedPreferences.edit().putString("friends", gson.toJson(setHashToList(ModelSessionData.getInstance().getFriends()))).apply();
        sharedPreferences.edit().putString("groups", gson.toJson(ModelSessionData.getInstance().getModelGroups())).apply();
        super.onDestroy();

    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            int update = extras.getInt("update");
            setDialogUpdate(update);
        }
    };

    public void inviteFriends(MenuItem item) {
        inviteFacebookFriends(this);
    }

    public void settings(MenuItem item) {
        MaterialSimpleListAdapter materialAdapter = new MaterialSimpleListAdapter(this);
        materialAdapter.add(new MaterialSimpleListItem.Builder(this)
                .content(R.string.see_tutorial)
                .icon(R.drawable.ic_help_outline_black_48dp)
                .build());
        materialAdapter.add(new MaterialSimpleListItem.Builder(this)
                .content(R.string.see_privacy_policy)
                .icon(R.drawable.ic_perm_device_information_black_48dp)
                .build());
        materialAdapter.add(new MaterialSimpleListItem.Builder(this)
                .content(R.string.settings_select_contacts)
                .icon(R.drawable.ic_contacts_black_48dp)
                .build());
        materialAdapter.add(new MaterialSimpleListItem.Builder(this)
                .content(R.string.settings_option_uninstall)
                .icon(R.drawable.ic_phonelink_erase_black_48dp)
                .build());
        MaterialDialog.Builder materialDialog = new MaterialDialog.Builder(this)
                .title(R.string.settings_options)
                .titleColorRes(R.color.accent)
                .adapter(materialAdapter, new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(final MaterialDialog materialDialog, View view, int which, CharSequence charSequence) {
                        if (which == 0){
                            startActivity(new Intent(ActivityHome.this, ActivityOnboarding.class));
                            materialDialog.dismiss();
                        }else if(which == 1) {
                            startActivity(new Intent(ActivityHome.this, ActivityPrivacyPolicy.class));
                            materialDialog.dismiss();
                        }else if (which == 2){
                            startActivity(new Intent(ActivityHome.this,ActivitySelectContacts.class));
                            materialDialog.dismiss();
                        }else if (which == 3) {
                            new MaterialDialog.Builder(ActivityHome.this)
                                    .title(getResources().getString(R.string.settings_option_uninstall) + "?")
                                    .content(R.string.really_delete)
                                    .positiveText(R.string.yes)
                                    .positiveColorRes(R.color.accent)
                                    .negativeText(R.string.no)
                                    .negativeColorRes(R.color.black)
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
        sharedPreferences.edit().remove("update_key").apply();
        delDirImages(this, setHashToList(ModelSessionData.getInstance().getFriends()), ModelSessionData.getInstance().getModelGroups());
        ModelSessionData.getInstance().clear();
        LoginManager.getInstance().logOut();
        new TaskChangeState().execute(userLogin.getId(), "O");
        isRunning = false;
        if (item != null) {
            startActivity(new Intent(ActivityHome.this, ActivityMain.class));
            finish();
        }
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
                unSelectFriends(setHashToList(ModelSessionData.getInstance().getFriends()));
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

    public void inviteFriends(View view){
        inviteFacebookFriends(this);
    }

    public void setDialogUpdate(int val){
        if (val==0) makeSnackbar(this,tabLayout,R.string.no_connection, Snackbar.LENGTH_LONG);
        if (val==1) {
            if (updateOptionalDialog!=null && updateOptionalDialog.isShowing())
                updateOptionalDialog.dismiss();
            if (updateForcedDialog == null || !updateForcedDialog.isShowing())
                updateForcedDialog = new AlertDialogWrapper.Builder(this)
                        .setTitle(getResources().getString(R.string.update_available))
                        .setMessage(getResources().getString(R.string.update_forced))
                        .setCancelable(false)
                        .setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startPlayActivity();
                                logout(null);
                            }
                        }).show();
        }else if(val==2){
            if (updateForcedDialog!=null && updateForcedDialog.isShowing())
                updateForcedDialog.dismiss();
            if (updateOptionalDialog == null || !updateOptionalDialog.isShowing())
                updateOptionalDialog = new MaterialDialog.Builder(this)
                        .title(getResources().getString(R.string.update_available))
                        .content(getResources().getString(R.string.update_optional))
                        .positiveText(R.string.yes)
                        .positiveColorRes(R.color.accent)
                        .negativeText(R.string.no)
                        .cancelable(false)
                        .negativeColorRes(R.color.black)
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                startPlayActivity();
                                sharedPreferences.edit().remove("update_key").apply();
                                dialog.dismiss();
                            }

                            @Override
                            public void onNegative(MaterialDialog dialog) {
                                sharedPreferences.edit().remove("update_key").apply();
                                dialog.dismiss();
                            }
                        }).show();
        }else if (val==3){
            if (updateForcedDialog!=null && updateForcedDialog.isShowing())
                updateForcedDialog.dismiss();
            if (updateOptionalDialog!=null && updateOptionalDialog.isShowing())
                updateOptionalDialog.dismiss();
        }
    }

    public void startPlayActivity() {
        String appPackageName = getPackageName();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem searchItem = menu.findItem(R.id.MenuHome_SearchContact);
        if (fragmentPage==0 && searchItem!=null){
            if (FragmentContacts.isSearchOpened){
                searchItem.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_close_black_24dp));
            }
            searchItem.setVisible(true);
        }else if (fragmentPage==1 && searchItem!=null){
            searchItem.setVisible(false);
            if (FragmentContacts.isSearchOpened){
                Intent i = new Intent("com.objective4.app.onlife.Fragments.Social.FragmentContacts");
                i.putExtra("tag", "remove_search");
                sendBroadcast(i);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        fragmentPage = position;
        invalidateOptionsMenu();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }
}

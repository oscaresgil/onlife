package com.example.henzer.socialize.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem;
import com.example.henzer.socialize.Adapters.AdapterContact;
import com.example.henzer.socialize.Adapters.AdapterFragmentPager;
import com.example.henzer.socialize.Adapters.AdapterGroup;
import com.example.henzer.socialize.Layouts.LayoutSlidingTab;
import com.example.henzer.socialize.Models.ModelGroup;
import com.example.henzer.socialize.Models.ModelPerson;
import com.example.henzer.socialize.Models.ModelSessionData;
import com.example.henzer.socialize.R;
import com.example.henzer.socialize.Services.ServicePhoneState;
import com.example.henzer.socialize.Tasks.TaskChangeState;
import com.example.henzer.socialize.Tasks.TaskGetFriends;
import com.example.henzer.socialize.Tasks.TaskSetFriends;
import com.example.henzer.socialize.Tasks.TaskSimpleImageDownload;
import com.facebook.login.LoginManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.example.henzer.socialize.Controller.StaticMethods.activateDeviceAdmin;
import static com.example.henzer.socialize.Controller.StaticMethods.delDirImages;
import static com.example.henzer.socialize.Controller.StaticMethods.delImageProfile;

public class ActivityHome extends ActionBarActivity {
    public static final String TAG = "ActivityHome";
    private ModelPerson userLogin;
    private List<ModelPerson> friends;
    private List<ModelGroup> groups;
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
        friends = gson.fromJson(sharedPreferences.getString("friends", ""), (new TypeToken<ArrayList<ModelPerson>>(){}.getType()));

        groups = new ArrayList<>();
        if (sharedPreferences.contains("groups")){
            groups = gson.fromJson(sharedPreferences.getString("groups", ""), (new TypeToken<ArrayList<ModelGroup>>(){}.getType()));
        } else {
            sharedPreferences.edit().putString("groups",gson.toJson(groups)).commit();
        }

        ModelSessionData.initInstance(userLogin,friends,groups);

        if (!sharedPreferences.contains("session")) {
            ArrayList<String> idFriends = new ArrayList<>();
            idFriends.add(userLogin.getId());
            for (ModelPerson modelPerson : friends){
                idFriends.add(modelPerson.getId());
            }
            TaskSetFriends taskFriends = new TaskSetFriends(this);
            taskFriends.execute(idFriends);
            friends = new ArrayList<>();
        }else{
            new TaskGetFriends(this,null).execute(userLogin.getId());
        }
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

        adapterContact = new AdapterContact(this,R.layout.layout_contact,friends);
        adapterGroup = new AdapterGroup(this,R.layout.layout_groups,groups);

    }

    @Override
    protected void onResume() {
        registerReceiver(broadcastReceiver,new IntentFilter("com.example.henzer.socialize.Activities.ActivityHome"));
        super.onResume();
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

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            String tag = extras.getString("tag");
            if (tag.equals("update")) {
                String id = extras.getString("id");
                String state = extras.getString("state");
                Log.i(TAG, "Broadcast Receiver. ID:" + id + ". State:" + state);
                List<ModelPerson> friendsT = ModelSessionData.getInstance().getFriends();
                if (state.equals("O")) {
                    for (int i = 0; i < friendsT.size(); i++) {
                        ModelPerson p = friendsT.get(i);
                        if (p.getId().equals(id)) {
                            boolean removed = delImageProfile(ActivityHome.this,p.getId());
                            friendsT.remove(i);
                            adapterContact.remove(adapterContact.getItem(i));
                            Log.i(TAG, "Broadcast Receiver. Remove ID:" + p.getId() + ". Name: " + p.getName() + ". State:" + state+  ". Image Deleted: "+removed);
                            break;
                        }
                    }
                } else {
                    for (ModelPerson p : friendsT) {
                        if (p.getId().equals(id)) {
                            p.setState(state);
                            Log.i(TAG, "Broadcast Receiver. Update ID:" + p.getId() + ". Name: " + p.getName() + ". State:" + state);
                            break;
                        }
                    }
                }
            }else if(tag.equals("new_user")){
                ModelPerson newUser = (ModelPerson) extras.getSerializable("new_user");
                List<ModelPerson> friends = ModelSessionData.getInstance().getFriends();
                friends.add(newUser);

                Collections.sort(friends, new Comparator<ModelPerson>() {
                    @Override
                    public int compare(ModelPerson modelPerson1, ModelPerson modelPerson2) {
                        return modelPerson1.getName().compareTo(modelPerson2.getName());
                    }
                });
                adapterContact.clear();
                adapterContact.addAll(friends);
            }
            adapterContact.notifyDataSetChanged();
        }
    };

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    protected void onStop() {
        Log.i(TAG,"onStop()");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG,"onDestroy()");

        Gson gson = new Gson();
        sharedPreferences.edit().putString("userLogin",gson.toJson(ModelSessionData.getInstance().getUser())).commit();
        sharedPreferences.edit().putString("friends",gson.toJson(ModelSessionData.getInstance().getFriends())).commit();
        sharedPreferences.edit().putString("groups",gson.toJson(ModelSessionData.getInstance().getModelGroups())).commit();
        unregisterReceiver(broadcastReceiver);

        super.onDestroy();

    }

    public void settings(MenuItem item){
        MaterialSimpleListAdapter materialAdapter = new MaterialSimpleListAdapter(this);
        materialAdapter.add(new MaterialSimpleListItem.Builder(this)
                .content(R.string.settings_option_select_friends)
                .icon(R.drawable.ic_person_add_black_24dp)
                .build());
        MaterialDialog.Builder materialDialog = new MaterialDialog.Builder(this)
                .title(R.string.settings_options)
                .titleColorRes(R.color.orange_light)
                .adapter(materialAdapter, new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog materialDialog, View view, int which, CharSequence charSequence) {
                        if (which == 0) {
                            /*List<ModelPerson> friends = ModelSessionData.getInstance().getFriends();
                            for (ModelPerson f: friends){
                                f.setSelected(false);
                            }

                            Intent intent = new Intent(ActivityHome.this, ActivitySelectContacts.class);
                            intent.putExtra("data", ModelSessionData.getInstance());
                            startActivity(intent);
                            materialDialog.cancel();

                            friends = ModelSessionData.getInstance().getFriends();
                            for (ModelPerson f: friends){
                                f.setSelected(false);
                                //Log.i("Friend Home Selected", f.isHomeSelected() + "");
                            }*/
                        }
                    }
                });
        materialDialog.show();
    }

    public void logout(MenuItem item) {
        sharedPreferences.edit().remove("session").commit();
        sharedPreferences.edit().remove("friends").commit();
        sharedPreferences.edit().remove("groups").commit();
        sharedPreferences.edit().remove("userLogin").commit();
        boolean delete =delDirImages(this,ModelSessionData.getInstance().getFriends(),ModelSessionData.getInstance().getModelGroups());
        ModelSessionData.getInstance().clear();
        LoginManager.getInstance().logOut();
        new TaskChangeState().execute(userLogin.getId(),"O");
        Log.i(TAG,"LogOut. User: "+userLogin.getId()+". Name: "+userLogin.getName()+". State: O. Directory Deleted: "+delete);
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

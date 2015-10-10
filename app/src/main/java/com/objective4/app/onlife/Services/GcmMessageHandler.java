package com.objective4.app.onlife.Services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.objective4.app.onlife.Activities.ActivityMain;
import com.objective4.app.onlife.BlockActivity.ActivityInBlock;
import com.objective4.app.onlife.Models.ModelGroup;
import com.objective4.app.onlife.Models.ModelPerson;
import com.objective4.app.onlife.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.objective4.app.onlife.Controller.StaticMethods.checkDeviceAdmin;
import static com.objective4.app.onlife.Controller.StaticMethods.isFriendAlready;
import static com.objective4.app.onlife.Controller.StaticMethods.removeFriend;

public class GcmMessageHandler extends IntentService {
    private SharedPreferences sharedPreferences;
    private Gson gson;
    private String user;
    private String message;
    private String gifName;

    public GcmMessageHandler() {
        super("GcmMessageHandler");
        gson = new Gson();
    }

    public void onCreate() {
        super.onCreate();
    }

    @Override protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        String tag = extras.getString("tag");
        if (tag !=null) {
            switch (tag) {
                case "block":
                    message = extras.getString("message");
                    user = extras.getString("userName");
                    gifName = extras.getString("gifName");
                    boolean adminChecked = checkDeviceAdmin(this);
                    if ((!user.equals("") || user != null) && adminChecked) {
                        onMessage(this);
                    } else if (!adminChecked) {
                        Intent i = new Intent("com.objective4.app.onlife.Fragments.FragmentContacts");
                        i.putExtra("tag", "no_device_admin");
                        sendBroadcast(i);
                    }
                    break;
                case "newUser":
                    String user = extras.getString("user");
                    ModelPerson newUser = gson.fromJson(user, ModelPerson.class);

                    sharedPreferences = getSharedPreferences("OnlifePrefs", Context.MODE_PRIVATE);
                    List<ModelPerson> friends = gson.fromJson(sharedPreferences.getString("friends", ""), (new TypeToken<ArrayList<ModelPerson>>() {}.getType()));
                    if (!isFriendAlready(friends,newUser)){
                        friends.add(newUser);
                        Collections.sort(friends, new Comparator<ModelPerson>() {
                            @Override
                            public int compare(ModelPerson modelPerson1, ModelPerson modelPerson2) {
                                return modelPerson1.getName().compareTo(modelPerson2.getName());
                            }
                        });
                        sharedPreferences.edit().putString("friends",gson.toJson(friends)).apply();
                    }

                    Intent i = new Intent("com.objective4.app.onlife.Fragments.FragmentContacts");
                    i.putExtra("tag", "new_user");
                    i.putExtra("new_user", newUser);
                    sendBroadcast(i);

                    break;
                case "update": {
                    String idP = extras.getString("id");
                    String state = extras.getString("state");

                    if (state.equals("O")){
                        sharedPreferences = getSharedPreferences("OnlifePrefs", Context.MODE_PRIVATE);
                        List<ModelPerson> friendsR = gson.fromJson(sharedPreferences.getString("friends", ""), (new TypeToken<ArrayList<ModelPerson>>() {}.getType()));
                        List<ModelGroup> groupsR = gson.fromJson(sharedPreferences.getString("groups", ""), (new TypeToken<ArrayList<ModelGroup>>() {}.getType()));
                        removeFriend(this,friendsR,groupsR,idP);
                        sharedPreferences.edit().putString("friends",gson.toJson(friendsR)).apply();
                        sharedPreferences.edit().putString("groups",gson.toJson(groupsR)).apply();
                    }

                    Intent i2 = new Intent("com.objective4.app.onlife.Fragments.FragmentContacts");
                    i2.putExtra("tag", "update");
                    i2.putExtra("id", idP);
                    i2.putExtra("state", state);
                    sendBroadcast(i2);
                    break;
                }
            }
        }
    }



    protected void onMessage(Context context) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_notification_icon)
                        .setTicker(getResources().getString(R.string.notification_someone_block_you))
                        .setStyle(new NotificationCompat.BigTextStyle())
                        .setOnlyAlertOnce(true);

        NotificationManager myNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        myNotificationManager.notify(0, mBuilder.build());
        myNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        myNotificationManager.cancel(0);

        Intent i = new Intent(context, ActivityInBlock.class);
        i.putExtra("user",user);
        i.putExtra("message",message);
        i.putExtra("gif",gifName);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        DevicePolicyManager mDPM =
                (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ignored) {}
        mDPM.lockNow();
        startActivity(i);
    }
}
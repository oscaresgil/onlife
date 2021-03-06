package com.objective4.app.onlife.Services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.Display;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import static com.objective4.app.onlife.Controller.StaticMethods.removeFriendFromGroup;

public class GcmMessageHandler extends IntentService {
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
        SharedPreferences sharedPreferences = getSharedPreferences("OnlifePrefs", Context.MODE_PRIVATE);
        try {
            if (tag != null) {
                switch (tag) {
                    case "block":
                        message = extras.getString("message");
                        user = extras.getString("userName");
                        gifName = extras.getString("gifName");
                        boolean adminChecked = checkDeviceAdmin(this);
                        if ((user != null && !user.equals("")) && adminChecked && sharedPreferences.getInt("update_key", 0) != 1) {
                            onMessage(this);
                        }
                        break;
                    case "newUser":
                        String user = extras.getString("user");
                        ModelPerson newUser = gson.fromJson(user, ModelPerson.class);

                        List<ModelPerson> friends = gson.fromJson(sharedPreferences.getString("friends", ""), (new TypeToken<ArrayList<ModelPerson>>() {
                        }.getType()));
                        if (!isFriendAlready(friends, newUser.getId())) {
                            friends.add(newUser);
                            Collections.sort(friends, new Comparator<ModelPerson>() {
                                @Override
                                public int compare(ModelPerson modelPerson1, ModelPerson modelPerson2) {
                                    return modelPerson1.getName().compareTo(modelPerson2.getName());
                                }
                            });
                            sharedPreferences.edit().putString("friends", gson.toJson(friends)).apply();
                        }

                        Intent i = new Intent("com.objective4.app.onlife.Fragments.Social.FragmentContacts");
                        i.putExtra("tag", "new_user");
                        i.putExtra("new_user", newUser);
                        sendBroadcast(i);

                        break;
                    case "update": {
                        String idP = extras.getString("id");
                        String state = extras.getString("state");

                        assert state != null;
                        if (state.equals("O")) {
                            List<ModelPerson> friendsR = gson.fromJson(sharedPreferences.getString("friends", ""), (new TypeToken<ArrayList<ModelPerson>>() {
                            }.getType()));
                            List<ModelGroup> groupsR = gson.fromJson(sharedPreferences.getString("groups", ""), (new TypeToken<ArrayList<ModelGroup>>() {
                            }.getType()));

                            sharedPreferences.edit().putString("friends", gson.toJson(removeFriend(this, friendsR, idP))).apply();
                            sharedPreferences.edit().putString("groups", gson.toJson(removeFriendFromGroup(groupsR, idP))).apply();
                        }

                        Intent i2 = new Intent("com.objective4.app.onlife.Fragments.Social.FragmentContacts");
                        i2.putExtra("tag", "update");
                        i2.putExtra("id", idP);
                        i2.putExtra("state", state);
                        sendBroadcast(i2);
                        break;
                    }
                    case "ping": {
                        String idP = extras.getString("id");
                        if (isScreenOn(getApplicationContext())) {
                            Intent pingIntent = new Intent("com.objective4.app.onlife.BroadcastReceivers.BroadcastReceiverPing");
                            pingIntent.putExtra("id", idP);
                            sendBroadcast(pingIntent);
                        }
                    }
                }
            }
        }catch (Exception ignored){}
    }



    protected void onMessage(Context context) {

        NotificationManager myNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder notificationBuilder = new Notification.Builder(context)
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setOnlyAlertOnce(true)
                .setContentTitle(getResources().getString(R.string.notification_someone_block_you))
                .setContentText(user + " " + getResources().getString(R.string.notification_has_blocked))
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setCategory(Notification.CATEGORY_PROMO)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setVisibility(Notification.VISIBILITY_PUBLIC);

        }else{
            notificationBuilder.setTicker(getResources().getString(R.string.notification_someone_block_you))
                            .setStyle(new Notification.BigTextStyle());
        }

        myNotificationManager.notify(0,notificationBuilder.build());

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
        myNotificationManager.cancel(0);
        startActivity(i);
    }

    /**
     * Is the screen of the device on.
     * @param context the context
     * @return true when (at least one) screen is on
     */
    public boolean isScreenOn(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            DisplayManager dm = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
            boolean screenOn = false;
            for (Display display : dm.getDisplays()) {
                if (display.getState() != Display.STATE_OFF) {
                    screenOn = true;
                }
            }
            return screenOn;
        } else {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            //noinspection deprecation
            return pm.isScreenOn();
        }
    }

}

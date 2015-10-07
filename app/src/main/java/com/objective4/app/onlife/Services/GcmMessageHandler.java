package com.objective4.app.onlife.Services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.objective4.app.onlife.BlockActivity.ActivityInBlock;
import com.objective4.app.onlife.Models.ModelPerson;
import com.objective4.app.onlife.R;

import static com.objective4.app.onlife.Controller.StaticMethods.checkDeviceAdmin;

public class GcmMessageHandler extends IntentService {
    private String user;
    private String message;
    private String gifName;

    public GcmMessageHandler() {
        super("GcmMessageHandler");
    }

    public void onCreate() {
        super.onCreate();
    }

    @Override protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        String tag = extras.getString("tag");
        if (tag != null) {
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
                    Gson gson = new Gson();
                    String user = extras.getString("user");
                    ModelPerson newUser = gson.fromJson(user, ModelPerson.class);

                    Intent i = new Intent("com.objective4.app.onlife.Fragments.FragmentContacts");
                    i.putExtra("tag", "new_user");
                    i.putExtra("new_user", newUser);
                    sendBroadcast(i);

                    break;
                case "update": {
                    String idP = extras.getString("id");
                    String state = extras.getString("state");

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

package com.example.henzer.socialize.Services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Adapter;
import android.widget.Toast;

import com.example.henzer.socialize.Activities.ActivityHome;
import com.example.henzer.socialize.Adapters.AdapterContact;
import com.example.henzer.socialize.BlockActivity.ActivityInBlock;
import com.example.henzer.socialize.Models.ModelPerson;
import com.example.henzer.socialize.Models.ModelSessionData;
import com.example.henzer.socialize.R;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class GcmMessageHandler extends IntentService {
    private final String TAG = "GcmMessageHandler";
    private NotificationManager myNotificationManager;
    private String user, message, gifName,tag;

    public GcmMessageHandler() {
        super("GcmMessageHandler");
    }

    public void onCreate() {
        super.onCreate();
    }

    @Override protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);
        Log.i(TAG,"MessageType: "+messageType);
        tag = extras.getString("tag");
        if (tag!=null) {
            Log.i(TAG,"Tag: "+tag);
            if (tag.equals("block")) {
                message = extras.getString("message");
                user = extras.getString("userName");
                gifName = extras.getString("gifName");
                Log.i(TAG, "User: " + user + ". Message: " + message + ". GifName: " + gifName);
                if (!user.equals("") || user != null) {
                    onMessage(this);
                }
            } else if (tag.equals("update")) {
                String state = extras.getString("state");
                String idP = extras.getString("id");

                Intent i = new Intent("com.example.henzer.socialize.Activities.ActivityHome");
                i.putExtra("id",idP);
                i.putExtra("state",state);
                sendBroadcast(i);
            }
        }
    }



    protected void onMessage(Context context) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setTicker(getResources().getString(R.string.notification_someone_block_you))
                        .setStyle(new NotificationCompat.BigTextStyle())
                        .setOnlyAlertOnce(true);

        myNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

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
        } catch (InterruptedException ex) {
        }
        mDPM.lockNow();
        startActivity(i);
    }
}

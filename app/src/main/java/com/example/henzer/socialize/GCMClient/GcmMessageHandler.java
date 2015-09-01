package com.example.henzer.socialize.GCMClient;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.henzer.socialize.BlockActivity.ActivityInBlock;
import com.example.henzer.socialize.R;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;


public class GcmMessageHandler extends IntentService {
    private final String TAG = "GcmMessageHandler";
    private NotificationManager myNotificationManager;
    private String user, message, gifName;

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
        message = extras.getString("message");
        user = extras.getString("userName");
        gifName = extras.getString("gifName");
        Log.i(TAG,"User: "+user+". Message: "+message+". GifName: "+gifName);
        if(!user.equals("") || user != null) {
            onMessage(this);
        }
    }

    protected void onMessage(Context context) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_launcher))
                        .setTicker(getResources().getString(R.string.notification_someone_block_you))
                        .setContentTitle(user)
                        .setVibrate(new long[]{ 1000, 1000 })
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setWhen(System.currentTimeMillis())
                        .setLights(getResources().getColor(R.color.orange_light), 3000, 3000)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setContentText(message);

        myNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        myNotificationManager.notify(0, mBuilder.build());

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

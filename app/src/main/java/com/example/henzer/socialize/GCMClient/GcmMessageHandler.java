package com.example.henzer.socialize.GCMClient;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.henzer.socialize.Adapters.GPSControl;
import com.example.henzer.socialize.InBlockActivity;
import com.example.henzer.socialize.R;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import static com.example.henzer.socialize.Adapters.StaticMethods.distFrom;


public class GcmMessageHandler extends IntentService {
    private NotificationManager myNotificationManager;

    private static Looper looper;
    private String user, message;
    private double distance;

    public GcmMessageHandler() {
        super("GcmMessageHandler");
    }

    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        String messageS = extras.getString("message");
        String[] dataP = messageS.split("\n");
        for (String d: dataP){
            System.out.println("DATAA "+d);
        }
        user = dataP[0];
        message = dataP[1];
        looper = Looper.myLooper();
        if (looper==null){
            Looper.prepare();
        }
        GPSControl gpsControl = new GPSControl(this,false,false);
        gpsControl.execute();

        looper.loop();
        Location location = gpsControl.getLocation();

        double latitude = Double.parseDouble(dataP[2]);
        double longitude = Double.parseDouble(dataP[3]);
        double latitudeBlocked = location.getLatitude();
        double longitudeBlocked = location.getLongitude();

        distance = distFrom(latitude, longitude, latitudeBlocked, longitudeBlocked);
        Log.e("Distance", distFrom(latitude, longitude, latitudeBlocked, longitudeBlocked)+"");

        if (distance<150){
            onMessage(this, intent);
            GcmBroadcastReceiver.completeWakefulIntent(intent);
        }
    }

    public static void stopLoop(){
        if (looper!=null) {
            looper.quit();
        }
    }

    protected void onMessage(Context context, Intent intent) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        // Icono que tendra la notificacion
                        .setSmallIcon(R.drawable.ic_ic_launcher)
                                // Nombre de la notificacion (La que aparece en la barra)
                        .setTicker("Someone Block You!")
                                // Nombre de la notificacion (La que aparece en las notificaciones)
                        .setContentTitle("OnLife")
                        .setPriority(Notification.PRIORITY_HIGH)
                                // Texto con el mensaje de la notificacion
                        .setContentText(message);
        myNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        myNotificationManager.notify(0, mBuilder.build());
        Intent i = new Intent(context, InBlockActivity.class);
        i.putExtra("message",message);
        i.putExtra("distance",distance);
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

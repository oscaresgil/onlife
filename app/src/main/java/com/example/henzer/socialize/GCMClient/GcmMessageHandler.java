package com.example.henzer.socialize.GCMClient;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.henzer.socialize.R;
import com.google.android.gms.gcm.GoogleCloudMessaging;


public class GcmMessageHandler extends IntentService{
    private NotificationManager myNotificationManager;
    private String[] msg = new String[2];
    private Handler handler;
    //private int notificationId = 111;

    public GcmMessageHandler(){
        super("GcmMessageHandler");
    }

    public void onCreate(){
        super.onCreate();
        handler = new Handler();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        msg[0] = extras.getString("title");
        msg[1] = extras.getString("message");

        showToast();

        onMessage(this,intent);
        Log.i("GCM", "Received: (" + messageType + ") " + extras.getString("title"));
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }
    protected void onMessage(Context context, Intent intent) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        // Icono que tendra la notificacion
                        .setSmallIcon(R.drawable.ic_launcher)
                        // Nombre de la notificacion (La que aparece en la barra)
                        .setTicker("New Notification!")
                        // Nombre de la notificacion (La que aparece en las notificaciones)
                        .setContentTitle(msg[0])
                        .setPriority(Notification.PRIORITY_HIGH)
                        // Texto con el mensaje de la notificacion
                        .setContentText(msg[1]);
        myNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        // Este codigo servira para crear una notificacion y crear una nueva instancia de una actividad cualquiera.

       /* Intent resultIntent = new Intent(this,Notification.class);
        resultIntent.putExtra("notificationId",notificationId);
        //Retorna a la app
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Se agrega la app en el stack por el intento
        stackBuilder.addParentStack(Notification.class);
        // Se agrega el intent que inicia la actividad hasta la cima del stack
        stackBuilder.addNextIntent(resultIntent);
        // Solo una vez se puede apachar la notificacion
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT);
        // Inicia la actividad cuando el usuario apacha la notificacion
        mBuilder.setContentIntent(resultPendingIntent);*/

        myNotificationManager.notify(0, mBuilder.build());

    }

    public void showToast(){
        handler.post(new Runnable(){
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg[0], Toast.LENGTH_LONG).show();
            }
        });
    }
}

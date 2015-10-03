package com.objective4.app.onlife.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import static com.objective4.app.onlife.Controller.StaticMethods.activatePhoneBroadcast;

public class ServicePhoneState extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        activatePhoneBroadcast(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

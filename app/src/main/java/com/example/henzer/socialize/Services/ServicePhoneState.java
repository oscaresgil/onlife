package com.example.henzer.socialize.Services;

import android.app.IntentService;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.example.henzer.socialize.BroadcastReceivers.BroadcastReceiverPhoneStatus;
import com.example.henzer.socialize.Tasks.TaskChangeState;

import static com.example.henzer.socialize.Controller.StaticMethods.activatePhoneBroadcast;

public class ServicePhoneState extends Service {
    public static final String TAG = "ServicePhoneState";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG,"onCreate()");
        activatePhoneBroadcast(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG,"onStartCommand()");
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

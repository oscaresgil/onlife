package com.example.henzer.socialize.Services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.example.henzer.socialize.BroadcastReceivers.BroadcastReceiverPhoneStatus;
import com.example.henzer.socialize.Tasks.TaskChangeState;

/**
 * Created by Boris on 9/11/2015.
 */
public class ServicePhoneState extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        BroadcastReceiver broadcastReceiver = new BroadcastReceiverPhoneStatus();
        registerReceiver(broadcastReceiver,intentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String id = intent.getStringExtra("user");
        String state = intent.getStringExtra("state");

        if (state.equals("I")){
            new TaskChangeState().execute(id,"I");
        }else{
            new TaskChangeState().execute(id,"A");
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

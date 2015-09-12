package com.example.henzer.socialize.Services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.example.henzer.socialize.BroadcastReceivers.BroadcastReceiverPhoneStatus;
import com.example.henzer.socialize.Tasks.TaskChangeState;

/**
 * Created by Boris on 9/11/2015.
 */
public class ServicePhoneState extends Service {
    public static final String TAG = "ServicePhoneState";

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate()");
        super.onCreate();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        BroadcastReceiver broadcastReceiver = new BroadcastReceiverPhoneStatus();
        registerReceiver(broadcastReceiver,intentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String id="", state="";
        id = intent.getStringExtra("user");
        state = intent.getStringExtra("state");
        Log.i(TAG, "onStart(): id=" + id + " state: " +state);
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

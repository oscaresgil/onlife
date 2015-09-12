package com.example.henzer.socialize.BroadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.henzer.socialize.Activities.ActivityMain;
import com.example.henzer.socialize.Models.ModelPerson;
import com.example.henzer.socialize.Services.ServicePhoneState;
import com.example.henzer.socialize.Tasks.TaskChangeState;
import com.google.gson.Gson;

public class BroadcastReceiverPhoneStatus extends BroadcastReceiver{
    public static final String TAG = "BroadcastReceiverWake";

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(ActivityMain.MyPREFERENCES, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        ModelPerson userLogin = gson.fromJson(sharedPreferences.getString("userLogin", ""), ModelPerson.class);
        Log.i(TAG, "User: " + userLogin.toString());

        Intent i = new Intent(context, ServicePhoneState.class);
        i.putExtra("user",userLogin.getId());
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
            i.putExtra("state","I");
        }else if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
            i.putExtra("state","A");
        }
        context.startService(i);
    }
}

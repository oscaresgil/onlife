package com.example.henzer.socialize.BroadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.henzer.socialize.Activities.ActivityMain;
import com.example.henzer.socialize.Models.ModelPerson;
import com.example.henzer.socialize.Tasks.TaskChangeState;
import com.google.gson.Gson;

public class BroadcastReceiverPhoneStatus extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(ActivityMain.MyPREFERENCES, Context.MODE_PRIVATE);
        ModelPerson userLogin = new Gson().fromJson(sharedPreferences.getString("userLogin", ""), ModelPerson.class);

        String id = userLogin.getId();
        String state = "A";

        if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
            state = "A";
            new TaskChangeState().execute(id,state);
        }else if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
            state = "I";
            new TaskChangeState().execute(id,state);
        }
    }
}

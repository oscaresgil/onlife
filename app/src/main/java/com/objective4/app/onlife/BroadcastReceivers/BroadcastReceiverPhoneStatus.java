package com.objective4.app.onlife.BroadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.objective4.app.onlife.Activities.ActivityMain;
import com.objective4.app.onlife.Models.ModelPerson;
import com.objective4.app.onlife.Tasks.TaskChangeState;

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
        }else if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF) || intent.getAction().equals(Intent.ACTION_SHUTDOWN)){
            state = "I";
            new TaskChangeState().execute(id,state);
        }
    }
}

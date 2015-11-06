package com.objective4.app.onlife.BroadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.objective4.app.onlife.Tasks.TaskChangeState;

public class BroadcastReceiverPing extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String id = intent.getExtras().getString("id");
        new TaskChangeState().execute(id,"A");
    }
}

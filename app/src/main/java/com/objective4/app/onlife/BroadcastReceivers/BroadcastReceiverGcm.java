package com.objective4.app.onlife.BroadcastReceivers;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.objective4.app.onlife.Services.GcmMessageHandler;

public class BroadcastReceiverGcm extends WakefulBroadcastReceiver {

    @Override public void onReceive(Context context, Intent intent) {
        //Determina explicitamente que GcmMessageHandler hara el intent
        ComponentName comp = new ComponentName(context.getPackageName(), GcmMessageHandler.class.getName());
        // Inicia el servicio, manteniendo el dispositivo prendido cuando es lanzado el intent
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }
}

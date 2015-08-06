package com.example.henzer.socialize.GCMClient;

import android.content.Context;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public final class GcmHelper {
    private static GoogleCloudMessaging gcm = null;
    private static Context context = null;

    public GcmHelper(Context context) {
        this.context = context;
    }

    private static boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            return false;
        }
        return true;
    }

    public String GCMRegister(String SENDER_ID) throws Exception {
        String regid = "";
        //Check if Play store services are available.
        if (!checkPlayServices())
            throw new Exception("Google Play Services not supported. Please install and configure Google Play Store.");

        if (gcm == null) {
            gcm = GoogleCloudMessaging.getInstance(context);
        }
        regid = gcm.register(SENDER_ID);

        return regid;
    }
}
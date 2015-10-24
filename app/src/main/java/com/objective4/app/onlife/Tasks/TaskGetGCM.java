package com.objective4.app.onlife.Tasks;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.objective4.app.onlife.Activities.ActivityMain;
import com.objective4.app.onlife.GCMClient.GCMHelper;
import com.objective4.app.onlife.R;

import java.io.IOException;

import static com.objective4.app.onlife.Controller.StaticMethods.isNetworkAvailable;
import static com.objective4.app.onlife.Controller.StaticMethods.makeSnackbar;


public class TaskGetGCM extends AsyncTask<Void, Void, String> {
    private Context context;
    private SharedPreferences sharedPreferences;

    public TaskGetGCM(Context context){
        this.context = context;
    }

    @Override
    protected String doInBackground(Void... params) {
        String msg;
        try {
            GCMHelper gcmRegistrationHelper = new GCMHelper(context);
            String gcmRegID = gcmRegistrationHelper.GCMRegister(ActivityMain.PROJECT_NUMBER);
            msg = gcmRegID;
            return msg;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onPostExecute(String idMSG) {
        if (isNetworkAvailable((Activity)context)){
            sharedPreferences = context.getSharedPreferences(ActivityMain.MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (idMSG == null) {
                editor.putBoolean("update_playservice", true).apply();
            } else {
                editor.putString("gcmId", idMSG).apply();
                editor.remove("update_playservice").apply();
            }
        }else{
            makeSnackbar(context,((Activity) context).findViewById(R.id.ActivityMain_ImageViewLogo), R.string.no_connection, Snackbar.LENGTH_LONG);
        }
}}

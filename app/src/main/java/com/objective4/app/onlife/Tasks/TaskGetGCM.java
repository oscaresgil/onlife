package com.objective4.app.onlife.Tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.objective4.app.onlife.Activities.ActivityMain;
import com.objective4.app.onlife.GCMClient.GCMHelper;

import java.io.IOException;


public class TaskGetGCM extends AsyncTask<Void, Void, String> {
    private Context context;
    private SharedPreferences sharedPreferences;

    public TaskGetGCM(Context context){
        this.context = context;
    }

    @Override
    protected String doInBackground(Void... params) {
        String msg = "";
        try {
            GCMHelper gcmRegistrationHelper = new GCMHelper(context);
            String gcmRegID = gcmRegistrationHelper.GCMRegister(ActivityMain.PROJECT_NUMBER);
            msg = gcmRegID;
            return msg;
        } catch (IOException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onPostExecute(String idMSG) {
        sharedPreferences = context.getSharedPreferences(ActivityMain.MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("gcmId", idMSG);
        editor.commit();
}}

package com.example.henzer.socialize.Tasks;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.example.henzer.socialize.Activities.ActivityMain;
import com.example.henzer.socialize.GCMClient.GCMHelper;
import com.kenny.snackbar.SnackBar;

import java.io.IOException;


public class TaskGetGCM extends AsyncTask<Void, Void, String> {

    private static final String TAG = "GetGCM";
    private Context context;
    private SharedPreferences sharedPreferences;

    public TaskGetGCM(Context context){
        this.context = context;
    }

    /*@Override
    protected void onPreExecute(){
        super.onPreExecute();
        Log.e("MainActivity", "Mostrando el Progress Dialog");
        materialDialog = new MaterialDialog.Builder(context)
                .title("Get GCM")
                .content(R.string.dialog_title_loading_friends)
                .progress(true,0)
                .widgetColorRes(R.color.orange_light)
                .cancelable(false)
                .show();
    }*/

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
        SnackBar.show((Activity)context, "TaskGCMOnPost "+idMSG);
}}

package com.objective4.app.onlife.Tasks;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;

import com.objective4.app.onlife.Activities.ActivityMain;
import com.objective4.app.onlife.GCMClient.GCMHelper;
import com.objective4.app.onlife.R;

import java.net.ConnectException;

import static com.objective4.app.onlife.Controller.StaticMethods.isNetworkAvailable;
import static com.objective4.app.onlife.Controller.StaticMethods.makeSnackbar;

public class TaskGetGCM extends AsyncTask<Void, Void, String> {
    private Context context;
    private SharedPreferences sharedPreferences;
    private boolean connectionFailure = false;

    public TaskGetGCM(Context context){
        this.context = context;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            return new GCMHelper(context).GCMRegister(ActivityMain.PROJECT_NUMBER);
        } catch (ConnectException ignored) {
            connectionFailure = true;
        } catch(Exception ignored) {}
        return null;
    }

    @Override
    public void onPostExecute(String idMSG) {
        if (isNetworkAvailable((Activity)context)){
            sharedPreferences = context.getSharedPreferences(ActivityMain.MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (idMSG == null) {
                if (connectionFailure) makeSnackbar(context,((Activity) context).findViewById(R.id.ActivityMain_ImageViewLogo), R.string.no_connection, Snackbar.LENGTH_LONG);
                else editor.putBoolean("update_playservice", true).apply();
            } else {
                editor.putString("gcmId", idMSG).apply();
                editor.remove("update_playservice").apply();
            }
        }else{
            makeSnackbar(context,((Activity) context).findViewById(R.id.ActivityMain_ImageViewLogo), R.string.no_connection, Snackbar.LENGTH_LONG);
        }
}}

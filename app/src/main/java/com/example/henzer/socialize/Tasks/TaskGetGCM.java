package com.example.henzer.socialize.Tasks;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.henzer.socialize.Activities.ActivityMain;
import com.example.henzer.socialize.GCMClient.GcmHelper;
import com.example.henzer.socialize.Models.ModelPerson;
import com.example.henzer.socialize.R;
import com.kenny.snackbar.SnackBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


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
            GcmHelper gcmRegistrationHelper = new GcmHelper(context);
            String gcmRegID = gcmRegistrationHelper.GCMRegister(ActivityMain.PROJECT_NUMBER);
            msg = gcmRegID;
            Log.i("GCM", gcmRegID);
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
        //AddNewUser addNewUser = new AddNewUser(MainActivity.this);
/*        userLogin.setId_phone(idMSG);
        TaskLoadAllInformation load = new TaskLoadAllInformation(context, materialDialog);
        List<ModelPerson> enviados = new ArrayList();
        enviados.add(userLogin);
        enviados.addAll(friends);
        JSONObject data = null;
        try {
            data = load.execute(enviados).get();
            Log.e(TAG, data.toString());

            boolean error = data.getBoolean("error");
            String mensaje = data.getString("message");
            if(error == false){
                sharedPreferences = context.getSharedPreferences(ActivityMain.MyPREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                data.put("activity_groups", new JSONArray());
                editor.putString("session", data.toString());
                editor.commit();

                //gotoHome();
            }else{
                SnackBar.show((Activity)context, mensaje);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/
}}

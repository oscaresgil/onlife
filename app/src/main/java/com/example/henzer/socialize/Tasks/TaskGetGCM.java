package com.example.henzer.socialize.Tasks;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.example.henzer.socialize.GCMClient.GcmHelper;
import com.example.henzer.socialize.Models.ModelPerson;
import com.kenny.snackbar.SnackBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class TaskGetGCM extends AsyncTask<Void, Void, String> {

    private static final String PROJECT_NUMBER = "194566212765";
    private static final String TAG = "GetGCM";
    public static final String MyPREFERENCES = "MyPrefs";
    private Context context;
    private ModelPerson userLogin;
    private List<ModelPerson> friends;
    private SharedPreferences sharedPreferences;

    public TaskGetGCM(Context context, ModelPerson userLogin, List<ModelPerson> friends, SharedPreferences sharedPreferences){
        this.context = context;
        this.userLogin = userLogin;
        this.friends = friends;
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    protected String doInBackground(Void... params) {
        String msg = "";
        try {
            GcmHelper gcmRegistrationHelper = new GcmHelper(context);
            String gcmRegID = gcmRegistrationHelper.GCMRegister(PROJECT_NUMBER);
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
        //AddNewUser addNewUser = new AddNewUser(MainActivity.this);
        userLogin.setId_phone(idMSG);
        TaskLoadAllInformation load = new TaskLoadAllInformation(context);
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
                sharedPreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
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
    }
}

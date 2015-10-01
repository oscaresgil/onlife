package com.example.henzer.socialize.Tasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.henzer.socialize.Controller.JSONParser;
import com.example.henzer.socialize.Models.ModelPerson;
import com.example.henzer.socialize.R;
import com.google.gson.Gson;

import net.steamcrafted.loadtoast.LoadToast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.example.henzer.socialize.Controller.StaticMethods.animationEnd;
import static com.example.henzer.socialize.Controller.StaticMethods.hideSoftKeyboard;

public class TaskSendNotification extends AsyncTask<ModelPerson, String, Boolean>{
    public final static String TAG = "TaskSendNotification";
    private Activity context;
    private LoadToast toast;
    private JSONParser jsonParser;
    private String message="",actualUser, gifName="";

    public TaskSendNotification(Activity c, String actualUser, String message, String gifName){
        this.context = c;
        this.message = message;
        this.actualUser = actualUser;
        this.gifName = gifName;
        jsonParser = new JSONParser();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        toast = new LoadToast(context)
                .setText(context.getResources().getString(R.string.blocking))
                .setTextColor(context.getResources().getColor(R.color.black))
                .setTranslationY(100)
                .setProgressColor(context.getResources().getColor(R.color.orange_light))
                .show();
    }

    @Override
    protected Boolean doInBackground(ModelPerson... params) {
        //Parametros a enviar
        List<NameValuePair> p = new ArrayList<>();
        for(int i = 0; i<params.length; i++){
            p.add(new BasicNameValuePair("id[]",params[i].getId_phone()));
        }

        p.add(new BasicNameValuePair("userName", actualUser));
        p.add(new BasicNameValuePair("message", message));
        p.add(new BasicNameValuePair("gifName", gifName));

        JSONObject json = jsonParser.makeHttpRequest("http://104.236.74.55/onlife/gcm.php", "POST", p);
        Log.e(TAG, "Response: "+json.toString());
        //for (int i=0; i<30000; i++);
        try {
            boolean error = json.getBoolean("error");
            if(error==false){
                return true;
            }
            message = json.getString("message");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result){
        Log.e("MainActivity", "Quitando el Progress Dialog");
        toast.success();
    }
}


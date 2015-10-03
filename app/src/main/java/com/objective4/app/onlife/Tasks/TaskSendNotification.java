package com.objective4.app.onlife.Tasks;

import android.app.Activity;
import android.os.AsyncTask;

import com.objective4.app.onlife.Controller.JSONParser;
import com.objective4.app.onlife.Models.ModelPerson;
import com.objective4.app.onlife.R;

import net.steamcrafted.loadtoast.LoadToast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TaskSendNotification extends AsyncTask<ModelPerson, String, Boolean>{
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
        toast.success();
    }
}


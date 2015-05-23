package com.example.henzer.socialize.Controller;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.henzer.socialize.Models.Person;
import com.example.henzer.socialize.R;

import net.steamcrafted.loadtoast.LoadToast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SendNotification extends AsyncTask<Person, String, Boolean>{
    private LoadToast toast;
    private Context context;
    private JSONParser jsonParser;
    private String message;
    private String data;

    public SendNotification(Context c, String message, String data){
        this.context = c;
        this.message = message;
        this.data = data;
        jsonParser = new JSONParser();
    }

    @Override
    protected void onPreExecute(){
        super.onPreExecute();
        toast = new LoadToast(context)
            .setText("Blocking...")
            .setTextColor(context.getResources().getColor(R.color.black))
            .setTranslationY(100)
            .setProgressColor(context.getResources().getColor(R.color.orange_light))
            .show();
    }

    @Override
    protected Boolean doInBackground(Person... params) {
        //Parametros a enviar
        List<NameValuePair> p = new ArrayList<>();
        for(int i = 0; i<params.length; i++){
            p.add(new BasicNameValuePair("id[]",params[i].getId_phone()));
        }
        p.add(new BasicNameValuePair("message", message));
        p.add(new BasicNameValuePair("data", data));

        JSONObject json = jsonParser.makeHttpRequest("http://socialize.comyr.com/Prueba/gcm.php", "POST", p);
        Log.e("AddNewGroup", json.toString());

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


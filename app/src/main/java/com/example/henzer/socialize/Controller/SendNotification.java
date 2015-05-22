package com.example.henzer.socialize.Controller;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.henzer.socialize.Models.Person;
import com.example.henzer.socialize.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SendNotification extends AsyncTask<Person, String, Boolean>{
    private MaterialDialog pDialog;
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
        pDialog= new MaterialDialog.Builder(context)
            .title("Blocking..")
            .progress(true,0)
            .backgroundColorRes(R.color.transparent)
            .widgetColorRes(R.color.orange_light)
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
        if (pDialog.isShowing()){
            pDialog.dismiss();
        }

        new MaterialDialog.Builder(context)
                .title("Contact blocked!")
                .positiveText("OK")
                .positiveColorRes(R.color.orange_light)
                .show();

    }
}


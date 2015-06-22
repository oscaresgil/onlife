package com.example.henzer.socialize.Tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.henzer.socialize.Controller.JSONParser;
import com.example.henzer.socialize.Models.Person;

import net.steamcrafted.loadtoast.LoadToast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TaskSendNotification extends AsyncTask<Person, String, Boolean>{
    private LoadToast toast;
    private Context context;
    private JSONParser jsonParser;
    private String data;
    private String message;
    private String longitude;
    private String latitude;
    private String actualUser;

    public TaskSendNotification(Context c, String message, String actualUser, double latitude, double longitude, LoadToast toast){
        this.context = c;
        this.toast = toast;
        this.message = message;
        jsonParser = new JSONParser();
        this.latitude = ""+latitude;
        this.longitude = ""+longitude;
        this.actualUser = actualUser;
    }

    @Override
    protected Boolean doInBackground(Person... params) {
        //Parametros a enviar
        List<NameValuePair> p = new ArrayList<>();
        for(int i = 0; i<params.length; i++){
            p.add(new BasicNameValuePair("id[]",params[i].getId_phone()));
            Log.i("IDPHONE",params[i].getId_phone());
        }

        p.add(new BasicNameValuePair("message", actualUser+"\n"+message+"\n"+latitude+"\n"+longitude));
        p.add(new BasicNameValuePair("data", ""));

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


package com.example.henzer.socialize.Tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.henzer.socialize.Controller.JSONParser;
import com.example.henzer.socialize.Models.ModelPerson;
import com.example.henzer.socialize.R;

import net.steamcrafted.loadtoast.LoadToast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TaskSendNotification extends AsyncTask<ModelPerson, String, Boolean>{
    private Context context;
    private LoadToast toast;
    private JSONParser jsonParser;
    private String message="",actualUser, gifName="";

    public TaskSendNotification(Context c, String message, String actualUser, String gifName){
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
        Log.e("FriendBlocked",params.toString());
        for(int i = 0; i<params.length; i++){
            p.add(new BasicNameValuePair("id[]",params[i].getId_phone()));
            Log.i("IDPHONE",params[i].getId_phone());
        }

        p.add(new BasicNameValuePair("message", actualUser+"\n"+message+"\n"+gifName));
        p.add(new BasicNameValuePair("data", ""));

        JSONObject json = jsonParser.makeHttpRequest("http://104.236.74.55/onlife/person.php", "POST", p);
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


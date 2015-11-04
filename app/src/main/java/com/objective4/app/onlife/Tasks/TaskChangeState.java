package com.objective4.app.onlife.Tasks;

import android.os.AsyncTask;

import com.objective4.app.onlife.Controller.ConnectionController;

import org.json.JSONObject;

public class TaskChangeState extends AsyncTask<String,Void,Void> {

    @Override
    protected Void doInBackground(String ... params) {
        try{
            JSONObject p = new JSONObject();
            p.put("tag", "changeState");
            p.put("id", params[0]);
            p.put("state", params[1]);
            p.put("location","");
            new ConnectionController().makeHttpRequest(p);
        }catch(Exception ignored){
        }
        return null;
    }
}

package com.example.henzer.socialize.Tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.example.henzer.socialize.Activities.ActivityMain;
import com.example.henzer.socialize.Controller.JSONParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TaskChangeState extends AsyncTask<String,Void,Boolean> {
    private JSONParser jsonParser;

    public TaskChangeState() {
        jsonParser = new JSONParser();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(String ... params) {
        String id = params[0];
        String state = params[1];

        List<NameValuePair> p = new ArrayList<>();
        p.add(new BasicNameValuePair("tag", "changeState"));
        p.add(new BasicNameValuePair("id", id));
        p.add(new BasicNameValuePair("state",state));

        try{
            JSONObject response = null;
            response = jsonParser.makeHttpRequest(ActivityMain.SERVER_URL, "POST", p);
            return response.getBoolean("error");
        }catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
    }
}

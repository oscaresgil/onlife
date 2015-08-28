package com.example.henzer.socialize.Tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.henzer.socialize.Activities.ActivityMain;
import com.example.henzer.socialize.Controller.JSONParser;
import com.example.henzer.socialize.Models.ModelPerson;
import com.example.henzer.socialize.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
public class TaskAddNewUser extends AsyncTask<ModelPerson, ModelPerson, Boolean> {
    private JSONParser jsonParser;

    public TaskAddNewUser(){
        jsonParser = new JSONParser();
    }

    @Override
    protected Boolean doInBackground(ModelPerson... params) {
        ModelPerson n = params[0];
        //Parametros a enviar
        List<NameValuePair> p = new ArrayList<>();
        p.add(new BasicNameValuePair("tag", "newUser"));
        p.add(new BasicNameValuePair("id", n.getId() + ""));
        p.add(new BasicNameValuePair("id_phone", n.getId_phone()));
        p.add(new BasicNameValuePair("photo", n.getPhoto()));
        p.add(new BasicNameValuePair("name", n.getName()));
        p.add(new BasicNameValuePair("state", n.getState()));
        try {
            JSONObject json = jsonParser.makeHttpRequest(ActivityMain.SERVER_URL, "POST", p);
            boolean error = json.getBoolean("error");
            return error;
        }catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
    }

}

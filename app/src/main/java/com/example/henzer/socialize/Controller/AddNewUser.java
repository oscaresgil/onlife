package com.example.henzer.socialize.Controller;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.henzer.socialize.Models.Person;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Henzer on 01/05/2015.
 */
public class AddNewUser extends AsyncTask<Person, Person, Boolean> {
    private ProgressDialog pDialog;
    private Context context;
    private JSONParser jsonParser;

    public AddNewUser(Context c){
        this.context = c;
        jsonParser = new JSONParser();
    }

    @Override
    protected void onPreExecute(){
        super.onPreExecute();
        Log.e("MainActivity", "Mostrando el Progress Dialog");
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Save new user...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();
    }

    @Override
    protected Boolean doInBackground(Person... params) {
        Person n = params[0];
        //Parametros a enviar
        List<NameValuePair> p = new ArrayList<>();
        p.add(new BasicNameValuePair("tag", "newUser"));
        p.add(new BasicNameValuePair("id", n.getId()+""));
        p.add(new BasicNameValuePair("id_phone", n.getId_phone()));
        p.add(new BasicNameValuePair("photo", n.getPhoto()));
        p.add(new BasicNameValuePair("name", n.getName()));
        p.add(new BasicNameValuePair("state", n.getState()));

        JSONObject json = jsonParser.makeHttpRequest("http://socialize.comyr.com/Prueba/person.php", "POST", p);
        Log.d("Create Response", json.toString());
        return true;
    }

    @Override
    protected void onPostExecute(Boolean param){
        Log.e("MainActivity", "Quitando el Progress Dialog");
        pDialog.dismiss();
    }


}
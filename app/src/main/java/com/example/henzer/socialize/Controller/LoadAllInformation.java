package com.example.henzer.socialize.Controller;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.henzer.socialize.Models.Person;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Henzer on 01/05/2015.
 */
public class LoadAllInformation extends AsyncTask<String, String, JSONObject>{
    private ProgressDialog pDialog;
    private Context context;
    private JSONParser jsonParser;

    public LoadAllInformation(Context c){
        this.context = c;
        jsonParser = new JSONParser();
    }

    @Override
    protected void onPreExecute(){
        super.onPreExecute();
        Log.e("MainActivity", "Mostrando el Progress Dialog");
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Loading Friends...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        //Parametros a enviar
        List<NameValuePair> p = new ArrayList<>();
        p.add(new BasicNameValuePair("tag", "getFriends"));
        p.add(new BasicNameValuePair("myId", params[0]));
        for(int i = 1; i<params.length; i++){
            p.add(new BasicNameValuePair("id[]", params[i]));
        }

        JSONObject json = jsonParser.makeHttpRequest("http://socialize.comyr.com/Prueba/person.php", "POST", p);
        Log.e("Create Response", json.toString());
        return json;
    }

    @Override
    protected void onPostExecute(JSONObject param){
        Log.e("MainActivity", "Quitando el Progress Dialog");
        pDialog.dismiss();
    }

}

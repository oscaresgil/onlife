package com.example.henzer.socialize.Tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.henzer.socialize.Controller.JSONParser;
import com.example.henzer.socialize.Models.ModelPerson;
import com.example.henzer.socialize.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TaskLoadAllInformation extends AsyncTask<List<ModelPerson>, String, JSONObject>{
    private MaterialDialog materialDialog;
    private Context context;
    private JSONParser jsonParser;

    public TaskLoadAllInformation(Context c){
        this.context = c;
        jsonParser = new JSONParser();
    }

    @Override protected void onPreExecute(){
        super.onPreExecute();
        Log.e("MainActivity", "Mostrando el Progress Dialog");
        materialDialog = new MaterialDialog.Builder(context)
                .title(R.string.dialog_message_loading_information_friends)
                .content(R.string.dialog_title_loading_friends)
                .progress(true,0)
                .widgetColorRes(R.color.orange_light)
                .cancelable(false)
                .show();
    }

    @Override protected JSONObject doInBackground(List<ModelPerson>... params) {
        ModelPerson me = params[0].get(0);

        List<NameValuePair> p = new ArrayList<>();
        p.add(new BasicNameValuePair("tag", "newUser"));
        p.add(new BasicNameValuePair("id", me.getId()+""));
        p.add(new BasicNameValuePair("id_phone", me.getId_phone()));
        p.add(new BasicNameValuePair("photo", me.getPhoto()));
        p.add(new BasicNameValuePair("name", me.getName()));
        p.add(new BasicNameValuePair("state", me.getState()));

        JSONObject json = jsonParser.makeHttpRequest("http://socialize.comyr.com/Prueba/person.php", "POST", p);
        Log.e("LoadAllInformation", json.toString());
        try {
            boolean error = json.getBoolean("error");
            if(error==false){
                //Se obtiene la informacion completa de sus amigos.
                List<NameValuePair> p2 = new ArrayList<>();
                p2.add(new BasicNameValuePair("tag", "getFriends"));
                p2.add(new BasicNameValuePair("myId", me.getId()));
                for(int i = 1; i<params[0].size(); i++){
                    p2.add(new BasicNameValuePair("id[]", params[0].get(i).getId()));
                }
                jsonParser = new JSONParser();
                JSONObject json2 = jsonParser.makeHttpRequest("http://socialize.comyr.com/Prueba/person.php", "POST", p2);
                Log.e("Create Response", json2.toString());
                return json2;
            }else{
                Log.e("Create Response", json.toString());
                return json;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("Create Response", json.toString());
        return json;
    }

    @Override protected void onPostExecute(JSONObject param){
        Log.e("MainActivity", "Quitando el Progress Dialog");
        materialDialog.cancel();
        String mensaje = null;
        try {
            mensaje = param.getString("message");
            Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

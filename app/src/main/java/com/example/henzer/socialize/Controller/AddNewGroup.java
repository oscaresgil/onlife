package com.example.henzer.socialize.Controller;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.henzer.socialize.Models.Group;
import com.example.henzer.socialize.Models.Person;
import com.example.henzer.socialize.Models.SessionData;
import com.example.henzer.socialize.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Henzer on 03/05/2015.
 */
public class AddNewGroup extends AsyncTask<Group, String, Group> {
    private MaterialDialog pDialog;
    private Context context;
    private JSONParser jsonParser;
    private String message;
    private SessionData sessionData;

    public AddNewGroup(Context c){
        this.context = c;
        jsonParser = new JSONParser();
    }

    @Override
    protected void onPreExecute(){
        super.onPreExecute();
        Log.e("MainActivity", "Mostrando el Progress Dialog");
        pDialog = new MaterialDialog.Builder(context)
            .title("Creating Group..")
            .content("Please wait..")
            .progress(true,0)
            .widgetColorRes(R.color.orange_light)
            .show();
    }

    @Override
    protected Group doInBackground(Group... params) {
        Group n = params[0];

        //Parametros a enviar
        List<NameValuePair> p = new ArrayList<>();
        p.add(new BasicNameValuePair("tag", "newGroup"));
        p.add(new BasicNameValuePair("name", n.getName()));
        p.add(new BasicNameValuePair("photo", n.getNameImage()));
        p.add(new BasicNameValuePair("limit", n.getLimit() + ""));
        p.add(new BasicNameValuePair("state", n.getState()));

        for(Person person: n.getFriendsInGroup()){
            p.add(new BasicNameValuePair("idPeople[]", person.getId()));
        }

        JSONObject json = jsonParser.makeHttpRequest("http://socialize.comyr.com/Prueba/group.php", "POST", p);
        Log.e("AddNewGroup", json.toString());

        try {
            boolean error = json.getBoolean("error");
            if(error==false){
                int id = json.getInt("id");
                n.setId(id);
            }else{
                n.setId(-1);
            }
            message = json.getString("message");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return n;
    }

    @Override
    protected void onPostExecute(Group newG){
        Log.e("MainActivity", "Quitando el Progress Dialog");
        pDialog.dismiss();
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}

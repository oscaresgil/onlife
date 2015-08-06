package com.example.henzer.socialize.Tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.henzer.socialize.Controller.JSONParser;
import com.example.henzer.socialize.Models.ModelGroup;
import com.example.henzer.socialize.Models.ModelPerson;
import com.example.henzer.socialize.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TaskAddNewGroup extends AsyncTask<ModelGroup, String, ModelGroup> {
    private MaterialDialog pDialog;
    private Context context;
    private JSONParser jsonParser;
    private String message;

    public TaskAddNewGroup(Context c){
        this.context = c;
        jsonParser = new JSONParser();
    }

    @Override
    protected void onPreExecute(){
        super.onPreExecute();
        Log.e("MainActivity", "Mostrando el Progress Dialog");
        pDialog = new MaterialDialog.Builder(context)
            .title(R.string.dialog_title_creating_group)
            .content(R.string.dialog_title_loading_friends)
            .progress(true,0)
            .widgetColorRes(R.color.orange_light)
            .show();
    }

    @Override
    protected ModelGroup doInBackground(ModelGroup... params) {
        ModelGroup n = params[0];

        //Parametros a enviar
        List<NameValuePair> p = new ArrayList<>();
        p.add(new BasicNameValuePair("tag", "newGroup"));
        p.add(new BasicNameValuePair("name", n.getName()));
        p.add(new BasicNameValuePair("photo", n.getNameImage()));
        p.add(new BasicNameValuePair("limit", n.getLimit() + ""));
        p.add(new BasicNameValuePair("state", n.getState()));

        for(ModelPerson modelPerson : n.getFriendsInGroup()){
            p.add(new BasicNameValuePair("idPeople[]", modelPerson.getId()));
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
    protected void onPostExecute(ModelGroup newG){
        Log.e("MainActivity", "Quitando el Progress Dialog");
        pDialog.dismiss();
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}

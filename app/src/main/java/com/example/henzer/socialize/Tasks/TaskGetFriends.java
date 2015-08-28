package com.example.henzer.socialize.Tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.henzer.socialize.Activities.ActivityHome;
import com.example.henzer.socialize.Activities.ActivityMain;
import com.example.henzer.socialize.Controller.JSONParser;
import com.example.henzer.socialize.Models.ModelPerson;
import com.example.henzer.socialize.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TaskGetFriends extends AsyncTask<String, Void, ArrayList<ModelPerson>> {
    private MaterialDialog dialog;
    private Context context;
    private JSONParser jsonParser;

    public TaskGetFriends(Context c, MaterialDialog dialog){
        this.dialog = dialog;
        this.context = c;
        jsonParser = new JSONParser();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog.setTitle(context.getResources().getString(R.string.loading_friends));
        dialog.show();
    }

    @Override
    protected ArrayList<ModelPerson> doInBackground(String... params) {
        String id = params[0];
        List<NameValuePair> p = new ArrayList<>();
        p.add(new BasicNameValuePair("tag", "getFriends"));
        p.add(new BasicNameValuePair("myId", id));
        JSONObject jsonFriends = null;
        try{
            jsonFriends = jsonParser.makeHttpRequest(ActivityMain.SERVER_URL, "POST", p);
            Gson gson = new Gson();
            ArrayList<ModelPerson> friends = gson.fromJson(jsonFriends.getString("friends"), (new TypeToken<ArrayList<ModelPerson>>(){}.getType()));
            return friends;
        }catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(ArrayList<ModelPerson> friends) {
        super.onPostExecute(friends);
        ActivityHome.modelSessionData.setFriends(friends);
        dialog.dismiss();
    }

}

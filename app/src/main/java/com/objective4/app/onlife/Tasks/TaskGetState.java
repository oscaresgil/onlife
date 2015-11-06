package com.objective4.app.onlife.Tasks;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.objective4.app.onlife.Controller.ConnectionController;

import org.json.JSONObject;

public class TaskGetState extends AsyncTask<String,Void,JSONObject> {
    private Context context;

    public TaskGetState(Context context) {
        this.context = context;
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        JSONObject object = new JSONObject();
        try {
            object.put("tag","getStateLight");
            object.put("myId", params[0]);
            return new ConnectionController().makeHttpRequest(object);
        } catch (Exception ignored) {
        }
        return null;
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        if (jsonObject!=null) {
            Intent intent = new Intent("com.objective4.app.onlife.Fragments.Social.FragmentContacts");
            intent.putExtra("tag","friends_state");
            intent.putExtra("friends_state", jsonObject.toString());
            context.sendBroadcast(intent);
        }
    }
}

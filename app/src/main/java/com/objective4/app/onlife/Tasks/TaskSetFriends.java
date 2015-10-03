package com.objective4.app.onlife.Tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.afollestad.materialdialogs.MaterialDialog;
import com.objective4.app.onlife.Activities.ActivityMain;
import com.objective4.app.onlife.Controller.JSONParser;
import com.objective4.app.onlife.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TaskSetFriends extends AsyncTask<ArrayList<String>, Void, String> {
    private MaterialDialog dialog;
    private Context context;
    private JSONParser jsonParser;

    public TaskSetFriends(Context c){
        this.context = c;
        jsonParser = new JSONParser();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new MaterialDialog.Builder(context)
                .title(context.getResources().getString(R.string.dialog_please_wait))
                .content(context.getResources().getString(R.string.uploading_friends))
                .progress(true,10)
                .widgetColor(context.getResources().getColor(R.color.orange_light))
                .cancelable(false)
                .show();
    }

    @Override
    protected String doInBackground(ArrayList<String>... params) {
        String id = params[0].get(0);
        List<NameValuePair> p = new ArrayList<>();
        p.add(new BasicNameValuePair("tag", "setFriends"));
        p.add(new BasicNameValuePair("myId", id));

        for(int i = 1; i<params[0].size(); i++){
            p.add(new BasicNameValuePair("id_friends[]", params[0].get(i)));
        }
        try{
            JSONObject response = null;
            response = jsonParser.makeHttpRequest(ActivityMain.SERVER_URL, "POST", p);
            boolean error = response.getBoolean("error");
            return id;
        }catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String userId) {
        super.onPostExecute(userId);
        dialog.dismiss();
    }
}

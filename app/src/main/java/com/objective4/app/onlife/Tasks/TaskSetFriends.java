package com.objective4.app.onlife.Tasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;

import com.afollestad.materialdialogs.MaterialDialog;
import com.objective4.app.onlife.Activities.ActivityMain;
import com.objective4.app.onlife.Controller.JSONParser;
import com.objective4.app.onlife.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

import static com.objective4.app.onlife.Controller.StaticMethods.makeSnackbar;

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
                .widgetColor(context.getResources().getColor(R.color.accent))
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
            JSONObject response =  jsonParser.makeHttpRequest(ActivityMain.SERVER_URL, "POST", p);
            boolean error = response.getBoolean("error");
            return id;
        }catch(ConnectException e){
            e.printStackTrace();
            return null;
        }catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String userId) {
        super.onPostExecute(userId);
        if (userId == null) makeSnackbar(context,((Activity)context).findViewById(R.id.ActivityMain_ImageViewLogo),R.string.error, Snackbar.LENGTH_SHORT);
        else dialog.dismiss();
    }
}

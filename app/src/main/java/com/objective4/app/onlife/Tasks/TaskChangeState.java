package com.objective4.app.onlife.Tasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.kenny.snackbar.SnackBar;
import com.objective4.app.onlife.Activities.ActivityMain;
import com.objective4.app.onlife.Controller.JSONParser;
import com.objective4.app.onlife.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

public class TaskChangeState extends AsyncTask<String,Void,Boolean> {
    private Context context;
    private JSONParser jsonParser;

    public TaskChangeState(Context context) {
        jsonParser = new JSONParser();
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(String ... params) {
        String id = params[0];
        String state = params[1];

        List<NameValuePair> p = new ArrayList<>();
        p.add(new BasicNameValuePair("tag", "changeState"));
        p.add(new BasicNameValuePair("id", id));
        p.add(new BasicNameValuePair("state",state));

        try{
            JSONObject response = jsonParser.makeHttpRequest(ActivityMain.SERVER_URL, "POST", p);
            return response.getBoolean("error");
        }catch(ConnectException e){
            SnackBar.show((Activity) context, context.getResources().getString(R.string.no_connection));
            return null;
        }catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
    }
}

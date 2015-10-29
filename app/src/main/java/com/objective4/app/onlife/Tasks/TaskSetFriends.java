package com.objective4.app.onlife.Tasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;

import com.afollestad.materialdialogs.MaterialDialog;
import com.objective4.app.onlife.Activities.ActivityMain;
import com.objective4.app.onlife.Controller.ConnectionController;
import com.objective4.app.onlife.R;
import org.json.JSONObject;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;

import static com.objective4.app.onlife.Controller.StaticMethods.makeSnackbar;

public class TaskSetFriends extends AsyncTask<ArrayList<String>, Void, String> {
    private MaterialDialog dialog;
    private Context context;
    private ConnectionController controller;
    private boolean connectionError = false;

    public TaskSetFriends(Context c){
        this.context = c;
        controller = new ConnectionController();
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
        HashMap<String,String> p = new HashMap<>();
        p.put("tag", "setFriends");
        p.put("myId", id);

        for(int i = 1; i<params[0].size(); i++){
            p.put("id_friends[]", params[0].get(i));
        }
        try{
            JSONObject response =  controller.makeHttpRequest("person.php", p);
            boolean error = response.getBoolean("error");
            return id;
        }catch(ConnectException e){
            connectionError = true;
        }catch(Exception ignored){
        }
        return null;
    }

    @Override
    protected void onPostExecute(String userId) {
        super.onPostExecute(userId);
        if (userId == null) {
            if (connectionError)makeSnackbar(context, ((Activity) context).findViewById(R.id.ActivityMain_ImageViewLogo), R.string.no_connection, Snackbar.LENGTH_LONG);
            else makeSnackbar(context, ((Activity) context).findViewById(R.id.ActivityMain_ImageViewLogo), R.string.error, Snackbar.LENGTH_LONG);
        }
        else {
            if (dialog.isShowing()) dialog.dismiss();
            ActivityMain activityMain = (ActivityMain) context;
            activityMain.gotoHome();
        }
    }
}

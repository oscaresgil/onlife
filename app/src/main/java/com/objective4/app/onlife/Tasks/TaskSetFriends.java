package com.objective4.app.onlife.Tasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;

import com.afollestad.materialdialogs.MaterialDialog;
import com.objective4.app.onlife.Activities.ActivityMain;
import com.objective4.app.onlife.Controller.ConnectionController;
import com.objective4.app.onlife.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.ConnectException;
import java.util.Arrays;

import static com.objective4.app.onlife.Controller.StaticMethods.makeSnackbar;

public class TaskSetFriends extends AsyncTask<String, Void, JSONObject> {
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
    protected JSONObject doInBackground(String... params) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("tag","setFriends");
            jsonObject.put("myId", params[0]);
            jsonObject.put("id_friends",new JSONArray(Arrays.asList(Arrays.copyOfRange(params,1,params.length))));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try{
            return controller.makeHttpRequest(jsonObject);
        }catch(ConnectException e){
            connectionError = true;
        }catch(Exception ignored){
        }
        return null;
    }

    @Override
    protected void onPostExecute(JSONObject userId) {
        super.onPostExecute(userId);
        if (userId == null) {
            if (connectionError)makeSnackbar(context, ((Activity) context).findViewById(R.id.ActivityMain_ImageViewLogo), R.string.no_connection, Snackbar.LENGTH_LONG);
            else makeSnackbar(context, ((Activity) context).findViewById(R.id.ActivityMain_ImageViewLogo), R.string.error, Snackbar.LENGTH_INDEFINITE);
        }
        else {
            if (dialog.isShowing()) dialog.dismiss();
            ActivityMain activityMain = (ActivityMain) context;
            activityMain.gotoHome();
        }
    }
}

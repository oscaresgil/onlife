package com.objective4.app.onlife.Tasks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.objective4.app.onlife.Activities.ActivityHome;
import com.objective4.app.onlife.Activities.ActivityMain;
import com.objective4.app.onlife.Adapters.AdapterBaseElements;
import com.objective4.app.onlife.Controller.ConnectionController;
import com.objective4.app.onlife.Models.ModelPerson;
import com.objective4.app.onlife.Models.ModelSessionData;
import com.objective4.app.onlife.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.objective4.app.onlife.Controller.StaticMethods.comparePerson;
import static com.objective4.app.onlife.Controller.StaticMethods.isNetworkAvailable;
import static com.objective4.app.onlife.Controller.StaticMethods.makeSnackbar;
import static com.objective4.app.onlife.Controller.StaticMethods.setHashToList;

public class TaskGetFriends extends AsyncTask<String, Void, JSONObject> {
    private MaterialDialog dialog;
    private boolean flagDialog, connectionFailure =false;
    private Context context;
    private ConnectionController connection;

    public TaskGetFriends(Context c, boolean flagDialog){
        this.flagDialog = flagDialog;
        this.context = c;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        connection = new ConnectionController();
        if (flagDialog) {
            dialog = new MaterialDialog.Builder(context)
                    .title(R.string.dialog_please_wait)
                    .content(R.string.loading_friends)
                    .progress(true,10)
                    .widgetColor(context.getResources().getColor(R.color.accent))
                    .cancelable(false)
                    .show();
        }
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        try{
            JSONObject p = new JSONObject();
            p.put("tag", "getFriends");
            p.put("myId", params[0]);
            return new ConnectionController().makeHttpRequest(p);
        }catch(ConnectException e){
            connectionFailure = true;
        } catch(Exception ignored){}
        return null;
    }

    @Override
    protected void onPostExecute(JSONObject object) {
        if (object!=null){
            try {
                List<ModelPerson> friends = new Gson().fromJson(object.getString("friends"), (new TypeToken<ArrayList<ModelPerson>>() {
                }.getType()));

                HashMap<String,ModelPerson> hashMap = comparePerson(ModelSessionData.getInstance().getFriends(), friends);
                ModelSessionData.getInstance().setFriends(hashMap);

                AdapterBaseElements adapterContact = ((ActivityHome) context).getAdapterContact();
                if (adapterContact!=null) adapterContact.updateElements(setHashToList(hashMap));

                SharedPreferences sharedPreferences = context.getSharedPreferences(ActivityMain.MyPREFERENCES, Context.MODE_PRIVATE);
                if (sharedPreferences.getBoolean("first_login", false)) {
                    sharedPreferences.edit().putString("friends", new Gson().toJson(setHashToList(ModelSessionData.getInstance().getFriends()))).apply();
                    sharedPreferences.edit().putBoolean("first_login", false).apply();

                    Intent i = new Intent("com.objective4.app.onlife.Fragments.Social.FragmentContacts");
                    i.putExtra("tag", "friends_updated");
                    context.sendBroadcast(i);
                }

                if (flagDialog) if (dialog.isShowing())  dialog.dismiss();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            if (connectionFailure) makeSnackbar(context, ((Activity) context).findViewById(R.id.ActivityHome_CoordinatorLayout), R.string.no_connection, Snackbar.LENGTH_INDEFINITE);
            else makeSnackbar(context, ((Activity) context).findViewById(R.id.ActivityHome_CoordinatorLayout), R.string.error, Snackbar.LENGTH_INDEFINITE);
        }
    }
}

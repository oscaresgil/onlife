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
import org.json.JSONObject;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.objective4.app.onlife.Controller.StaticMethods.comparePerson;
import static com.objective4.app.onlife.Controller.StaticMethods.isNetworkAvailable;
import static com.objective4.app.onlife.Controller.StaticMethods.makeSnackbar;
import static com.objective4.app.onlife.Controller.StaticMethods.setHashToList;

public class TaskGetFriends extends AsyncTask<String, Void, ArrayList<ModelPerson>> {
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
    protected ArrayList<ModelPerson> doInBackground(String... params) {
        String id = params[0];
        HashMap<String,String> p = new HashMap<>();
        p.put("tag", "getFriends");
        p.put("myId", id);
        JSONObject jsonFriends;
        try{
            jsonFriends = connection.makeHttpRequest("person.php", p);
            Gson gson = new Gson();
            return gson.fromJson(jsonFriends.getString("friends"), (new TypeToken<ArrayList<ModelPerson>>(){}.getType()));
        }catch(ConnectException e){
            connectionFailure = true;
        } catch(Exception ignored){}
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<ModelPerson> friends) {
        if (isNetworkAvailable((Activity) context)) {
            if (friends == null){
                if (connectionFailure) makeSnackbar(context, ((Activity) context).findViewById(R.id.ActivityHome_CoordinatorLayout), R.string.no_connection, Snackbar.LENGTH_INDEFINITE);
                else makeSnackbar(context, ((Activity) context).findViewById(R.id.ActivityHome_CoordinatorLayout), R.string.error, Snackbar.LENGTH_INDEFINITE);
            }
            else {
                    HashMap<String,ModelPerson> hashMap = comparePerson(ModelSessionData.getInstance().getFriends(), friends);
                    ModelSessionData.getInstance().setFriends(hashMap);

                    AdapterBaseElements adapterContact = ((ActivityHome) context).getAdapterContact();
                    if (adapterContact!=null) adapterContact.updateElements(setHashToList(hashMap));

                    SharedPreferences sharedPreferences = context.getSharedPreferences(ActivityMain.MyPREFERENCES, Context.MODE_PRIVATE);
                    if (sharedPreferences.getBoolean("first_login", false)) {
                        Gson gson = new Gson();
                        sharedPreferences.edit().putString("friends", gson.toJson(setHashToList(ModelSessionData.getInstance().getFriends()))).apply();
                        sharedPreferences.edit().putBoolean("first_login", false).apply();

                        Intent i = new Intent("com.objective4.app.onlife.Fragments.Social.FragmentContacts");
                        i.putExtra("tag", "friends_updated");
                        context.sendBroadcast(i);

                    }

                if (flagDialog) if (dialog.isShowing())  dialog.dismiss();
            }
        } else {
            makeSnackbar(context, ((Activity) context).findViewById(R.id.ActivityHome_CoordinatorLayout), R.string.no_connection, Snackbar.LENGTH_LONG, R.string.button_change_connection, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                }
            });
        }
    }
}

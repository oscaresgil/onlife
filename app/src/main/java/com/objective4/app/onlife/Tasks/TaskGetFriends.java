package com.objective4.app.onlife.Tasks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.Settings;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kenny.snackbar.SnackBar;
import com.objective4.app.onlife.Activities.ActivityHome;
import com.objective4.app.onlife.Activities.ActivityMain;
import com.objective4.app.onlife.Adapters.AdapterBaseElements;
import com.objective4.app.onlife.Controller.JSONParser;
import com.objective4.app.onlife.Models.ModelPerson;
import com.objective4.app.onlife.Models.ModelSessionData;
import com.objective4.app.onlife.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.objective4.app.onlife.Controller.StaticMethods.isNetworkAvailable;

public class TaskGetFriends extends AsyncTask<String, Void, ArrayList<ModelPerson>> {
    private MaterialDialog dialog;
    private boolean flagDialog;
    private Context context;
    private JSONParser jsonParser;

    public TaskGetFriends(Context c, boolean flagDialog){
        this.flagDialog = flagDialog;
        this.context = c;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        jsonParser = new JSONParser();
        if (flagDialog) {
            dialog = new MaterialDialog.Builder(context)
                    .title(R.string.dialog_please_wait)
                    .content(R.string.loading_friends)
                    .progress(true,10)
                    .widgetColor(context.getResources().getColor(R.color.orange_light))
                    .cancelable(false)
                    .show();
        }
    }

    @Override
    protected ArrayList<ModelPerson> doInBackground(String... params) {
        String id = params[0];
        List<NameValuePair> p = new ArrayList<>();
        p.add(new BasicNameValuePair("tag", "getFriends"));
        p.add(new BasicNameValuePair("myId", id));
        JSONObject jsonFriends;
        try{
            do{
                jsonFriends = jsonParser.makeHttpRequest(ActivityMain.SERVER_URL, "POST", p);
            }
            while(!isNetworkAvailable((Activity) context));
            Gson gson = new Gson();

            return gson.fromJson(jsonFriends.getString("friends"), (new TypeToken<ArrayList<ModelPerson>>(){}.getType()));
        }catch(Exception ex){
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    protected void onPostExecute(ArrayList<ModelPerson> friends) {
        super.onPostExecute(friends);
        if (isNetworkAvailable((Activity)context)){
            if (flagDialog) dialog.dismiss();

            for (ModelPerson f: friends) { f.setRefreshImage(true); f.setRefreshImageBig(true); }
            Collections.sort(friends, new Comparator<ModelPerson>() {
                @Override
                public int compare(ModelPerson modelPerson1, ModelPerson modelPerson2) {
                    return modelPerson1.getName().compareTo(modelPerson2.getName());
                }
            });

            ModelSessionData.getInstance().setFriends(friends);
            AdapterBaseElements adapterContact = ((ActivityHome) context).getAdapterContact();
            adapterContact.clear();
            adapterContact.addAll(friends);
        }
        else{
            SnackBar.show((Activity)context, R.string.no_connection, R.string.button_change_connection, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                }
            });
            if (flagDialog) dialog.dismiss();
        }
    }
}

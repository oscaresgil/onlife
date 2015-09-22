package com.example.henzer.socialize.Tasks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.henzer.socialize.Activities.ActivityHome;
import com.example.henzer.socialize.Activities.ActivityMain;
import com.example.henzer.socialize.Adapters.AdapterContact;
import com.example.henzer.socialize.Adapters.AdapterGroup;
import com.example.henzer.socialize.Controller.JSONParser;
import com.example.henzer.socialize.Fragments.FragmentContacts;
import com.example.henzer.socialize.Models.ModelPerson;
import com.example.henzer.socialize.Models.ModelSessionData;
import com.example.henzer.socialize.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kenny.snackbar.SnackBar;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.example.henzer.socialize.Controller.StaticMethods.friendsOnlyOnline;
import static com.example.henzer.socialize.Controller.StaticMethods.isNetworkAvailable;

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
        if (dialog!=null) {
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
        JSONObject jsonFriends = null;
        try{
            jsonFriends = jsonParser.makeHttpRequest(ActivityMain.SERVER_URL, "POST", p);
            Gson gson = new Gson();
            ArrayList<ModelPerson> friends = new ArrayList<>();
            friends = gson.fromJson(jsonFriends.getString("friends"), (new TypeToken<ArrayList<ModelPerson>>(){}.getType()));

            return friends;
        }catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(ArrayList<ModelPerson> friends) {
        super.onPostExecute(friends);
        if (isNetworkAvailable((Activity)context)){
            if (dialog!=null) {
                dialog.dismiss();
            }

            friends = friendsOnlyOnline(friends);

            Collections.sort(friends, new Comparator<ModelPerson>() {
                @Override
                public int compare(ModelPerson modelPerson1, ModelPerson modelPerson2) {
                    return modelPerson1.getName().compareTo(modelPerson2.getName());
                }
            });

            ModelSessionData.getInstance().setFriends(friends);
            AdapterContact adapterContact = ((ActivityHome) context).getAdapterContact();
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
        }
    }
}

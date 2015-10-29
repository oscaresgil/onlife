package com.objective4.app.onlife.Tasks;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;

import com.google.gson.Gson;
import com.objective4.app.onlife.Activities.ActivityMain;
import com.objective4.app.onlife.BlockActivity.ActivityFriendBlock;
import com.objective4.app.onlife.Controller.ConnectionController;
import com.objective4.app.onlife.Models.ModelPerson;
import com.objective4.app.onlife.Models.ModelSessionData;
import com.objective4.app.onlife.R;

import net.steamcrafted.loadtoast.LoadToast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ConnectException;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static com.objective4.app.onlife.Controller.StaticMethods.makeSnackbar;

public class TaskSendNotification extends AsyncTask<ModelPerson, String, String[]>{
    private Activity context;
    private LoadToast toast;
    private ConnectionController connection;
    private String message="",actualUser, gifName="";
    private int numBlocked;

    public TaskSendNotification(Activity c, String actualUser, String message, String gifName){
        this.context = c;
        this.message = message;
        this.actualUser = actualUser;
        this.gifName = gifName;
        connection = new ConnectionController();
        numBlocked = 0;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        toast = new LoadToast(context)
                .setText(context.getResources().getString(R.string.blocking))
                .setTextColor(context.getResources().getColor(R.color.black))
                .setTranslationY(100)
                .setProgressColor(context.getResources().getColor(R.color.accent))
                .show();
    }

    @Override
    protected String[] doInBackground(ModelPerson... params) {
        String returnMessage = "";
        SharedPreferences sharedPreferences = context.getSharedPreferences(ActivityMain.MyPREFERENCES, Context.MODE_PRIVATE);
        if (!sharedPreferences.contains("update_key") || 2 == sharedPreferences.getInt("update_key",0)) {
            long actualTime = Calendar.getInstance().getTimeInMillis();
            // hashmap para pasar parametros de id y phoneId
            HashMap<String,String> map = new HashMap();
            if (params.length == 1) {
                ModelPerson f = params[0];
                f = ModelSessionData.getInstance().getFriends().get(f.getId());
                if (actualTime - f.getLastBlockedTime() > context.getResources().getInteger(R.integer.block_time_remaining)) {
                    map.put("id[]", f.getId_phone());
                    f.setLastBlockedTime(actualTime);
                } else {
                    returnMessage = context.getResources().getString(R.string.toast_not_time_yet) + " " + ((context.getResources().getInteger(R.integer.block_time_remaining) - (actualTime - f.getLastBlockedTime())) / 1000) + " s";
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ignored) {}
                    return new String[]{"false",returnMessage};
                }
            } else {
                for (ModelPerson f : params) {
                    f = ModelSessionData.getInstance().getFriends().get(f.getId());
                    if (actualTime - f.getLastBlockedTime() > context.getResources().getInteger(R.integer.block_time_remaining) && f.getState().equals("A")) {
                        map.put("id[]", f.getId_phone());
                        f.setLastBlockedTime(actualTime);
                        numBlocked++;
                    }
                }
                returnMessage = context.getResources().getString(R.string.friends_blocked_number) + " " + numBlocked + "/" + params.length;
                if (numBlocked==0){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ignored) {}
                    return new String[]{"false",returnMessage};
                }
            }

            map.put("userName", actualUser);
            map.put("message", message);
            map.put("gifName",gifName);

            HttpURLConnection urlConnection = null;
            URL url;
            try {

                JSONObject jsonObject = connection.makeHttpRequest("gcm.php", map);

                switch (jsonObject.getInt("code")) {
                    case 0:
                        return new String[]{"true", returnMessage};
                    case -1:
                        return new String[]{"false", context.getResources().getString(R.string.gcm_not_registered)};
                }

            } catch (ConnectException e) {
                returnMessage = context.getResources().getString(R.string.no_connection);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    urlConnection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace(); //If you want further info on failure...
                }
            }
        }else{
            returnMessage = context.getResources().getString(R.string.update_forced);
        }
        return new String[]{"false",returnMessage};
    }

    @Override
    protected void onPostExecute(String[] result){
        if (result[0].equals("false")){
            toast.error();
            makeSnackbar(context, context.getCurrentFocus(), result[1], Snackbar.LENGTH_LONG);
        }else{
            toast.success();
            if (!result[1].equals("")) makeSnackbar(context,context.getCurrentFocus(),result[1],Snackbar.LENGTH_SHORT);

            if (context instanceof ActivityFriendBlock){
                ((ActivityFriendBlock)context).setTimer();
            }
        }
    }
}
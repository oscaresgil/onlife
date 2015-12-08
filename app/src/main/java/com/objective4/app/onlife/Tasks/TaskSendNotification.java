package com.objective4.app.onlife.Tasks;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;

import com.objective4.app.onlife.Activities.ActivityMain;
import com.objective4.app.onlife.BlockActivity.ActivityFriendBlock;
import com.objective4.app.onlife.Controller.ConnectionController;
import com.objective4.app.onlife.Models.ModelPerson;
import com.objective4.app.onlife.Models.ModelSessionData;
import com.objective4.app.onlife.R;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import static com.objective4.app.onlife.Controller.StaticMethods.makeSnackbar;

public class TaskSendNotification extends AsyncTask<ModelPerson, String, String[]>{
    private Activity context;
    private LoadToast toast;
    private String message="", gifName="";
    private int numBlocked;
    private boolean isMyUser = false;

    public TaskSendNotification(Activity c, String message, String gifName){
        this.context = c;
        this.message = message;
        this.gifName = gifName;
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
            JSONObject map = new JSONObject();
            try{
                if (params.length == 1) {
                    ModelPerson f = params[0];
                    f = ModelSessionData.getInstance().getFriends().get(f.getId());
                    if (actualTime - f.getLastBlockedTime() > context.getResources().getInteger(R.integer.block_time_remaining)) {
                        map.put("id_blocked", new JSONArray(Arrays.asList(new String[]{f.getId()})));
                        if (!f.getId().equals(ModelSessionData.getInstance().getUser().getId())){
                            f.setLastBlockedTime(actualTime);
                        }else{
                            isMyUser = true;
                        }
                    } else {
                        returnMessage = context.getResources().getString(R.string.toast_not_time_yet) + " " + ((context.getResources().getInteger(R.integer.block_time_remaining) - (actualTime - f.getLastBlockedTime())) / 1000) + " s";
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ignored) {}
                        return new String[]{"false",returnMessage};
                    }
                } else {
                    ArrayList<String> ids = new ArrayList<>();
                    for (ModelPerson f : params) {
                        f = ModelSessionData.getInstance().getFriends().get(f.getId());
                        if (actualTime - f.getLastBlockedTime() > context.getResources().getInteger(R.integer.block_time_remaining) && f.getState().equals("A")) {
                            ids.add(f.getId());
                            if (!f.getId().equals(ModelSessionData.getInstance().getUser().getId())){
                                f.setLastBlockedTime(actualTime);
                            }
                            numBlocked++;
                        }
                    }
                    returnMessage = context.getResources().getString(R.string.friends_blocked_number) + " " + numBlocked + "/" + params.length;
                    if (numBlocked==0){
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ignored) {}
                        return new String[]{"false",returnMessage};
                    }else{
                        map.put("id_blocked", new JSONArray(Arrays.asList(ids.toArray())));
                    }
                }
                map.put("tag","block");
                map.put("id_blocking", ModelSessionData.getInstance().getUser().getId());
                map.put("message", message);
                map.put("gifName",gifName);
                new ConnectionController().makeHttpRequest(map);
                return new String[]{"true",returnMessage};

            }catch (Exception ignored){}
        }else{
            returnMessage = context.getResources().getString(R.string.update_forced);
        }
        return new String[]{"false",returnMessage};
    }

    @Override
    protected void onPostExecute(String[] result){
        if (result[0].equals("false")){
            toast.error();
            if (!result[1].equals("")) makeSnackbar(context, context.getCurrentFocus(), result[1], Snackbar.LENGTH_LONG);
        }else{
            toast.success();
            if (!result[1].equals("")) makeSnackbar(context, context.getCurrentFocus(), result[1], Snackbar.LENGTH_LONG);
            if (context instanceof ActivityFriendBlock && !isMyUser){
                ((ActivityFriendBlock)context).setTimer();
            }
        }
    }
}
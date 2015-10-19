package com.objective4.app.onlife.Tasks;

import android.app.Activity;
import android.os.AsyncTask;

import com.kenny.snackbar.SnackBar;
import com.objective4.app.onlife.Controller.JSONParser;
import com.objective4.app.onlife.Models.ModelPerson;
import com.objective4.app.onlife.Models.ModelSessionData;
import com.objective4.app.onlife.R;

import net.steamcrafted.loadtoast.LoadToast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TaskSendNotification extends AsyncTask<ModelPerson, String, String[]>{
    private Activity context;
    private LoadToast toast;
    private JSONParser jsonParser;
    private String message="",actualUser, gifName="";
    private int numBlocked;

    public TaskSendNotification(Activity c, String actualUser, String message, String gifName){
        this.context = c;
        this.message = message;
        this.actualUser = actualUser;
        this.gifName = gifName;
        jsonParser = new JSONParser();
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
        //Parametros a enviar
        String returnMessage = "";
        long actualTime = Calendar.getInstance().getTimeInMillis();
        List<NameValuePair> p = new ArrayList<>();
        if (params.length==1){
            ModelPerson f = params[0];
            if (actualTime - f.getLastBlockedTime() > context.getResources().getInteger(R.integer.block_time_remaining)){
                p.add(new BasicNameValuePair("id[]",f.getId_phone()));
                f.setLastBlockedTime(actualTime);
            }else{
                returnMessage = context.getResources().getString(R.string.toast_not_time_yet)+" "+((context.getResources().getInteger(R.integer.block_time_remaining)-(actualTime - f.getLastBlockedTime()))/1000)+" s";
            }
        }else {
            for (int i = 0; i < params.length; i++) {
                ModelPerson f = params[i];
                if (actualTime - f.getLastBlockedTime() > context.getResources().getInteger(R.integer.block_time_remaining)){
                    p.add(new BasicNameValuePair("id[]", f.getId_phone()));
                    f.setLastBlockedTime(actualTime);
                    numBlocked++;
                }
            }
            returnMessage = context.getResources().getString(R.string.friends_blocked_number)+" "+numBlocked+"/"+params.length;
        }

        p.add(new BasicNameValuePair("userName", actualUser));
        p.add(new BasicNameValuePair("message", message));
        p.add(new BasicNameValuePair("gifName", gifName));

        try {
            JSONObject json = jsonParser.makeHttpRequest("http://104.236.74.55/onlife/gcm.php", "POST", p);
            switch (json.getInt("code")){
                case 0: return new String[]{"true",returnMessage};
                case -1: return new String[]{"false",context.getResources().getString(R.string.gcm_not_registered)};
            }

        }catch(ConnectException e){
            returnMessage = context.getResources().getString(R.string.no_connection);
        }catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String[]{"false",returnMessage};
    }

    @Override
    protected void onPostExecute(String[] result){
        SnackBar.cancelSnackBars(context);
        if (result[0].equals("false")){
            toast.error();
            SnackBar.show(context,result[1]);
        }else{
            toast.success();
            /*if (context instanceof ActivityBlockBase){
                context.finish();
            }*/
            if (!result[1].equals("")) SnackBar.show(context,result[1]);
        }
    }
}
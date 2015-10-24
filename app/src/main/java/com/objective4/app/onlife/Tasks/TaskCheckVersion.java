package com.objective4.app.onlife.Tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;

import com.objective4.app.onlife.Activities.ActivityHome;
import com.objective4.app.onlife.Activities.ActivityMain;
import com.objective4.app.onlife.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.ConnectException;

public class TaskCheckVersion extends AsyncTask<Void,Void,Double> {
    private Context context;

    public TaskCheckVersion(Context context) {
        this.context = context;
    }

    @Override
    protected Double doInBackground(Void... params) {
        try {
            Document doc = Jsoup.connect("https://play.google.com/store/apps/details?id=com.objective4.app.onlife&hl="+context.getResources().getString(R.string.language_type)).referrer("http://www.google.com").get();
            String version = doc.select("div[itemprop=softwareVersion]").first().ownText();
            return Double.parseDouble(version);
        } catch (ConnectException e){
            return -1.0;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    @Override
    protected void onPostExecute(Double version) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(ActivityMain.MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //if (version==-1.0) INTERNET
        if(version!=0.0){
            double actualVersion = 0.0;
            try {
                PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                actualVersion = Double.parseDouble(pInfo.versionName);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            if (version != Double.parseDouble(sharedPreferences.getString("version_name", "0.0")) && version>actualVersion) {
                editor.putString("version_name",""+version).apply();
                version = version * 10;
                if (version % 2 == 0) {
                    editor.putInt("update_key", 1).apply();
                    ((ActivityHome)context).setDialogUpdate(1);
                } else {
                    editor.putInt("update_key", 2).apply();
                    ((ActivityHome)context).setDialogUpdate(2);
                }
            }else{
                if (sharedPreferences.contains("update_key")) editor.remove("update_key").apply();
            }
        }
    }
}

package com.objective4.app.onlife.Tasks;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;

import com.objective4.app.onlife.Activities.ActivityHome;
import com.objective4.app.onlife.Activities.ActivityMain;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class TaskCheckVersion extends AsyncTask<Void,Void,String> {
    private Context context;

    public TaskCheckVersion(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            Document doc = Jsoup.connect("https://play.google.com/store/apps/details?id=com.objective4.app.onlife&hl=en")
                    .referrer("http://www.google.com").get();
            return doc.select("div[itemprop=softwareVersion]").first().ownText();
        } catch (IOException ignored) {
        }
        return null;
    }

    @Override
    protected void onPostExecute(String version) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(ActivityMain.MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (version != null) {
            String actualVersion = "0.0";
            try {
                PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                actualVersion = pInfo.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            if (!actualVersion.equals("0.0") && !version.equals(sharedPreferences.getString("version_name", "0.0")) && version.compareTo(actualVersion)>0){
                editor.putString("version_name", "" + version).apply();
                Intent i = new Intent("com.objective4.app.onlife.Activities.ActivityHome");
                if (Integer.parseInt(""+version.charAt(version.length()-1)) % 2 != 0) {
                    editor.putInt("update_key", 1).apply();
                    i.putExtra("update", 1);
                } else {
                    editor.putInt("update_key", 2).apply();
                    i.putExtra("update", 2);
                }
                if (ActivityHome.isRunning) context.sendBroadcast(i);
            } else if (sharedPreferences.getInt("update_key",0)!=1) {
                if (sharedPreferences.contains("update_key"))
                    editor.remove("update_key").apply();
            } else{
                if (version.compareTo(actualVersion)==0 || version.compareTo(actualVersion)<0){
                    if (sharedPreferences.contains("update_key")) {
                        editor.remove("update_key").apply();
                        Intent i = new Intent("com.objective4.app.onlife.Activities.ActivityHome");
                        i.putExtra("update", 3);
                        if (ActivityHome.isRunning) context.sendBroadcast(i);
                    }
                }
            }
        }
    }


}

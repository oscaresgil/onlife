package com.example.henzer.socialize.Controller;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;

import com.example.henzer.socialize.Models.Person;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class StaticMethods {

    public static Bitmap loadImage(Context context, String name){
        ContextWrapper cw = new ContextWrapper(context);
        File dirImages = cw.getDir("Profiles",Context.MODE_APPEND);
        File myPath = new File(dirImages, name+".png");
        Bitmap b = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            b = BitmapFactory.decodeFile(myPath.getAbsolutePath(), options);
        }catch (Exception e){e.printStackTrace();}
        return b;
    }

    public static File loadImagePath(Context context, String id){
        ContextWrapper cw = new ContextWrapper(context);
        File dirImages = cw.getDir("Profiles",Context.MODE_APPEND);
        File myPath = new File(dirImages, id+".png");
        return myPath;
    }

    public static String saveImage(Context context, String name, Bitmap image){
        ContextWrapper cw = new ContextWrapper(context);
        File dirImages = cw.getDir("Profiles",Context.MODE_PRIVATE);
        File myPath = new File(dirImages, name+".png");

        FileOutputStream fos = null;
        try{
            fos = new FileOutputStream(myPath);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.flush();
        }catch (Exception e){e.printStackTrace();}
        return myPath.getAbsolutePath();
    }

    public static boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String deleteAccent(String input) {
        String output = input;
        for (int i=0; i<output.length(); i++){
            if ((int)output.charAt(i) == 237){
                output = output.replace(output.charAt(i),'i');
            }
            else if((int)output.charAt(i) == 243){
                output = output.replace(output.charAt(i),'o');
            }
            else if((int)output.charAt(i) == 250){
                output = output.replace(output.charAt(i),'u');
            }
            else if((int)output.charAt(i) == 225){
                output = output.replace(output.charAt(i),'a');
            }
            else if((int)output.charAt(i) == 233){
                output = output.replace(output.charAt(i),'e');
            }
        }
        return output;
    }

    public static void turnGPSOn(Context context){
        Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
        intent.putExtra("enabled", true);
        context.sendBroadcast(intent);

        String provider = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (! provider.contains("gps")) { //if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            context.sendBroadcast(poke);
        }
    }

    public static void turnGPSOff(Context context){
        Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
        intent.putExtra("enabled", false);
        context.sendBroadcast(intent);

        String provider = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (provider.contains("gps")){ //if gps is enabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            context.sendBroadcast(poke);
        }
    }

    public static double distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return (earthRadius * c);
    }
}

package com.example.henzer.socialize.Controller;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.example.henzer.socialize.Models.ModelGroup;
import com.example.henzer.socialize.Models.ModelPerson;
import com.example.henzer.socialize.Models.ModelSessionData;
import com.example.henzer.socialize.R;
import com.google.gson.Gson;
import com.kenny.snackbar.SnackBar;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class StaticMethods {

    public static final int ACTIVATION_REQUEST = 47;

    public static void activateDeviceAdmin(Activity activity){
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) activity.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName myDeviceAdmin = new ComponentName(activity, DeviceAdmin.class);

        if (!devicePolicyManager.isAdminActive(myDeviceAdmin)) {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, myDeviceAdmin);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    activity.getString(R.string.device_admin_description));
            Log.e("DeviceAdmin", "entro a la contidicion");
            activity.startActivityForResult(intent, ACTIVATION_REQUEST);
        }
    }

    public static void animationStart(Context context){
        ((Activity)context).overridePendingTransition(R.animator.push_right, R.animator.push_left);
    }

    public static void animationEnd(Context context){
        ((Activity)context).overridePendingTransition(R.animator.push_left_inverted, R.animator.push_right_inverted);
    }

    public static void hideSoftKeyboard(Context context, View v){
        InputMethodManager imm = (InputMethodManager)context.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
    public static void showSoftKeyboard(Context context, View v){
        InputMethodManager imm = (InputMethodManager)context.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(v,0);
    }

    public static boolean imageInDisk(Context context, String name){
        ContextWrapper cw = new ContextWrapper(context);
        File dirImages = cw.getDir("Profiles",Context.MODE_APPEND);
        File myPath = new File(dirImages, name+".png");
        return myPath.exists();
    }

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

    public static List<String> setGifNames(){
        List<String> gifNames = new ArrayList<>();
        for (int j=1; j<35; j++){
            gifNames.add("gif"+j);
        }
        return gifNames;
    }

    public static String getRealPathFromURI(Context context, Uri contentUri){
        String [] proj      = {MediaStore.Images.Media.DATA};
        Cursor cursor       = ((Activity)context).managedQuery( contentUri, proj, null, null,null);
        if (cursor == null) return null;
        int column_index    = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public static void performCrop(Context context, Uri mImageCaptureUri, int PIC_CROP){
        try {
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setType("image/*");

            Log.e("Version",""+(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (mImageCaptureUri.toString().substring(0,21).equals("content://com.android")) {
                    String imageUriString = "content://media/external/images/media/"+mImageCaptureUri.toString().split("%3A")[1];
                    mImageCaptureUri = Uri.parse(imageUriString);
                }
            }

            List<ResolveInfo> list = context.getPackageManager().queryIntentActivities( intent, 0 );
            int size = list.size();
            if (size != 0) {
                intent.setData(mImageCaptureUri);
                intent.putExtra("crop", "true");
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("outputX", 300);
                intent.putExtra("outputY", 300);
                intent.putExtra("return-data", true);
                if (size > 0) {
                    Intent i = new Intent(intent);
                    ResolveInfo res = list.get(0);
                    i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                    ((Activity)context).startActivityForResult(intent, PIC_CROP);
                }
            }
            else{
                SnackBar.show(((Activity)context), context.getResources().getString(R.string.not_supported_image_crop));
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public static ModelPerson getFriend(String id){
        for (ModelPerson p: ModelSessionData.getInstance().getFriends()){
            if (p.getId().equals(id)){
                return p;
            }
        }
        return null;
    }

    public static void removeGroup(ModelGroup modelGroup, SharedPreferences sharedPreferences, ModelSessionData modelSessionData){
        List<ModelGroup> modelGroups = modelSessionData.getModelGroups();
        for (int i=0; i< modelGroups.size(); i++){
            if (modelGroups.get(i).getId()== modelGroup.getId()){
                modelGroups.remove(i);
                Log.e("RemovedGroup",modelGroups.toString());
            }
        }
        Gson gson = new Gson();
        modelSessionData.setModelGroups(modelGroups);
        sharedPreferences.edit().putString("groups", gson.toJson(modelGroups)).commit();
    }

    public static void removeGroup(ModelGroup modelGroup){
        List<ModelGroup> modelGroups = ModelSessionData.getInstance().getModelGroups();
        for (int i=0; i< modelGroups.size(); i++){
            if (modelGroups.get(i).getId()== modelGroup.getId()){
                modelGroups.remove(i);
                Log.e("RemovedGroup",modelGroups.toString());
            }
        }
        ModelSessionData.getInstance().setModelGroups(modelGroups);
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
        //context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
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

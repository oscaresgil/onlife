package com.objective4.app.onlife.Controller;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.objective4.app.onlife.BlockActivity.ActivityGroupBlock;
import com.objective4.app.onlife.BroadcastReceivers.BroadcastReceiverPhoneStatus;
import com.objective4.app.onlife.Models.ModelGroup;
import com.objective4.app.onlife.Models.ModelPerson;
import com.objective4.app.onlife.Models.ModelSessionData;
import com.objective4.app.onlife.R;
import com.kenny.snackbar.SnackBar;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrPosition;

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
            activity.startActivityForResult(intent, ACTIVATION_REQUEST);
        }
    }

    public static boolean deactivateDeviceAdmin(Activity activity){
        DevicePolicyManager dpm;
        ComponentName admin;
        try
        {
            dpm = (DevicePolicyManager) activity.getSystemService(Context.DEVICE_POLICY_SERVICE);
            admin = new ComponentName(activity, DeviceAdmin.class);
            if(dpm.isAdminActive(admin))
            {
                dpm.removeActiveAdmin(admin);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean checkDeviceAdmin(Context activity){
        DevicePolicyManager dpm;
        ComponentName admin;
        try
        {
            dpm = (DevicePolicyManager) activity.getSystemService(Context.DEVICE_POLICY_SERVICE);
            admin = new ComponentName(activity, DeviceAdmin.class);
            if(dpm.isAdminActive(admin))
            {
                return true;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public static void activatePhoneBroadcast(Context context){
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        BroadcastReceiver receiver = new BroadcastReceiverPhoneStatus();
        context.registerReceiver(receiver,filter);
    }

    public static void setSlidr(Context context){
        SlidrConfig config = new SlidrConfig.Builder()
                .primaryColor(context.getResources().getColor(R.color.orange))
                .secondaryColor(context.getResources().getColor(R.color.orange_light))
                .position(SlidrPosition.LEFT)
                .sensitivity(0.4f)
                .build();
        Slidr.attach((Activity)context, config);
    }

    public static void unSelectFriends(List<ModelPerson> friends){
        for (ModelPerson p: friends){
            p.setSelected(false);
        }
    }

    public static int getModelGroupIndex(ModelGroup group, List<ModelGroup> modelGroup){
        for (int i=0; i<modelGroup.size(); i++){
            ModelGroup g = modelGroup.get(i);
            if (group.getName().equals(g.getName())){
                return i;
            }
        }
        return -1;
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

    public static boolean delDirImages(Context context,List<ModelPerson> friends, List<ModelGroup> groups){
        ContextWrapper cw = new ContextWrapper(context);
        File dirImages = cw.getDir("Profiles",Context.MODE_PRIVATE);
        for (ModelPerson p: friends){
            File img = new File(dirImages, p.getId()+"_"+context.getResources().getInteger(R.integer.adapter_contact_size_large)+".png");
            img.delete();
            img = new File(dirImages, p.getId()+"_"+context.getResources().getInteger(R.integer.adapter_contact_size_little)+".png");
            img.delete();
        }
        for (ModelGroup g: groups){
            File img = new File(dirImages, g.getName()+"_"+context.getResources().getInteger(R.integer.adapter_contact_size_large)+".png");
            img.delete();
            img = new File(dirImages, g.getName()+"_"+context.getResources().getInteger(R.integer.adapter_contact_size_little)+".png");
            img.delete();
        }
        return dirImages.delete();
    }

    public static boolean delImageProfile(Context context, String id){
        ContextWrapper cw = new ContextWrapper(context);
        File dirImages = cw.getDir("Profiles",Context.MODE_APPEND);
        File myPath = new File(dirImages, id+"_"+context.getResources().getInteger(R.integer.adapter_contact_size_large)+".png");
        myPath.delete();
        myPath = new File(dirImages, id+"_"+context.getResources().getInteger(R.integer.adapter_contact_size_little)+".png");
        return myPath.delete();
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

    public static void performCrop(Context context, Uri mImageCaptureUri, int PIC_CROP){
        try {
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setType("image/*");

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

    public static void removeGroup(ModelGroup modelGroup){
        List<ModelGroup> modelGroups = ModelSessionData.getInstance().getModelGroups();
        for (int i=0; i< modelGroups.size(); i++){
            if (modelGroups.get(i).getId()== modelGroup.getId()){
                modelGroups.remove(i);
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
}

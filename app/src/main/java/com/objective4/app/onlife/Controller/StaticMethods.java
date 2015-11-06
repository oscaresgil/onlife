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
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.facebook.share.model.AppInviteContent;
import com.facebook.share.widget.AppInviteDialog;
import com.objective4.app.onlife.BroadcastReceivers.BroadcastReceiverPhoneStatus;
import com.objective4.app.onlife.BroadcastReceivers.BroadcastReceiverPing;
import com.objective4.app.onlife.Models.ModelGroup;
import com.objective4.app.onlife.Models.ModelPerson;
import com.objective4.app.onlife.R;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrInterface;
import com.r0adkll.slidr.model.SlidrPosition;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class StaticMethods {
    public static final int ACTIVATION_REQUEST = 47;

    public static Bitmap decodeDrawable(Context context,int d, int size){
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(context.getResources(),d,o);

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while(o.outWidth / scale / 2 >= size &&
                    o.outHeight / scale / 2 >= size) {
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeResource(context.getResources(), d, o2);
        } catch (Exception ignored) {}
        return null;

    }

    public static void expand(final View v) {
        v.measure(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? WindowManager.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density) *3);
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density) *3);
        v.startAnimation(a);
    }

    public static Snackbar makeSnackbar(Context context, View v, int textId, int duration){
        Snackbar snackbar = Snackbar.make(v, textId, duration);
        snackbar.getView().setBackgroundColor(context.getResources().getColor(R.color.accent));
        ((TextView)snackbar.getView().findViewById(android.support.design.R.id.snackbar_text)).setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/oldrepublic.ttf"));
        snackbar.show();
        return snackbar;
    }

    public static Snackbar makeSnackbar(Context context, View v, String text, int duration){
        Snackbar snackbar = Snackbar.make(v, text, duration);
        snackbar.getView().setBackgroundColor(context.getResources().getColor(R.color.accent));
        ((TextView)snackbar.getView().findViewById(android.support.design.R.id.snackbar_text)).setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/oldrepublic.ttf"));
        snackbar.show();
        return snackbar;
    }


    public static Snackbar makeSnackbar(Context context, View v, int textId, int duration, int actionTextId, View.OnClickListener listener){
        Snackbar snackbar = Snackbar.make(v,textId,duration).setAction(actionTextId, listener);
        snackbar.getView().setBackgroundColor(context.getResources().getColor(R.color.accent));
        snackbar.show();
        return snackbar;
    }

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
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) activity.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName myDeviceAdmin = new ComponentName(activity, DeviceAdmin.class);
        if (devicePolicyManager.isAdminActive(myDeviceAdmin)) {
            devicePolicyManager.removeActiveAdmin(myDeviceAdmin);
            return true;
        }
        return false;
    }

    public static boolean checkDeviceAdmin(Context activity){
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) activity.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName myDeviceAdmin = new ComponentName(activity, DeviceAdmin.class);
        return devicePolicyManager.isAdminActive(myDeviceAdmin);
    }

    public static void inviteFacebookFriends(Context context){
        String appLinkUrl, previewImageUrl;

        appLinkUrl = context.getString(R.string.app_link_url);
        previewImageUrl = context.getString(R.string.app_link_image_url);

        if (AppInviteDialog.canShow()) {
            AppInviteContent content = new AppInviteContent.Builder()
                    .setApplinkUrl(appLinkUrl)
                    .setPreviewImageUrl(previewImageUrl)
                    .build();
            if (context instanceof Activity)
                AppInviteDialog.show((Activity) context, content);
        }
    }

    public static void activatePhoneBroadcast(Context context){
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SHUTDOWN);

        BroadcastReceiver receiver = new BroadcastReceiverPhoneStatus();
        context.registerReceiver(receiver,filter);
    }

    public static void activatePingBroadcast(Context context){
        BroadcastReceiver receiver = new BroadcastReceiverPing();
        context.registerReceiver(receiver,new IntentFilter("com.objective4.app.onlife.BroadcastReceivers.BroadcastReceiverPing"));
    }

    public static SlidrInterface setSlidr(Context context){
        SlidrConfig config = new SlidrConfig.Builder()
                .primaryColor(context.getResources().getColor(R.color.primary))
                .secondaryColor(context.getResources().getColor(R.color.primary_dark))
                .position(SlidrPosition.LEFT)
                .sensitivity(0.4f)
                .build();
        return Slidr.attach((Activity) context, config);
    }

    public static void unSelectFriends(List<ModelPerson> friends){
        for (ModelPerson p: friends){
            p.setSelected(false);
        }
    }

    public static boolean isFriendAlready(List<ModelPerson> friends, String id){
        for (ModelPerson f: friends){
            if (id.equals(f.getId())){
                return true;
            }
        }
        return false;
    }

    public static void removeFriend(Context context, List<ModelPerson> friends, List<ModelGroup> groups, String id){
        for (int i=0; i<friends.size(); i++){
            if (id.equals(friends.get(i).getId())){
                friends.remove(i);
                delImageProfile(context,id);
                break;
            }
        }
        for (int i = 0; i<groups.size(); i++){
            ModelGroup g = groups.get(i);
            for (int j=0; j<g.getFriendsInGroup().size(); j++){
                ModelPerson f = g.getFriendsInGroup().get(j);
                if (f.getId().equals(id)){
                    g.getFriendsInGroup().remove(j);
                    if (g.getFriendsInGroup().isEmpty()){
                        groups.remove(j);
                    }
                    break;
                }
            }
        }
    }

    public static int getModelPersonIndex(List<ModelPerson> friends, String id){
        Collections.sort(friends, new Comparator<ModelPerson>() {
            @Override
            public int compare(ModelPerson modelPerson1, ModelPerson modelPerson2) {
                return modelPerson1.getName().compareTo(modelPerson2.getName());
            }
        });
        for (int i=0; i<friends.size(); i++){
            ModelPerson p = friends.get(i);
            if (id.equals(p.getId())){
                return i;
            }
        }
        return -1;
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
        imm.showSoftInput(v, 0);
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
            new File(dirImages, p.getId()+"_"+context.getResources().getInteger(R.integer.adapter_contact_size_large)+".png").delete();
            new File(dirImages, p.getId()+"_"+context.getResources().getInteger(R.integer.adapter_contact_size_little)+".png").delete();
        }
        for (ModelGroup g: groups){
            new File(dirImages, g.getName()+"_"+context.getResources().getInteger(R.integer.adapter_contact_size_large)+".png").delete();
            new File(dirImages, g.getName()+"_"+context.getResources().getInteger(R.integer.adapter_contact_size_little)+".png").delete();
        }
        return dirImages.delete();
    }

    public static boolean delImageProfile(Context context, String id){
        ContextWrapper cw = new ContextWrapper(context);
        File dirImages = cw.getDir("Profiles", Context.MODE_APPEND);
        new File(dirImages, id+"_"+context.getResources().getInteger(R.integer.adapter_contact_size_large)+".png").delete();
        return new File(dirImages, id+"_"+context.getResources().getInteger(R.integer.adapter_contact_size_little)+".png").delete();
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
        try{
            FileOutputStream fos = new FileOutputStream(myPath);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.flush();
        }catch (Exception e){e.printStackTrace();}
        return myPath.getAbsolutePath();
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
                makeSnackbar(context,((Activity)context).getCurrentFocus(),R.string.not_supported_image_crop,Snackbar.LENGTH_SHORT);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public static boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static HashMap<String,ModelPerson> setListToHash(List<ModelPerson> friends){
        HashMap<String,ModelPerson> hashMap = new HashMap<>();
        for (ModelPerson p: friends) hashMap.put(p.getId(),p);
        return hashMap;
    }

    public static List<ModelPerson> setHashToList(HashMap<String,ModelPerson> hashMap){
        List<ModelPerson> friends = new ArrayList<>(hashMap.values());
        Collections.sort(friends, new Comparator<ModelPerson>() {
            @Override
            public int compare(ModelPerson modelPerson1, ModelPerson modelPerson2) {
                return modelPerson1.getName().compareTo(modelPerson2.getName());
            }
        });
        return friends;
    }

    public static HashMap<String,ModelPerson> comparePerson(HashMap<String,ModelPerson> hashMap, List<ModelPerson> friends){
        for (ModelPerson f: friends){
            if (!hashMap.containsKey(f.getId())){
                f.setRefreshImage(true);
                f.setRefreshImageBig(true);
                hashMap.put(f.getId(),f);
            }else {
                hashMap.get(f.getId()).setRefreshImage(true);
                hashMap.get(f.getId()).setRefreshImageBig(true);
                hashMap.get(f.getId()).setState(f.getState());
            }
        }
        return hashMap;
    }
}

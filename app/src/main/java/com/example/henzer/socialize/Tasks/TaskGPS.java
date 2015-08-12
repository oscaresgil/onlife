package com.example.henzer.socialize.Tasks;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.GridView;

import com.example.henzer.socialize.Activities.ActivityHome;
import com.example.henzer.socialize.BlockActivity.ActivityFriendBlock;
import com.example.henzer.socialize.BlockActivity.ActivityGroupBlock;
import com.example.henzer.socialize.Fragments.FragmentContacts;
import com.example.henzer.socialize.GCMClient.GcmMessageHandler;
import com.example.henzer.socialize.Models.ModelPerson;
import com.example.henzer.socialize.R;

import net.steamcrafted.loadtoast.LoadToast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static com.example.henzer.socialize.Controller.StaticMethods.turnGPSOff;
import static com.example.henzer.socialize.Controller.StaticMethods.turnGPSOn;

public class TaskGPS extends AsyncTask<Void,Void,Void> implements LocationListener{
    private LoadToast toast;
    private Context context;
    private Location location;
    private LocationManager locationManager;
    private String TAG;

    private ModelPerson actualUser;
    private ModelPerson friend;

    public TaskGPS(Context context, String TAG){
        this.context = context;
        this.TAG = TAG;
    }
    public TaskGPS(Context context, String TAG, ModelPerson actualUser, ModelPerson friend){
        this.context = context;
        this.TAG = TAG;
        this.actualUser = actualUser;
        this.friend = friend;
    }

    @Override protected void onPreExecute() {
        super.onPreExecute();
        if (!TAG.equals("GcmMessageHandler")) {
            toast = new LoadToast(context)
                    .setText(context.getResources().getString(R.string.blocking))
                    .setTextColor(context.getResources().getColor(R.color.black))
                    .setTranslationY(100)
                    .setProgressColor(context.getResources().getColor(R.color.orange_light))
                    .show();
        }
    }

    @Override protected Void doInBackground(Void... params) {
        if (Looper.myLooper()==null){
            Looper.prepare();
        }
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, this, null);
        Looper.loop();
        return null;
    }

    @Override protected void onPostExecute(Void loc) {
        super.onPostExecute(loc);
        if (location==null){
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        if (TAG.equals("ActivityFriendBlock")){
            ActivityFriendBlock activityFriendBlock = (ActivityFriendBlock) context;
            activityFriendBlock.blockContact(location,toast);
        }
        else if(TAG.equals("ActivityGroupBlock")){
            ActivityGroupBlock activityGroupBlock = (ActivityGroupBlock) context;
            activityGroupBlock.blockGroup(location,toast);
        }
        else if(TAG.equals("ContactsFragment")){
            ActivityHome homeActivity = (ActivityHome) context;
            homeActivity.blockContact(location,toast,actualUser,friend);

        }
        else{
            GcmMessageHandler messageHandler = (GcmMessageHandler) context;
            messageHandler.setLocation(location);
        }
    }

    @Override public void onLocationChanged(Location loc) {
        location = loc;
        if (loc.getLongitude()!=0.0 && loc.getLatitude()!=0.0) {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> list = null;
            try {
                list = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (list!=null && !list.isEmpty()) {
                Address address = list.get(0);
                Log.i("Address", address.toString());
            }
        }
        Looper.myLooper().quit();
    }

    @Override public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override public void onProviderEnabled(String provider) {

    }

    @Override public void onProviderDisabled(String provider) {

    }

    public Location getLocation(){
        return location;
    }
}

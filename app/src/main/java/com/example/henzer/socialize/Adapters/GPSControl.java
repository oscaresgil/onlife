package com.example.henzer.socialize.Adapters;

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

import com.example.henzer.socialize.BlockActivity.FriendActionActivity;
import com.example.henzer.socialize.BlockActivity.GroupActionActivity;
import com.example.henzer.socialize.GCMClient.GcmMessageHandler;
import com.example.henzer.socialize.R;

import net.steamcrafted.loadtoast.LoadToast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static com.example.henzer.socialize.Adapters.StaticMethods.turnGPSOff;
import static com.example.henzer.socialize.Adapters.StaticMethods.turnGPSOn;

public class GPSControl extends AsyncTask<Void,Void,Void> implements LocationListener{

    private LoadToast toast;
    private Context context;
    private Location location;
    private boolean blockFriend;
    private boolean blockGroup;

    private LocationManager locationManager;

    public GPSControl(Context context, boolean blockFriend, boolean blockGroup){
        this.context = context;
        this.blockFriend = blockFriend;
        this.blockGroup = blockGroup;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        turnGPSOn(context);
        if (blockFriend || blockGroup) {
            toast = new LoadToast(context)
                    .setText("Blocking..")
                    .setTextColor(context.getResources().getColor(R.color.black))
                    .setTranslationY(100)
                    .setProgressColor(context.getResources().getColor(R.color.orange_light))
                    .show();
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        if (Looper.myLooper()==null){
            Looper.prepare();
        }
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, this, null);
        Looper.loop();
        return null;
    }

    @Override
    protected void onPostExecute(Void loc) {
        super.onPostExecute(loc);

        turnGPSOff(context);
        if (blockFriend){
            FriendActionActivity.blockContact(context,location,toast);
        }
        else if(blockGroup){
            GroupActionActivity.blockGroup(context,location,toast);
        }
        else{
            GcmMessageHandler.stopLoop();
        }
    }

    @Override
    public void onLocationChanged(Location loc) {
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

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public Location getLocation(){
        return location;
    }
}

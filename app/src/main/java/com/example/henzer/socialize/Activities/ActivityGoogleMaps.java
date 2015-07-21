package com.example.henzer.socialize.Activities;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.henzer.socialize.R;
import com.gc.materialdesign.views.ButtonIcon;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kenny.snackbar.SnackBar;

import net.steamcrafted.loadtoast.LoadToast;

import java.text.DecimalFormat;

import static com.example.henzer.socialize.Controller.StaticMethods.distFrom;

public class ActivityGoogleMaps extends Activity  implements OnMapReadyCallback {
    private GoogleMap map;
    private LatLng loc;
    private LatLng myLoc;
    private String user;
    private double latitude;
    private double longitude;
    private double distance = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps);

        user = getIntent().getStringExtra("user");
        latitude = getIntent().getDoubleExtra("latitude",0.0);
        longitude = getIntent().getDoubleExtra("longitude",0.0);

        loc = new LatLng(latitude, longitude);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        LinearLayout layout = (LinearLayout) findViewById(R.id.menu_in_map);
        ButtonIcon closeB = (ButtonIcon) findViewById(R.id.close_button);
        closeB.setDrawableIcon(getResources().getDrawable(R.drawable.ic_content_clear));
        closeB.setRippleColor(getResources().getColor(R.color.orange_light));
        closeB.setDrawingCacheBackgroundColor(getResources().getColor(R.color.orange_light));
        ButtonIcon homeB = (ButtonIcon) findViewById(R.id.home_button);
        homeB.setDrawableIcon(getResources().getDrawable(R.drawable.ic_action_home));
        homeB.setRippleColor(getResources().getColor(R.color.orange_light));
        homeB.setDrawingCacheBackgroundColor(getResources().getColor(R.color.orange_light));
        layout.bringToFront();
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        this.map = map;

        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.getUiSettings().setCompassEnabled(false);
        map.getUiSettings().setMapToolbarEnabled(false);
        map.getUiSettings().setZoomControlsEnabled(false);
        map.getUiSettings().setTiltGesturesEnabled(true);
        map.setMyLocationEnabled(true);

        final LoadToast toast = new LoadToast(this)
                .setText(getResources().getString(R.string.getting_actual_position))
                .setTextColor(getResources().getColor(R.color.black))
                .setTranslationY(100)
                .setProgressColor(getResources().getColor(R.color.orange_light))
                .show();

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 15));

        map.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_social_person))
                .title(user)
                .snippet(user + " " + getResources().getString(R.string.blocked_here))
                .position(loc));

        map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                toast.success();
                myLoc = new LatLng(location.getLatitude(), location.getLongitude());
                double distance2 = distFrom(latitude, longitude, location.getLatitude(), location.getLongitude());
                if (Math.abs(distance - distance2) > 5){
                    distance = distance2;
                    DecimalFormat df = new DecimalFormat("#.###");
                    SnackBar.show(ActivityGoogleMaps.this,getResources().getString(R.string.distance)+": "+df.format(distance)+"m");
                }
            }
        });
    }
    public void getHome(View v){
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(myLoc,15));
    }
    public void closeMap(View v){
        finish();
    }
}

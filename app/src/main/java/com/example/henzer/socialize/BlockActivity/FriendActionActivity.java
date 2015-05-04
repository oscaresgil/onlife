package com.example.henzer.socialize.BlockActivity;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.NumberPicker;

import com.example.henzer.socialize.Controller.SendNotification;
import com.example.henzer.socialize.Models.Person;
import com.example.henzer.socialize.R;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Boris on 01/05/2015.
 */
public class FriendActionActivity extends ActionBarActivity {
    public static final String TAG = "FriendActionActivity";
    private Person friend;
    private NumberPicker minPicker;
    private NumberPicker secPicker;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.orange_light)));
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_48dp);
        setContentView(R.layout.contact_action);

        Intent i = getIntent();
        friend = (Person)i.getSerializableExtra("data");

        minPicker = (NumberPicker) findViewById(R.id.timeMinBlock);
        secPicker = (NumberPicker) findViewById(R.id.timeSecBlock);
        minPicker.setMinValue(1); minPicker.setMaxValue(3);
        secPicker.setMinValue(0); secPicker.setMaxValue(9);

        actionBar.setTitle((Html.fromHtml("<b><font color=\"#000000\">" + friend.getName() + "</font></b>")));
        CircleImageView imageView = (CircleImageView) findViewById(R.id.avatar);
        imageView.setImageBitmap(cargarImagen(this,friend.getId()+""));
    }

    private Bitmap cargarImagen(Context context, String name){
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    public void bloquear(View view){
        SendNotification gcm = new SendNotification(this, "Enjoy your life. \nLeave the phone", "5 min");
        Log.e(TAG, "Bloquear a: " + friend.toString());
        gcm.execute(friend);
    }
}

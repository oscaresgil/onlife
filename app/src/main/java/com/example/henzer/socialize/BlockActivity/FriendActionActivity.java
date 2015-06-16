package com.example.henzer.socialize.BlockActivity;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.henzer.socialize.Controller.SendNotification;
import com.example.henzer.socialize.Adapters.GPSControl;
import com.example.henzer.socialize.Models.Person;
import com.example.henzer.socialize.R;
import com.gc.materialdesign.views.ButtonRectangle;
import com.kenny.snackbar.SnackBar;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrPosition;
import com.rengwuxian.materialedittext.MaterialEditText;

import net.steamcrafted.loadtoast.LoadToast;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Boris on 01/05/2015.
 */
public class FriendActionActivity extends ActionBarActivity {
    public static final String TAG = "FriendActionActivity";
    private Person actualUser;
    private Person friend;
    private MaterialEditText messageTextView;
    private TextView maxCharsView;
    private int maximumChars = 30;
    private int actualChar = 0;
    private ButtonRectangle blockButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SlidrConfig config = new SlidrConfig.Builder()
                .primaryColor(getResources().getColor(R.color.orange))
                .secondaryColor(getResources().getColor(R.color.orange_light))
                .position(SlidrPosition.LEFT)
                .sensitivity(1f)
                .build();

        Slidr.attach(this, config);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.orange_light)));
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_48dp);
        setContentView(R.layout.contact_action);

        Intent i = getIntent();
        friend = (Person)i.getSerializableExtra("data");
        actualUser = (Person) i.getSerializableExtra("actualuser");

        maxCharsView = (TextView) findViewById(R.id.maxCharactersContact);

        blockButton = (ButtonRectangle) findViewById(R.id.blockButtonContact);
        messageTextView = (MaterialEditText) findViewById(R.id.messageContact);
        messageTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(messageTextView.getWindowToken(), 0);
                    messageTextView.setFocusable(false);
                    messageTextView.clearFocus();
                }
            }
        });
        messageTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                actualChar = s.length();
                if (actualChar > 30) {
                    maxCharsView.setTextColor(getResources().getColor(R.color.red));
                    maxCharsView.setText(actualChar + "/" + maximumChars);
                } else {
                    maxCharsView.setTextColor(getResources().getColor(R.color.black));
                    maxCharsView.setText(actualChar + "/" + maximumChars);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        actionBar.setTitle((Html.fromHtml("<b><font color=\"#000000\">" + friend.getName() + "</font></b>")));
        CircleImageView imageView = (CircleImageView) findViewById(R.id.avatar);
        imageView.setImageBitmap(cargarImagen(this,friend.getId()+""));
    }

    @Override
    protected void onResume() {
        super.onResume();
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layout_contact);
        linearLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager)getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(messageTextView.getWindowToken(), 0);
                return false;
            }
        });
    }

    @Override
    protected void onDestroy() {
        InputMethodManager imm = (InputMethodManager)getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(messageTextView.getWindowToken(), 0);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        InputMethodManager imm = (InputMethodManager)getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(messageTextView.getWindowToken(), 0);
        overridePendingTransition(R.animator.push_left_inverted, R.animator.push_right_inverted);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(messageTextView.getWindowToken(), 0);
        overridePendingTransition(R.animator.push_left_inverted, R.animator.push_right_inverted);
        return true;
    }

    public void block(View view) {
        if (isNetworkAvailable()) {
            if (actualChar <= 30) {
                try {
                    new GPSControl(this,true,false).execute();
                    getIntent().putExtra("name", actualUser.getName());
                    getIntent().putExtra("message",messageTextView.getText().toString());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    SnackBar.show(FriendActionActivity.this, R.string.error);
                }
            } else {
                SnackBar.show(FriendActionActivity.this, R.string.max_characters, R.string.change_text, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        messageTextView.requestFocus();
                        InputMethodManager imm = (InputMethodManager) getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(messageTextView, 0);
                    }
                });
            }
        } else {

            SnackBar.show(FriendActionActivity.this, R.string.no_connection, R.string.change_connection, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                }
            });
        }
    }

    public static void blockContact(Context context, Location location, LoadToast toast){
        SnackBar.show((Activity)context, location.getLatitude() + "," + location.getLongitude());

        Person blockPerson = (Person)((Activity) context).getIntent().getSerializableExtra("data");
        String name = ((Activity)context).getIntent().getStringExtra("name");
        String message = ((Activity)context).getIntent().getStringExtra("message");
        SendNotification gcm = new SendNotification(context, name, message, location.getLatitude(), location.getLongitude(), toast);
        gcm.execute(blockPerson);
    }
}

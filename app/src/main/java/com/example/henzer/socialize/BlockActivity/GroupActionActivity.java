package com.example.henzer.socialize.BlockActivity;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.henzer.socialize.Controller.SendNotification;
import com.example.henzer.socialize.GroupInformationActivity;
import com.example.henzer.socialize.GroupsFragment;
import com.example.henzer.socialize.Models.Group;
import com.example.henzer.socialize.Models.Person;
import com.example.henzer.socialize.R;
import com.gc.materialdesign.views.ButtonRectangle;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrPosition;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.File;
import java.io.Serializable;
import java.util.List;

/**
 * Created by Boris on 02/05/2015.
 */
public class GroupActionActivity extends ActionBarActivity {
    public static final String TAG = "GroupActionActivity";
    private String nameGroup;
    private ImageView avatar;
    private Group group;
    private List<Person> friendsInGroup;
    private MaterialEditText messageTextView;
    private int maximumChars = 30;
    private int actualChar = 0;
    private ButtonRectangle blockButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        setContentView(R.layout.group_action);

        Intent i = getIntent();
        nameGroup = i.getStringExtra("name");
        group = (Group) i.getSerializableExtra("data");

        avatar = (ImageView) findViewById(R.id.avatar_group);
        avatar.setImageBitmap(cargarImagen(this, group.getName()));

        friendsInGroup = group.getFriendsInGroup();
        actionBar.setTitle((Html.fromHtml("<b><font color=\"#000000\">" + nameGroup + "</font></b>")));


        blockButton = (ButtonRectangle) findViewById(R.id.blockButtonGroup);
        messageTextView = (MaterialEditText) findViewById(R.id.messageGroup);
        final Handler handler = new Handler();
        messageTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(messageTextView, InputMethodManager.SHOW_IMPLICIT);
                    }

                }, 500000);
            }
        });
        messageTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                actualChar = maximumChars - s.length();
                if (actualChar < 0) {
                    blockButton.setClickable(false);
                } else {
                    blockButton.setClickable(true);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layout_groups);
        linearLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_in_group, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.information_button){
            Intent intent = new Intent(this,GroupInformationActivity.class);
            intent.putExtra("data",(Serializable)friendsInGroup);
            startActivity(intent);
            overridePendingTransition(R.animator.push_right, R.animator.push_left);
        }
        else if(i == R.id.delete_group){
            GroupsFragment.removeGroup(group);
            InputMethodManager imm = (InputMethodManager)getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(messageTextView.getWindowToken(), 0);
            finish();
            overridePendingTransition(R.animator.push_left_inverted, R.animator.push_right_inverted);
        }
        else {
            InputMethodManager imm = (InputMethodManager)getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(messageTextView.getWindowToken(), 0);
            finish();
            overridePendingTransition(R.animator.push_left_inverted, R.animator.push_right_inverted);
        }
        return true;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void bloquear(View view){
        if (isNetworkAvailable()) {
            if (actualChar >= 0) {
                try {
                    SendNotification gcm = new SendNotification(this, messageTextView.getText().toString(), "5 min");
                    Log.e(TAG, "Bloquear a: " + friendsInGroup.toString());
                    gcm.execute(friendsInGroup.toArray(new Person[friendsInGroup.size()]));
                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), "There was an error", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Only 30 or less characters please", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(this, "No connection", Toast.LENGTH_LONG).show();
        }
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

}

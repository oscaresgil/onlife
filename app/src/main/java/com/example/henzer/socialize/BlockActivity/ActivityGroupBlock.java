package com.example.henzer.socialize.BlockActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.henzer.socialize.Adapters.AdapterContact;
import com.example.henzer.socialize.Adapters.AdapterGroup;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.henzer.socialize.Activities.ActivityGroupInformation;
import com.example.henzer.socialize.Fragments.FragmentGroups;
import com.example.henzer.socialize.Models.ModelPerson;
import com.example.henzer.socialize.Tasks.TaskSendNotification;
import com.example.henzer.socialize.Tasks.TaskGPS;
import com.example.henzer.socialize.Models.ModelGroup;
import com.example.henzer.socialize.R;
import com.kenny.snackbar.SnackBar;
import com.melnykov.fab.FloatingActionButton;
import com.mingle.entity.MenuEntity;
import com.mingle.sweetpick.BlurEffect;
import com.mingle.sweetpick.CustomDelegate;
import com.mingle.sweetpick.Delegate;
import com.mingle.sweetpick.DimEffect;
import com.mingle.sweetpick.RecyclerViewDelegate;
import com.mingle.sweetpick.SweetSheet;
import com.mingle.sweetpick.ViewPagerDelegate;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrPosition;
import com.rengwuxian.materialedittext.MaterialEditText;

import net.steamcrafted.loadtoast.LoadToast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.example.henzer.socialize.Controller.StaticMethods.loadImage;

public class ActivityGroupBlock extends ActionBarActivity {
    public static final String TAG = "ActivityGroupBlock";
    private String nameGroup;
    private int maximumChars = 30, actualChar = 0;

    private RelativeLayout rl;
    private SweetSheet sweetSheet;

    private ModelPerson actualUser;
    private ModelGroup modelGroup;
    private List<ModelPerson> friendsInGroup;

    private ImageView avatar;
    private TextView maxCharsView;
    private MaterialEditText messageTextView;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SlidrConfig config = new SlidrConfig.Builder()
                .primaryColor(getResources().getColor(R.color.orange))
                .secondaryColor(getResources().getColor(R.color.orange_light))
                .position(SlidrPosition.LEFT)
                .sensitivity(0.4f)
                .build();

        Slidr.attach(this, config);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.orange_light)));
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_48dp);
        setContentView(R.layout.activity_group_block);

        fab = (FloatingActionButton) findViewById(R.id.ActivityGroupBlock_ButtonBlock);
        Animation blinkAnim = AnimationUtils.loadAnimation(ActivityGroupBlock.this, R.anim.blink);
        fab.startAnimation(blinkAnim);
        fab.bringToFront();

        Intent i = getIntent();
        nameGroup = i.getStringExtra("name");
        modelGroup = (ModelGroup) i.getSerializableExtra("data");
        actualUser = (ModelPerson) i.getSerializableExtra("user");

        avatar = (ImageView) findViewById(R.id.ActivityGroupBlock_ImageViewAvatarGroup);
        avatar.setImageBitmap(loadImage(this, modelGroup.getName()));
        avatar.setScaleType(ImageView.ScaleType.CENTER_CROP);

        friendsInGroup = modelGroup.getFriendsInGroup();
        actionBar.setTitle((Html.fromHtml("<b><font color=\"#000000\">" + nameGroup + "</font></b>")));

        maxCharsView = (TextView) findViewById(R.id.ActivityGroupBlock_TextViewMaxCharacter);
        messageTextView = (MaterialEditText) findViewById(R.id.ActivityGroupBlock_EditTextMessage);
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
            public void afterTextChanged(Editable s) {}
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.ActivityGroupBlock_ButtonBlock);
        fab.bringToFront();

        rl = (RelativeLayout) findViewById(R.id.ActivityGroupBlock_LayoutMain);
        sweetSheet = new SweetSheet(rl);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        InputMethodManager imm = (InputMethodManager)getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(messageTextView.getWindowToken(), 0);
        overridePendingTransition(R.animator.push_left_inverted, R.animator.push_right_inverted);
    }

    @Override
    protected void onResume() {
        super.onResume();
        RelativeLayout linearLayout = (RelativeLayout) findViewById(R.id.ActivityGroupBlock_LayoutMain);
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
            if (!sweetSheet.isShow()) {
                List<MenuEntity> menuEntities = new ArrayList<>();
                for (int j = 0; j < friendsInGroup.size(); j++) {
                    MenuEntity menuEntity = new MenuEntity();
                    ModelPerson f = friendsInGroup.get(j);
                    menuEntity.title = f.getName();
                    menuEntity.icon = new BitmapDrawable(getResources(), loadImage(this, f.getId()));
                    menuEntities.add(menuEntity);
                }
                sweetSheet.setMenuList(menuEntities);

                if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    sweetSheet.setDelegate(new RecyclerViewDelegate(true));
                }
                else{
                    sweetSheet.setDelegate(new RecyclerViewDelegate(false));
                }
                sweetSheet.setBackgroundEffect(new DimEffect(0.8f));
                sweetSheet.setOnMenuItemClickListener(new SweetSheet.OnMenuItemClickListener() {
                    @Override
                    public boolean onItemClick(int position, MenuEntity menuEntity1) {
                        if (position!=0) {
                            ModelPerson friend = friendsInGroup.get(position-1);
                            Intent intent = new Intent(ActivityGroupBlock.this, ActivityFriendBlock.class);
                            intent.putExtra("data", friend);
                            intent.putExtra("actualuser", actualUser);
                            startActivity(intent);
                            overridePendingTransition(R.animator.push_right, R.animator.push_left);
                            return true;
                        }
                        else{
                            return false;
                        }
                    }
                });
                sweetSheet.toggle();
            }
            else{
                sweetSheet.dismiss();
            }

            /*Intent intent = new Intent(this,ActivityGroupInformation.class);
            intent.putExtra("data",(Serializable)friendsInGroup);
            intent.putExtra("user",actualUser);
            startActivity(intent);
            overridePendingTransition(R.animator.push_right, R.animator.push_left);*/
        }
        else if(i == R.id.delete_group){
            new MaterialDialog.Builder(this)
                .title(R.string.delete)
                .content(R.string.really_delete)
                .positiveText(R.string.yes)
                .positiveColorRes(R.color.orange_light)
                .negativeText(R.string.no)
                .negativeColorRes(R.color.red)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        FragmentGroups.removeGroup(modelGroup);
                        dialog.dismiss();
                        dialog.cancel();

                        new MaterialDialog.Builder(ActivityGroupBlock.this)
                            .title("Group " + modelGroup.getName() + " deleted!")
                            .positiveText(R.string.yes)
                            .positiveColorRes(R.color.orange_light)
                            .callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    InputMethodManager imm = (InputMethodManager) getSystemService(
                                            Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(messageTextView.getWindowToken(), 0);
                                    finish();
                                    overridePendingTransition(R.animator.push_left_inverted, R.animator.push_right_inverted);
                                }
                            })
                            .show();
                    }
                }).show();
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

    public void block(View view){
        if (isNetworkAvailable()) {
            if (actualChar <= 30) {
                try {
                    new TaskGPS(this,TAG).execute();
                } catch (Exception ex) {
                    SnackBar.show(ActivityGroupBlock.this, R.string.error);
                }
            } else {
                SnackBar.show(ActivityGroupBlock.this, R.string.message_max_characters, R.string.button_change_text_message, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        messageTextView.requestFocus();
                        InputMethodManager imm = (InputMethodManager) getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(messageTextView, 0);
                    }
                });
            }
        }else{
            SnackBar.show(ActivityGroupBlock.this, R.string.no_connection, R.string.button_change_connection, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                }
            });
        }
    }

    public void blockGroup(Location location, LoadToast toast){
        SnackBar.show(ActivityGroupBlock.this, location.getLatitude() + "," + location.getLongitude());
        TaskSendNotification gcm = new TaskSendNotification(ActivityGroupBlock.this, actualUser.getName(), messageTextView.getText().toString(), location.getLatitude(), location.getLongitude(), toast);
        gcm.execute(friendsInGroup.toArray(new ModelPerson[friendsInGroup.size()]));
    }
}

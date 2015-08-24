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
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.example.henzer.socialize.Listeners.MessageFocusChangedListener;
import com.example.henzer.socialize.Listeners.TextWatcherListener;
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
import com.squareup.picasso.Picasso;

import net.steamcrafted.loadtoast.LoadToast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.example.henzer.socialize.Controller.StaticMethods.isNetworkAvailable;
import static com.example.henzer.socialize.Controller.StaticMethods.loadImage;
import static com.example.henzer.socialize.Controller.StaticMethods.loadImagePath;

public class ActivityGroupBlock extends AppCompatActivity {
    public static final String TAG = "ActivityGroupBlock";
    private int actualChar = 0;

    private RelativeLayout rl;
    private SweetSheet sweetSheet;
    private Toolbar toolbar;

    private ModelPerson actualUser;
    private ModelGroup modelGroup;
    private List<ModelPerson> friendsInGroup;

    private TextView maxCharsView;
    private MaterialEditText messageTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SlidrConfig config = new SlidrConfig.Builder().primaryColor(getResources().getColor(R.color.orange)).secondaryColor(getResources().getColor(R.color.orange_light)).position(SlidrPosition.LEFT).sensitivity(0.4f).build();
        Slidr.attach(this, config);

        setContentView(R.layout.activity_group_block);

        toolbar = (Toolbar) findViewById(R.id.ActivityGroupBlock_ToolBar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        }

        Intent i = getIntent();
        modelGroup = (ModelGroup) i.getSerializableExtra("data");
        actualUser = (ModelPerson) i.getSerializableExtra("user");
        friendsInGroup = modelGroup.getFriendsInGroup();

        CollapsingToolbarLayout collapser = (CollapsingToolbarLayout) findViewById(R.id.ActivityGroupBlock_CollapsingToolBarLayout);
        collapser.setTitle(modelGroup.getName());
        collapser.setCollapsedTitleTextColor(getResources().getColor(R.color.white));
        collapser.setExpandedTitleColor(getResources().getColor(R.color.white));

        Picasso.with(this).load(loadImagePath(this, modelGroup.getName())).into((ImageView) findViewById(R.id.ActivityGroupBlock_ImageViewContact));

        maxCharsView = (TextView) findViewById(R.id.ActivityGroupBlock_TextViewMaxCharacters);
        messageTextView = (MaterialEditText) findViewById(R.id.ActivityGroupBlock_EditTextMessage);
        messageTextView.setOnFocusChangeListener(new MessageFocusChangedListener(this,messageTextView));
        messageTextView.addTextChangedListener(new TextWatcherListener(this,maxCharsView));

        /*gridView = (GridView) findViewById(R.id.ActivityFriendBlock_GridLayout);

        final List<String> gifNames = setGifNames();

        gridView.setAdapter(new AdapterEmoticon(this,gifNames));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SnackBar.show(ActivityFriendBlock_2.this,gifNames.get(position));
            }
        });*/

        rl = (RelativeLayout) findViewById(R.id.ActivityGroupBlock_RelativeLayoutContact);
        sweetSheet = new SweetSheet(rl);
        sweetSheet.setBackgroundClickEnable(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*RelativeLayout linearLayout = (RelativeLayout) findViewById(R.id.ActivityGroupBlock_LayoutMain);
        linearLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
                return false;
            }
        });*/
    }

    @Override
    public void onBackPressed() {
        if (sweetSheet.isShow()){
            sweetSheet.dismiss();
        }
        else {
            InputMethodManager imm = (InputMethodManager) getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(messageTextView.getWindowToken(), 0);
            overridePendingTransition(R.animator.push_left_inverted, R.animator.push_right_inverted);
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
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

    public void block(View view){
        if (isNetworkAvailable(this)) {
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

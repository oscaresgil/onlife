package com.objective4.app.onlife.BlockActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem;
import com.objective4.app.onlife.Adapters.AdapterEmoticon;
import com.objective4.app.onlife.Listeners.ListenerMessageFocusChanged;
import com.objective4.app.onlife.Listeners.ListenerTextWatcher;
import com.objective4.app.onlife.Models.ModelGroup;
import com.objective4.app.onlife.Models.ModelPerson;
import com.objective4.app.onlife.Models.ModelSessionData;
import com.objective4.app.onlife.R;
import com.objective4.app.onlife.Tasks.TaskSendNotification;
import com.kenny.snackbar.SnackBar;
import com.mingle.entity.MenuEntity;
import com.mingle.sweetpick.DimEffect;
import com.mingle.sweetpick.RecyclerViewDelegate;
import com.mingle.sweetpick.SweetSheet;
import com.rengwuxian.materialedittext.MaterialEditText;

import net.soulwolf.widget.ratiolayout.widget.RatioImageView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

import static com.objective4.app.onlife.Controller.StaticMethods.activateDeviceAdmin;
import static com.objective4.app.onlife.Controller.StaticMethods.animationEnd;
import static com.objective4.app.onlife.Controller.StaticMethods.checkDeviceAdmin;
import static com.objective4.app.onlife.Controller.StaticMethods.hideSoftKeyboard;
import static com.objective4.app.onlife.Controller.StaticMethods.isNetworkAvailable;
import static com.objective4.app.onlife.Controller.StaticMethods.loadImage;
import static com.objective4.app.onlife.Controller.StaticMethods.performCrop;
import static com.objective4.app.onlife.Controller.StaticMethods.saveImage;
import static com.objective4.app.onlife.Controller.StaticMethods.setGifNames;
import static com.objective4.app.onlife.Controller.StaticMethods.setSlidr;
import static com.objective4.app.onlife.Controller.StaticMethods.showSoftKeyboard;

public class ActivityGroupBlock extends AppCompatActivity {
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_FILE = 2;
    private static final int PIC_CROP = 3;

    private RelativeLayout rl;
    private SweetSheet sweetSheet;
    private Toolbar toolbar;

    private ModelPerson actualUser;
    private ModelGroup modelGroup;
    private List<ModelPerson> friendsInGroup;

    private TextView maxCharsView;
    private MaterialEditText messageTextView;
    private ListenerTextWatcher listenerTextWatcher;

    private GridView gridView;
    private String gifName="";

    private RatioImageView avatarGroup;
    private Bitmap bitmapGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setSlidr(this);
        setContentView(R.layout.activity_group_block);

        toolbar = (Toolbar) findViewById(R.id.ActivityGroupBlock_ToolBar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        }

        Intent i = getIntent();
        modelGroup = (ModelGroup) i.getSerializableExtra("model_group");
        actualUser = ModelSessionData.getInstance().getUser();
        friendsInGroup = modelGroup.getFriendsInGroup();

        CollapsingToolbarLayout collapser = (CollapsingToolbarLayout) findViewById(R.id.ActivityGroupBlock_CollapsingToolBarLayout);
        collapser.setTitle(modelGroup.getName());
        collapser.setCollapsedTitleTextColor(getResources().getColor(R.color.white));
        collapser.setExpandedTitleColor(getResources().getColor(R.color.white));

        NestedScrollView nestedScrollView = (NestedScrollView) findViewById(R.id.ActivityGroupBlock_ScrollView);
        nestedScrollView.setNestedScrollingEnabled(false);

        avatarGroup = (RatioImageView) findViewById(R.id.ActivityGroupBlock_ImageViewContact);
        avatarGroup.setImageBitmap(loadImage(this,modelGroup.getName()));
        avatarGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTypeImage();
            }
        });

        maxCharsView = (TextView) findViewById(R.id.ActivityGroupBlock_TextViewMaxCharacters);
        messageTextView = (MaterialEditText) findViewById(R.id.ActivityGroupBlock_EditTextMessage);
        messageTextView.setOnFocusChangeListener(new ListenerMessageFocusChanged(this,messageTextView));
        listenerTextWatcher = new ListenerTextWatcher(this,maxCharsView,messageTextView);
        messageTextView.addTextChangedListener(listenerTextWatcher);

        gridView = (GridView) findViewById(R.id.ActivityGroupBlock_GridLayout);

        FloatingActionButton fabGif = (FloatingActionButton) findViewById(R.id.ActivityGroupBlock_FABEmoticon);
        fabGif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gridView.getVisibility() != View.VISIBLE) {
                    gridView.setVisibility(View.VISIBLE);
                    final List<String> gifNames = setGifNames();

                    gridView.setAdapter(new AdapterEmoticon(ActivityGroupBlock.this,gifNames));
                    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            try{
                                gifName = gifNames.get(position);
                                final GifImageView gifImageView = (GifImageView) findViewById(R.id.ActivityFriendBlock_EmoticonImage);
                                int resourceId = getResources().getIdentifier(gifName, "drawable", getPackageName());
                                GifDrawable gif = new GifDrawable(getResources(), resourceId);
                                gifImageView.setImageDrawable(gif);
                                gifImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

                                gifImageView.setOnLongClickListener(new View.OnLongClickListener() {
                                    @Override
                                    public boolean onLongClick(View v) {
                                        gifImageView.setImageBitmap(null);
                                        gifName = "";
                                        return false;
                                    }
                                });
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            gridView.setVisibility(View.GONE);
                        }
                    });

                }else{
                    gridView.setVisibility(View.GONE);
                    List<String> array = new ArrayList<>();
                    gridView.setAdapter(new AdapterEmoticon(ActivityGroupBlock.this,array));
                }
            }
        });

        rl = (RelativeLayout) findViewById(R.id.ActivityGroupBlock_RelativeLayoutMain);
        sweetSheet = new SweetSheet(rl);
        sweetSheet.setBackgroundClickEnable(true);
    }

    @Override
    public void onBackPressed() {
        if (sweetSheet.isShow()){
            sweetSheet.dismiss();
        }
        else {
            super.onBackPressed();
            hideSoftKeyboard(this,messageTextView);
            animationEnd(this);
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
                    menuEntity.icon = new BitmapDrawable(getResources(), loadImage(this, f.getId()+"_"+getResources().getInteger(R.integer.adapter_contact_size_little)));
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
                        ModelPerson friend = friendsInGroup.get(position);
                        Intent intent = new Intent(ActivityGroupBlock.this, ActivityFriendBlock.class);
                        intent.putExtra("data", friend);
                        intent.putExtra("actualuser", actualUser);
                        startActivity(intent);
                        overridePendingTransition(R.animator.push_right, R.animator.push_left);
                        return true;
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
                .title(getResources().getString(R.string.delete) + " " + modelGroup.getName())
                .content(R.string.really_delete)
                .positiveText(R.string.yes)
                .positiveColorRes(R.color.orange_light)
                .negativeText(R.string.no)
                .negativeColorRes(R.color.red)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {

                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("delete_group",modelGroup);
                        setResult(RESULT_OK,returnIntent);

                        dialog.dismiss();
                        dialog.cancel();
                        finish();
                        animationEnd(ActivityGroupBlock.this);
                    }
                }).show();
        }
        else {
            hideSoftKeyboard(this,messageTextView);
            finish();
            animationEnd(this);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == PICK_FROM_FILE || requestCode == PICK_FROM_CAMERA) && resultCode == RESULT_OK) {
            Uri mImageCaptureUri = data.getData();
            try {
                performCrop(this,mImageCaptureUri,PIC_CROP);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == PIC_CROP && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            bitmapGroup = extras.getParcelable("data");
            avatarGroup.setImageBitmap(bitmapGroup);
            saveImage(this,modelGroup.getName(),bitmapGroup);
        }
    }

    public void block(View view){
        if (isNetworkAvailable(this)) {
            hideSoftKeyboard(this,messageTextView);
            boolean devAdmin = checkDeviceAdmin(this);
            if (listenerTextWatcher.getActualChar() <= 30 && devAdmin) {
                try {
                    new TaskSendNotification(ActivityGroupBlock.this, actualUser.getName(), messageTextView.getText().toString(),gifName).execute(friendsInGroup.toArray(new ModelPerson[friendsInGroup.size()]));
                } catch (Exception ex) {
                    SnackBar.show(ActivityGroupBlock.this, R.string.error);
                }
            }else if(!devAdmin){
                activateDeviceAdmin(this);
            } else {
                SnackBar.show(ActivityGroupBlock.this, R.string.message_max_characters, R.string.button_change_text_message, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        messageTextView.requestFocus();
                        showSoftKeyboard(ActivityGroupBlock.this,messageTextView);
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
    private void selectTypeImage(){
        MaterialSimpleListAdapter materialAdapter = new MaterialSimpleListAdapter(this);
        materialAdapter.add(new MaterialSimpleListItem.Builder(this)
                .content(R.string.photo_option_camera)
                .icon(R.drawable.ic_camera_alt_black_24dp)
                .build());
        materialAdapter.add(new MaterialSimpleListItem.Builder(this)
                .content(R.string.photo_option_sd)
                .icon(R.drawable.ic_sd_card_black_24dp)
                .build());

        final MaterialDialog.Builder materialDialog = new MaterialDialog.Builder(this)
                .title(R.string.photo_select_image)
                .titleColorRes(R.color.orange_light)
                .adapter(materialAdapter, new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog materialDialog, View view, int which, CharSequence charSequence) {
                        if (which == 0) {
                            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
							startActivityForResult(cameraIntent,PICK_FROM_CAMERA);
                            materialDialog.cancel();
                        } else {
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.pick_image)), PICK_FROM_FILE);
                            materialDialog.cancel();
                        }
                    }
                });
        materialDialog.show();
    }
}

package com.example.henzer.socialize.BlockActivity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem;
import com.example.henzer.socialize.Adapters.AdapterEmoticon;
import com.example.henzer.socialize.Fragments.FragmentGroups;
import com.example.henzer.socialize.Listeners.MessageFocusChangedListener;
import com.example.henzer.socialize.Listeners.TextWatcherListener;
import com.example.henzer.socialize.Models.ModelPerson;
import com.example.henzer.socialize.Tasks.TaskSendNotification;
import com.example.henzer.socialize.Models.ModelGroup;
import com.example.henzer.socialize.R;
import com.kenny.snackbar.SnackBar;
import com.mingle.entity.MenuEntity;
import com.mingle.sweetpick.DimEffect;
import com.mingle.sweetpick.RecyclerViewDelegate;
import com.mingle.sweetpick.SweetSheet;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrPosition;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

import static com.example.henzer.socialize.Controller.StaticMethods.getRealPathFromURI;
import static com.example.henzer.socialize.Controller.StaticMethods.isNetworkAvailable;
import static com.example.henzer.socialize.Controller.StaticMethods.loadImage;
import static com.example.henzer.socialize.Controller.StaticMethods.loadImagePath;
import static com.example.henzer.socialize.Controller.StaticMethods.performCrop;
import static com.example.henzer.socialize.Controller.StaticMethods.saveImage;
import static com.example.henzer.socialize.Controller.StaticMethods.setGifNames;

public class ActivityGroupBlock extends AppCompatActivity {
    public static final String TAG = "ActivityGroupBlock";
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
    private TextWatcherListener textWatcherListener;

    private GridView gridView;
    private String gifName="";

    private ImageView avatarGroup;
    private Uri mImageCaptureUri;
    private String path;
    private Bitmap bitmapGroup;

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

        avatarGroup = (ImageView) findViewById(R.id.ActivityGroupBlock_ImageViewContact);
        avatarGroup.setImageBitmap(loadImage(this,modelGroup.getName()));

        selectTypeImage();
        //Picasso.with(this).load(loadImagePath(this, modelGroup.getName())).into(avatarGroup);

        maxCharsView = (TextView) findViewById(R.id.ActivityGroupBlock_TextViewMaxCharacters);
        messageTextView = (MaterialEditText) findViewById(R.id.ActivityGroupBlock_EditTextMessage);
        messageTextView.setOnFocusChangeListener(new MessageFocusChangedListener(this,messageTextView));
        textWatcherListener = new TextWatcherListener(this,maxCharsView,messageTextView);
        messageTextView.addTextChangedListener(textWatcherListener);

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
                                final GifImageView gifImageView = (GifImageView) findViewById(R.id.ActivityFriendBlock_GifImage);
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
                        ModelPerson friend = friendsInGroup.get(position-1);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("ResultCode",""+resultCode);
        Log.e("RequestCode", "" + requestCode);
        if (requestCode == PIC_CROP && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            bitmapGroup = extras.getParcelable("data");
            saveImage(this,modelGroup.getName(),bitmapGroup);
            avatarGroup.setImageBitmap(null);
            avatarGroup.setImageBitmap(bitmapGroup);
        }
        else if (requestCode == PICK_FROM_FILE && resultCode == RESULT_OK) {
            mImageCaptureUri = data.getData();
            // From Gallery
            path = getRealPathFromURI(this,mImageCaptureUri);
            if (path == null) {
                // From File Manager
                path = mImageCaptureUri.getPath();
            }
            if (path != null) {
                try {
                    performCrop(this,mImageCaptureUri,PIC_CROP);
                    //performCrop();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == PICK_FROM_CAMERA && resultCode==RESULT_OK) {
            path = mImageCaptureUri.getPath();
            try {
                performCrop(this,mImageCaptureUri,PIC_CROP);
                //performCrop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void block(View view){
        if (isNetworkAvailable(this)) {
            if (textWatcherListener.getActualChar() <= 30) {
                try {
                    new TaskSendNotification(ActivityGroupBlock.this, actualUser.getName(), messageTextView.getText().toString(),"").execute(friendsInGroup.toArray(new ModelPerson[friendsInGroup.size()]));

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
    public void selectTypeImage(){
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
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            File file = new File(Environment.getExternalStorageDirectory(), "tmp_avatar_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                            mImageCaptureUri = Uri.fromFile(file);
                            try {
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                                intent.putExtra("return-data", true);
                                startActivityForResult(intent, PICK_FROM_CAMERA);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
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

        avatarGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialog.show();
                materialDialog.show();
            }
        });
    }
    /*public void performCrop(){
        try {
            Log.e("ImageUriString",mImageCaptureUri.toString());

            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setType("image*//*");

            if (mImageCaptureUri.toString().substring(0,21).equals("content://com.android")) {
                Log.e("Path",mImageCaptureUri.toString().split("%3A")[1]);
                String imageUriString = "content://media/external/images/media/"+mImageCaptureUri.toString().split("%3A")[1];
                Log.e("PathCorrect",imageUriString);
                mImageCaptureUri = Uri.parse(imageUriString);
            }

            List<ResolveInfo> list = getPackageManager().queryIntentActivities( intent, 0 );
            int size = list.size();
            Log.e("SIZE",""+size);
            if (size != 0) {
                intent.setData(mImageCaptureUri);
                intent.putExtra("crop", "true");
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("outputX", 400);
                intent.putExtra("outputY", 400);
                intent.putExtra("return-data", true);
                if (size > 0) {
                    Intent i = new Intent(intent);
                    ResolveInfo res = list.get(0);
                    i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

                    startActivityForResult(intent, PIC_CROP);
                }
            }
            else{
                SnackBar.show(this, "NO CROP APP");
            }
        }
        catch(Exception e){
            Log.e("ErrorCrop",e.toString());
            e.printStackTrace();
        }
        *//*catch(ActivityNotFoundException anfe){
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            SnackBar.show((Activity)context, errorMessage);
            anfe.printStackTrace();
        }*//*
    }*/
}

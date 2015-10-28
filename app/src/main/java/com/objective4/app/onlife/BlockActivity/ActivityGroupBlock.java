package com.objective4.app.onlife.BlockActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem;
import com.mingle.entity.MenuEntity;
import com.mingle.sweetpick.DimEffect;
import com.mingle.sweetpick.RecyclerViewDelegate;
import com.mingle.sweetpick.SweetSheet;
import com.objective4.app.onlife.Models.ModelGroup;
import com.objective4.app.onlife.Models.ModelPerson;
import com.objective4.app.onlife.R;
import com.objective4.app.onlife.Tasks.TaskSendNotification;

import net.soulwolf.widget.ratiolayout.widget.RatioImageView;

import java.util.ArrayList;
import java.util.List;

import static com.objective4.app.onlife.Controller.StaticMethods.activateDeviceAdmin;
import static com.objective4.app.onlife.Controller.StaticMethods.animationEnd;
import static com.objective4.app.onlife.Controller.StaticMethods.checkDeviceAdmin;
import static com.objective4.app.onlife.Controller.StaticMethods.hideSoftKeyboard;
import static com.objective4.app.onlife.Controller.StaticMethods.imageInDisk;
import static com.objective4.app.onlife.Controller.StaticMethods.isNetworkAvailable;
import static com.objective4.app.onlife.Controller.StaticMethods.loadImage;
import static com.objective4.app.onlife.Controller.StaticMethods.makeSnackbar;
import static com.objective4.app.onlife.Controller.StaticMethods.performCrop;
import static com.objective4.app.onlife.Controller.StaticMethods.saveImage;
import static com.objective4.app.onlife.Controller.StaticMethods.showSoftKeyboard;

public class ActivityGroupBlock extends ActivityBlockBase<ModelGroup> {
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_FILE = 2;
    private static final int PIC_CROP = 3;

    private SweetSheet sweetSheet;
    private List<ModelPerson> friendsInGroup;
    private RatioImageView avatarGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        collapser.setTitle(actualObject.getName());

        friendsInGroup = actualObject.getFriendsInGroup();

        avatarGroup = (RatioImageView) findViewById(R.id.ActivityBlockBase_ImageViewContact);
        if (imageInDisk(this,actualObject.getName())) avatarGroup.setImageBitmap(loadImage(this, actualObject.getName()));
        else avatarGroup.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.ic_social_group));
        avatarGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTypeImage();
            }
        });

        RelativeLayout rl = (RelativeLayout) findViewById(R.id.ActivityBlockBase_RelativeLayoutMain);
        sweetSheet = new SweetSheet(rl);
        sweetSheet.setBackgroundClickEnable(true);

        messageTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE){
                    block(v);
                }
                return true;
            }
        });


    }

    @Override
    public void onBackPressed() {
        if (sweetSheet.isShow()){
            sweetSheet.dismiss();
        }
        else if(emoticonFlag){
            hideEmoticon();
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
                    if (imageInDisk(this,f.getId()+"_"+getResources().getInteger(R.integer.adapter_contact_size_little))){
                        menuEntity.icon = new BitmapDrawable(getResources(), loadImage(this, f.getId()+"_"+getResources().getInteger(R.integer.adapter_contact_size_little)));
                    }else{
                        menuEntity.icon = ContextCompat.getDrawable(this,R.drawable.loading_friend_icon);
                    }
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
                sweetSheet.toggle();
            }
            else{
                sweetSheet.dismiss();
            }
        }
        else if(i == R.id.delete_group){
            new MaterialDialog.Builder(this)
                .title(getResources().getString(R.string.delete) + " " + actualObject.getName())
                .content(R.string.really_delete)
                .positiveText(R.string.yes)
                .positiveColorRes(R.color.black)
                .negativeText(R.string.no)
                .negativeColorRes(R.color.accent)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {

                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("tag","delete");
                        returnIntent.putExtra("delete_group",actualObject);
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
            Bitmap bitmapGroup = extras.getParcelable("data");
            avatarGroup.setImageBitmap(bitmapGroup);
            saveImage(this, actualObject.getName(), bitmapGroup);

            Intent returnIntent = new Intent();
            returnIntent.putExtra("tag","image");
            returnIntent.putExtra("position",getIntent().getIntExtra("position", -1));
            setResult(RESULT_OK, returnIntent);
        }
    }

    public void block(View view){
        if (isNetworkAvailable(this)) {
            hideSoftKeyboard(this,messageTextView);
            boolean devAdmin = checkDeviceAdmin(this);
            if (listenerTextWatcher.getActualChar() <= 30 && devAdmin) {
                try {
                    new TaskSendNotification(ActivityGroupBlock.this, actualUser.getName(), messageTextView.getText().toString(),emoticonName).execute(friendsInGroup.toArray(new ModelPerson[friendsInGroup.size()]));
                    messageTextView.setText("");
                } catch (Exception ex) {
                    makeSnackbar(this, messageTextView, R.string.error, Snackbar.LENGTH_SHORT);
                }
            }else if(!devAdmin){
                activateDeviceAdmin(this);
            } else {
                makeSnackbar(this, messageTextView, R.string.max_characters_text, Snackbar.LENGTH_SHORT, R.string.button_change_text_message, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        messageTextView.requestFocus();
                        showSoftKeyboard(ActivityGroupBlock.this, messageTextView);
                    }
                });
            }
        }else{
            makeSnackbar(this, messageTextView, R.string.no_connection, Snackbar.LENGTH_LONG, R.string.button_change_connection, new View.OnClickListener() {
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
                .titleColorRes(R.color.accent)
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

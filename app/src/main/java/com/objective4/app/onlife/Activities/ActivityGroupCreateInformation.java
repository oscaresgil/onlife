package com.objective4.app.onlife.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem;
import com.kenny.snackbar.SnackBar;
import com.objective4.app.onlife.Adapters.AdapterSelectFriend;
import com.objective4.app.onlife.Models.ModelGroup;
import com.objective4.app.onlife.Models.ModelPerson;
import com.objective4.app.onlife.Models.ModelSessionData;
import com.objective4.app.onlife.R;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.List;

import static com.objective4.app.onlife.Controller.StaticMethods.animationEnd;
import static com.objective4.app.onlife.Controller.StaticMethods.hideSoftKeyboard;
import static com.objective4.app.onlife.Controller.StaticMethods.performCrop;
import static com.objective4.app.onlife.Controller.StaticMethods.saveImage;
import static com.objective4.app.onlife.Controller.StaticMethods.setHashToList;
import static com.objective4.app.onlife.Controller.StaticMethods.setSlidr;
import static com.objective4.app.onlife.Controller.StaticMethods.showSoftKeyboard;

public class ActivityGroupCreateInformation extends AppCompatActivity {
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_FILE = 2;
    private static final int PIC_CROP = 3;
    private FloatingActionButton fab;

    private List<ModelPerson> friends;
    private MenuItem searchItem;

    private AdapterSelectFriend adapterCheckList;
    private ImageView avatarGroup;
    private MaterialEditText nameNewGroup;

    private List<ModelPerson> friendsFiltred;
    private MaterialEditText searchText;
    private boolean isSearchOpened = false;
    private String mSearchQuery;

    private Bitmap bitmap = null;
    private String path = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_create_information);

        setSlidr(this);
        setSupportActionBar((Toolbar) findViewById(R.id.ActivityCreateGroup_ToolBar));
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        friends = setHashToList(ModelSessionData.getInstance().getFriends());

        avatarGroup = (ImageView) findViewById(R.id.ActivityCreateGroup_ImageButtonSelectImage);
        avatarGroup.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_image_camera_alt_large));
        avatarGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTypeImage();
            }
        });
        fab = (FloatingActionButton) findViewById(R.id.ActivityCreateGroup_ButtonDone);
        fab.bringToFront();
        nameNewGroup = (com.rengwuxian.materialedittext.MaterialEditText) findViewById(R.id.ActivityCreateGroup_EditTextNameGroup);

        friendsFiltred = new ArrayList<>();
        friendsFiltred.addAll(friends);

        RecyclerView listView = (RecyclerView) findViewById(R.id.ActivityCreateGroup_ListViewFriends);
        adapterCheckList = new AdapterSelectFriend(this,friendsFiltred);

        listView.setHasFixedSize(true);
        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.setAdapter(adapterCheckList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_group, menu);
        searchItem = menu.findItem(R.id.search_contact);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (isSearchOpened){
            handleMenuSearch();
        }
        else{
            super.onBackPressed();
            hideSoftKeyboard(this, nameNewGroup);
            animationEnd(this);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i==R.id.search_contact){
            handleMenuSearch();
        }else {
            if (isSearchOpened) {
                handleMenuSearch();
            } else {
                finish();
                animationEnd(this);
            }
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == PICK_FROM_FILE || requestCode == PICK_FROM_CAMERA) && resultCode == RESULT_OK) {
            Uri mImageCaptureUri = data.getData();
            path = mImageCaptureUri.getPath();
            try {
                performCrop(this,mImageCaptureUri,PIC_CROP);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == PIC_CROP && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            bitmap = extras.getParcelable("data");
            avatarGroup.setImageBitmap(bitmap);
        }
    }

    public void addGroup(View v){
        List<ModelPerson> selected = new ArrayList();
        hideSoftKeyboard(this,nameNewGroup);
        for (ModelPerson userData: friends){
            if (userData.isSelected()) {
                selected.add(userData);
            }
        }

        String name = nameNewGroup.getText().toString();
        int limit = 30;
        String state = "A";

        if (!name.equals("")  && !selected.isEmpty()){
            if (alreadyGroup(name)){
                SnackBar.show(ActivityGroupCreateInformation.this, R.string.group_already, R.string.button_group_change_name, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nameNewGroup.requestFocus();
                        showSoftKeyboard(ActivityGroupCreateInformation.this,nameNewGroup);
                    }
                });
            }
            else{
                if (selected.size()<6){
                    if (!path.equals("")) {
                        path = saveImage(getApplicationContext(), name, bitmap);
                    }
                    else{
                        path = saveImage(getApplicationContext(), name, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
                    }

                    ModelGroup newG = new ModelGroup(ModelSessionData.getInstance().getModelGroups().size(), name, selected, path, limit, state);

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("new_group", newG);
                    setResult(RESULT_OK, returnIntent);
                    finish();
                    animationEnd(this);
                }
                else{
                    SnackBar.show(ActivityGroupCreateInformation.this,getResources().getString(R.string.toast_group_only_less_than_five_friends));
                }
            }
        }
        else{
            SnackBar.show(ActivityGroupCreateInformation.this,getResources().getString(R.string.toast_group_not_yet_created));
        }
    }

    private List<ModelPerson> performSearch(List<ModelPerson> actualFriends, String query){
        String[] queryByWords = query.toLowerCase().split("\\s+");
        List<ModelPerson> filtred = new ArrayList<>();
        for (ModelPerson actual: actualFriends){
            String content = (
                    actual.getName()
            ).toLowerCase();

            for (String word: queryByWords){
                int numberOfMatches = queryByWords.length;
                if (content.contains(word)){
                    numberOfMatches--;
                }
                else{
                    break;
                }

                if (numberOfMatches == 0){
                    filtred.add(actual);
                }
            }
        }
        return filtred;
    }

    private void handleMenuSearch(){
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();

        LinearLayout mainLayout = (LinearLayout)this.findViewById(R.id.ActivityCreateGroup_LinearLayoutSubMain);
        // http://stackoverflow.com/questions/19765938/show-and-hide-a-view-with-a-slide-up-down-animation
        mainLayout.animate().setStartDelay(getResources().getInteger(R.integer.animation_search_contact_create));
        //layout_friends.animate().setStartDelay(getResources().getInteger(R.integer.animation_search_contact_create));
        fab.animate().setStartDelay(getResources().getInteger(R.integer.animation_search_contact_create));
        if (isSearchOpened){
            hideSoftKeyboard(this,searchText);
            hideSoftKeyboard(this, nameNewGroup);
            //mainLayout.setVisibility(View.VISIBLE);
            fab.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_done_black_24dp));
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addGroup(v);
                }
            });
            searchItem.setVisible(true);

            friendsFiltred.clear();
            friendsFiltred.addAll(friends);
            adapterCheckList.notifyDataSetChanged();
            actionBar.setDisplayShowCustomEnabled(false);
            actionBar.setDisplayShowTitleEnabled(true);

            isSearchOpened = false;

        } else{
            //mainLayout.setVisibility(View.GONE);
            fab.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_clear_black_24dp));
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isSearchOpened = true;
                    handleMenuSearch();
                }
            });
            searchItem.setVisible(false);

            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setCustomView(R.layout.layout_search_contact_bar);

            actionBar.setShowHideAnimationEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);

            searchText = (MaterialEditText) actionBar.getCustomView().findViewById(R.id.LayoutSearchContactBar_EditTextSearch);
            searchText.setVisibility(View.VISIBLE);
            searchText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    mSearchQuery = searchText.getText().toString();
                    friendsFiltred.clear();
                    friendsFiltred.addAll(performSearch(friends, mSearchQuery));
                    adapterCheckList.notifyDataSetChanged();
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
            searchText.requestFocus();
            showSoftKeyboard(this, searchText);

            isSearchOpened = true;
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

    private boolean alreadyGroup(String name){
        for (ModelGroup g: ModelSessionData.getInstance().getModelGroups()){
            if (g.getName().equals(name)){
                return true;
            }
        }
        return false;
    }
}
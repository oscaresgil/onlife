package com.objective4.app.onlife.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem;
import com.objective4.app.onlife.Adapters.AdapterCheckList;
import com.objective4.app.onlife.Listeners.ListenerFlipCheckbox;
import com.objective4.app.onlife.Models.ModelGroup;
import com.objective4.app.onlife.Models.ModelPerson;
import com.objective4.app.onlife.Models.ModelSessionData;
import com.objective4.app.onlife.R;
import com.kenny.snackbar.SnackBar;
import com.melnykov.fab.FloatingActionButton;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.List;

import static com.objective4.app.onlife.Controller.StaticMethods.animationEnd;
import static com.objective4.app.onlife.Controller.StaticMethods.hideSoftKeyboard;
import static com.objective4.app.onlife.Controller.StaticMethods.performCrop;
import static com.objective4.app.onlife.Controller.StaticMethods.saveImage;
import static com.objective4.app.onlife.Controller.StaticMethods.setSlidr;
import static com.objective4.app.onlife.Controller.StaticMethods.showSoftKeyboard;
import static com.objective4.app.onlife.Controller.StaticMethods.unSelectFriends;

public class ActivityGroupCreateInformation extends ActionBarActivity {
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_FILE = 2;
    private static final int PIC_CROP = 3;

    private Menu myMenu;
    private android.support.v7.app.ActionBar actionBar;

    private List<ModelPerson> friends;

    private AdapterCheckList adapterCheckList;
    private ListenerFlipCheckbox listener;
    private Animation animation1;
    private Animation animation2;

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

        setSlidr(this);

        friends = ModelSessionData.getInstance().getFriends();

        setContentView(R.layout.activity_group_create_information);
        setAnimationAndListeners();
        setAbTitle();

        avatarGroup = (ImageView) findViewById(R.id.ActivityCreateGroup_ImageButtonSelectImage);
        avatarGroup.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_image_camera_alt_large));
        avatarGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTypeImage();
            }
        });

        nameNewGroup = (com.rengwuxian.materialedittext.MaterialEditText) findViewById(R.id.ActivityCreateGroup_EditTextNameGroup);

        (findViewById(R.id.ActivityCreateGroup_FABSearchFriend)).bringToFront();

        friendsFiltred = new ArrayList<>();
        friendsFiltred.addAll(friends);

        ListView listView = (ListView) findViewById(R.id.ActivityCreateGroup_ListViewFriends);
        adapterCheckList = new AdapterCheckList(this, R.layout.layout_select_contact_group, friendsFiltred);
        listView.setAdapter(adapterCheckList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (view != null) {
                    ImageView avatar = (ImageView) view.findViewById(R.id.LayoutContact_ImageViewFriend);
                    ModelPerson actualFriend = (ModelPerson)avatar.getTag();
                    actualFriend.setSelected(!actualFriend.isSelected());

                    listener.setFriend(actualFriend);
                    listener.setView(avatar);

                    avatar.clearAnimation();
                    avatar.setAnimation(animation1);
                    avatar.startAnimation(animation1);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_group, menu);
        this.myMenu = menu;
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
            unSelectFriends(ModelSessionData.getInstance().getFriends());
            animationEnd(this);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.saveGroup_button) {
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
                        returnIntent.putExtra("new_group",newG);
                        setResult(RESULT_OK,returnIntent);

                        unSelectFriends(ModelSessionData.getInstance().getFriends());

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
        else{
            if (isSearchOpened){
                handleMenuSearch();
            }
            else {
                finish();
                unSelectFriends(ModelSessionData.getInstance().getFriends());
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

    public void search(View view){
        handleMenuSearch();
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
        MenuItem savegroup = myMenu.findItem(R.id.saveGroup_button);
        FloatingActionButton searchButton = (FloatingActionButton) findViewById(R.id.ActivityCreateGroup_FABSearchFriend);
        LinearLayout mainLayout = (LinearLayout)this.findViewById(R.id.ActivityCreateGroup_LinearLayoutSubMain);
        final FrameLayout layout_friends = (FrameLayout) this.findViewById(R.id.ActivityCreateGroup_FrameLayoutSelectFriends);

        // http://stackoverflow.com/questions/19765938/show-and-hide-a-view-with-a-slide-up-down-animation
        mainLayout.animate().setStartDelay(getResources().getInteger(R.integer.animation_search_contact_create));
        layout_friends.animate().setStartDelay(getResources().getInteger(R.integer.animation_search_contact_create));
        searchButton.animate().setStartDelay(getResources().getInteger(R.integer.animation_search_contact_create));
        if (isSearchOpened){
            hideSoftKeyboard(this,searchText);
            hideSoftKeyboard(this,nameNewGroup);
            mainLayout.setVisibility(View.VISIBLE);
            searchButton.setVisibility(View.VISIBLE);

            friendsFiltred.clear();
            friendsFiltred.addAll(friends);
            adapterCheckList.notifyDataSetChanged();
            actionBar.setDisplayShowCustomEnabled(false);
            actionBar.setDisplayShowTitleEnabled(true);

            savegroup.setVisible(true);
            isSearchOpened = false;

        } else{
            mainLayout.setVisibility(View.GONE);
            searchButton.setVisibility(View.GONE);

            savegroup.setVisible(false);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setCustomView(R.layout.layout_search_contact_bar);

            actionBar.setShowHideAnimationEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));

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
            showSoftKeyboard(this,searchText);
            searchButton.bringToFront();

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

    private boolean alreadyGroup(String name){
        for (ModelGroup g: ModelSessionData.getInstance().getModelGroups()){
            if (g.getName().equals(name)){
                return true;
            }
        }
        return false;
    }

    private void setAnimationAndListeners(){
        listener = new ListenerFlipCheckbox(ActivityGroupCreateInformation.this);
        animation1 = AnimationUtils.loadAnimation(this,R.anim.flip_left_out);
        animation2 = AnimationUtils.loadAnimation(this,R.anim.flip_left_in);
        animation1.setAnimationListener(listener); animation2.setAnimationListener(listener);
        listener.setAnimation1(animation1); listener.setAnimation2(animation2);
    }

    private void setAbTitle(){
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.orange_light)));
        actionBar.setTitle((Html.fromHtml("<b><font color=\"#000000\">" + getString(R.string.title_activity_new_group) + "</font></b>")));
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
    }
}
package com.example.henzer.socialize.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem;
import com.example.henzer.socialize.Adapters.AdapterCheckList;
import com.example.henzer.socialize.Fragments.FragmentGroups;
import com.example.henzer.socialize.Listeners.ListenerFlipCheckbox;
import com.example.henzer.socialize.Models.ModelGroup;
import com.example.henzer.socialize.Models.ModelPerson;
import com.example.henzer.socialize.Models.ModelSessionData;
import com.example.henzer.socialize.R;
import com.kenny.snackbar.SnackBar;
import com.melnykov.fab.FloatingActionButton;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrPosition;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.example.henzer.socialize.Controller.StaticMethods.getRealPathFromURI;
import static com.example.henzer.socialize.Controller.StaticMethods.isNetworkAvailable;
import static com.example.henzer.socialize.Controller.StaticMethods.performCrop;
import static com.example.henzer.socialize.Controller.StaticMethods.saveImage;

public class ActivityGroupCreateInformation extends ActionBarActivity {
    private ModelSessionData modelSessionData;
    private List<ModelPerson> friends;
    private AdapterCheckList adapterCheckList;
    public static final String TAG ="GroupCreateInfActivity";

    private Uri mImageCaptureUri;
    private ImageView avatarGroup;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_FILE = 2;
    private static final int PIC_CROP = 3;

    private Menu myMenu;
    private MaterialEditText searchText;
    private List<ModelPerson> friendsFiltred;
    private boolean isSearchOpened = false;
    private String mSearchQuery;
    private android.support.v7.app.ActionBar actionBar;

    private Bitmap bitmap = null;
    private String path = "";
    private MaterialEditText nameNewGroup;

    private Animation animation1;
    private Animation animation2;
    private ListenerFlipCheckbox listener;

    private int width;

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

        listener = new ListenerFlipCheckbox(ActivityGroupCreateInformation.this);
        animation1 = AnimationUtils.loadAnimation(this,R.anim.flip_left_out);
        animation2 = AnimationUtils.loadAnimation(this,R.anim.flip_left_in);
        animation1.setAnimationListener(listener); animation2.setAnimationListener(listener);
        listener.setAnimation1(animation1); listener.setAnimation2(animation2);

        setContentView(R.layout.activity_group_create_information);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x/5;

        avatarGroup = (ImageView) findViewById(R.id.ActivityCreateGroup_ImageButtonSelectImage);
        Picasso.with(this).load(R.drawable.ic_image_camera_alt_large).resize(width,width).into(avatarGroup);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.orange_light)));
        actionBar.setTitle((Html.fromHtml("<b><font color=\"#000000\">" + getString(R.string.title_activity_new_group) + "</font></b>")));
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);

        (findViewById(R.id.ActivityCreateGroup_FABSearchFriend)).bringToFront();

        Intent i = getIntent();
        modelSessionData = (ModelSessionData) i.getSerializableExtra("data");
        friends = modelSessionData.getFriends();

        friendsFiltred = new ArrayList<>();
        friendsFiltred.addAll(friends);

        nameNewGroup = (com.rengwuxian.materialedittext.MaterialEditText) findViewById(R.id.ActivityCreateGroup_EditTextNameGroup);

        ListView listView = (ListView) findViewById(R.id.ActivityCreateGroup_ListViewFriends);
        adapterCheckList = new AdapterCheckList(this, R.layout.layout_select_contact_group, friendsFiltred);
        listView.setAdapter(adapterCheckList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (view != null) {
                    ImageView avatar = (ImageView) view.findViewById(R.id.LayoutSelectContactGroup_ImageViewFriend);
                    ModelPerson actualFriend = (ModelPerson)avatar.getTag();
                    actualFriend.setSelected(!actualFriend.isSelected());

                    listener.setFriend(actualFriend);
                    listener.setView(avatar);
                    listener.setHome(false);
                    avatar.clearAnimation();
                    avatar.setAnimation(animation1);
                    avatar.startAnimation(animation1);
                }
            }
        });

        selectTypeImage();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == PIC_CROP){
                Bundle extras = data.getExtras();
                bitmap = extras.getParcelable("data");
            }
            avatarGroup.setImageBitmap(bitmap);
            avatarGroup.getLayoutParams().height = width;
            avatarGroup.getLayoutParams().width = width;
        }
        if (requestCode == PICK_FROM_FILE && resultCode == RESULT_OK) {
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == PICK_FROM_CAMERA && resultCode==RESULT_OK) {
            path = mImageCaptureUri.getPath();
            try {
                performCrop(this,mImageCaptureUri,PIC_CROP);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
            InputMethodManager imm = (InputMethodManager)getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(nameNewGroup.getWindowToken(), 0);
            overridePendingTransition(R.animator.push_left_inverted, R.animator.push_right_inverted);
            actionBar.setIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        List<ModelPerson> selected = new ArrayList();
        int i = item.getItemId();
        if (i == R.id.saveGroup_button) {
            InputMethodManager imm = (InputMethodManager)getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(nameNewGroup.getWindowToken(), 0);

            for (ModelPerson userData: friends){
                if (userData.isSelected()) {
                    Log.i("User is Checked", userData.getName());
                    selected.add(userData);
                }
            }
            String name = nameNewGroup.getText().toString();
            int limit = 30;
            String state = "A";

            if (!name.equals("")  && !selected.isEmpty()){
                if (isNetworkAvailable(this)) {
                    if (FragmentGroups.alreadyGroup(name)){
                        SnackBar.show(ActivityGroupCreateInformation.this, R.string.group_already, R.string.button_group_change_name, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                nameNewGroup.requestFocus();
                                InputMethodManager imm = (InputMethodManager)getSystemService(
                                        Context.INPUT_METHOD_SERVICE);
                                imm.showSoftInput(nameNewGroup,0);
                            }
                        });
                    }
                    else{
                        if (!path.equals("")) {
                            path = saveImage(getApplicationContext(), name, bitmap);
                        }
                        else{
                            path = saveImage(getApplicationContext(), name, BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
                        }
                    }
                }else{
                    SnackBar.show(ActivityGroupCreateInformation.this, R.string.no_connection, R.string.button_change_connection, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                        }
                    });
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
                overridePendingTransition(R.animator.push_left_inverted, R.animator.push_right_inverted);
            }
        }
        return true;
    }

    public void saveGroup(ModelGroup newG){
        if (newG.getId() != -1) {
            modelSessionData.getModelGroups().add(newG);
            try {
                saveGroupInSession(newG);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            FragmentGroups.addNewGroup(newG);
            finish();
            overridePendingTransition(R.animator.push_left_inverted, R.animator.push_right_inverted);
        }
    }

    public void search(View view){
        handleMenuSearch();
    }

    public List<ModelPerson> performSearch(List<ModelPerson> actualFriends, String query){
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

    public void handleMenuSearch(){
        MenuItem savegroup = myMenu.findItem(R.id.saveGroup_button);

        FloatingActionButton searchButton = (FloatingActionButton) findViewById(R.id.ActivityCreateGroup_FABSearchFriend);

        LinearLayout mainLayout = (LinearLayout)this.findViewById(R.id.ActivityCreateGroup_LinearLayoutSubMain);
        final FrameLayout layout_friends = (FrameLayout) this.findViewById(R.id.ActivityCreateGroup_FrameLayoutSelectFriends);

        mainLayout.animate().setStartDelay(getResources().getInteger(R.integer.animation_search_contact_create));
        layout_friends.animate().setStartDelay(getResources().getInteger(R.integer.animation_search_contact_create));
        searchButton.animate().setStartDelay(getResources().getInteger(R.integer.animation_search_contact_create));
        // http://stackoverflow.com/questions/19765938/show-and-hide-a-view-with-a-slide-up-down-animation

        if (isSearchOpened){

            InputMethodManager imm = (InputMethodManager)getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(nameNewGroup.getWindowToken(), 0);
            mainLayout.setVisibility(View.VISIBLE);
            searchButton.setVisibility(View.VISIBLE);

            friendsFiltred.clear();
            friendsFiltred.addAll(friends);
            adapterCheckList.notifyDataSetChanged();
            actionBar.setDisplayShowCustomEnabled(false);
            actionBar.setDisplayShowTitleEnabled(true);

            searchButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_search_black_24dp));
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
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(searchText,0);

            searchButton.bringToFront();
            searchButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_close_black_24dp));

            isSearchOpened = true;
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

    /*private void performCrop(){
        try {
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setType("image*//*");

            List<ResolveInfo> list = getPackageManager().queryIntentActivities( intent, 0 );
            int size = list.size();

            if (size >= 0) {
                intent.setData(mImageCaptureUri);
                intent.putExtra("crop", "true");
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("outputX", 900);
                intent.putExtra("outputY", 900);
                intent.putExtra("scale", true);
                intent.putExtra("return-data", true);

                Intent i = new Intent(intent);
                ResolveInfo res = list.get(0);
                i.setComponent( new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

                startActivityForResult(i, PIC_CROP);
            }
        }
        catch(ActivityNotFoundException anfe){
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            SnackBar.show(ActivityGroupCreateInformation.this, errorMessage);
        }
    }*/

    private void saveGroupInSession(ModelGroup modelGroup) throws JSONException {
        SharedPreferences prefe = getSharedPreferences
                (ActivityMain.MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefe.edit();

        JSONObject mySession = new JSONObject(prefe.getString("session", "{}"));
        Log.e(TAG, mySession.toString());

        JSONArray myGroups = mySession.getJSONArray("activity_groups");

        JSONObject obj = new JSONObject();
        obj.put("id", modelGroup.getId());
        obj.put("name", modelGroup.getName());
        obj.put("photo", modelGroup.getNameImage());
        obj.put("limit", modelGroup.getLimit());
        obj.put("state", modelGroup.getState());

        JSONArray arr = new JSONArray();
        for(ModelPerson p: modelGroup.getFriendsInGroup()){
            JSONObject friend = new JSONObject();
            friend.put("id", p.getId());
            friend.put("id_phone", p.getId_phone());
            friend.put("name", p.getName());
            friend.put("photo", p.getPhoto());
            friend.put("state", p.getState());
            friend.put("background", p.getBackground());
            arr.put(friend);
        }
        obj.put("people", arr);
        myGroups.put(obj);

        Log.e(TAG, mySession.toString());
        editor.putString("session", mySession.toString());
        editor.commit();
    }
}
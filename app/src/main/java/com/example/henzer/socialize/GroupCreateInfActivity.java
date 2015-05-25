package com.example.henzer.socialize;

import android.animation.Animator;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem;
import com.example.henzer.socialize.Controller.AddNewGroup;
import com.example.henzer.socialize.Models.Group;
import com.example.henzer.socialize.Models.Person;
import com.example.henzer.socialize.Models.SessionData;
import com.gc.materialdesign.views.CheckBox;
import com.melnykov.fab.FloatingActionButton;
import com.kenny.snackbar.SnackBar;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrPosition;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Boris on 02/05/2015.
 */
public class GroupCreateInfActivity extends ActionBarActivity {
    private SessionData sessionData;
    private List<Person> friends;
    private CheckListAdapter checkListAdapter;
    public static final String TAG ="GroupCreateInfActivity";

    private Uri mImageCaptureUri;
    private ImageButton avatarGroup;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_FILE = 2;
    private static final int PIC_CROP = 3;

    private Menu myMenu;
    private MenuItem mSearchAction;
    private MaterialEditText searchText;
    private List<Person> friendsFiltred;
    private boolean isSearchOpened = false;
    private String mSearchQuery;

    private Bitmap bitmap = null;
    private String path = "";
    private MaterialEditText nameNewGroup;

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

        setContentView(R.layout.group_create_information);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.orange_light)));
        actionBar.setTitle((Html.fromHtml("<b><font color=\"#000000\">" + getString(R.string.new_group) + "</font></b>")));
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_48dp);

        (findViewById(R.id.search_button)).bringToFront();

        Intent i = getIntent();
        sessionData = (SessionData) i.getSerializableExtra("data");
        friends = sessionData.getFriends();

        friendsFiltred = new ArrayList<>();
        friendsFiltred.addAll(friends);

        nameNewGroup = (com.rengwuxian.materialedittext.MaterialEditText) findViewById(R.id.nameNewGroup);

        final ListView listView = (ListView) findViewById(R.id.listView);
        checkListAdapter = new CheckListAdapter(this, R.layout.select_contact_group, friendsFiltred);
        listView.setAdapter(checkListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (view != null) {
                    CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox1);
                    Person actualFriend = (Person)checkBox.getTag();
                    actualFriend.setSelected(!checkBox.isCheck());
                    checkBox.setChecked(!checkBox.isCheck());
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
        }
        if (requestCode == PICK_FROM_FILE && resultCode == RESULT_OK) {
            mImageCaptureUri = data.getData();
            // From Gallery
            path = getRealPathFromURI(mImageCaptureUri);
            if (path == null) {
                // From File Manager
                path = mImageCaptureUri.getPath();
            }
            if (path != null) {
                try {
                    performCrop();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == PICK_FROM_CAMERA && resultCode==RESULT_OK) {
            path = mImageCaptureUri.getPath();
            try {
                performCrop();
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        mSearchAction = menu.findItem(R.id.searchContact);
        return super.onPrepareOptionsMenu(menu);
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
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        List<Person> selected = new ArrayList();
        int i = item.getItemId();
        if (i == R.id.saveGroup_button) {
            List<Person> friendsChecked = checkListAdapter.friends;
            for (Person userData: friendsChecked){
                if (userData.isSelected()) {
                    Log.i("User is Checked", userData.getName());
                    selected.add(userData);
                }
            }
            String name = nameNewGroup.getText().toString();
            int limit = 30;
            String state = "A";

            if (!name.equals("") && !path.equals("") && !friendsChecked.isEmpty()){
                if (isNetworkAvailable()) {
                    path = guardarImagen(getApplicationContext(), name, bitmap);
                    Group newG = new Group(0, name, selected, path, limit, state);
                    Log.e(TAG, newG.toString());
                    AddNewGroup addNewGroup = new AddNewGroup(GroupCreateInfActivity.this);
                    try {
                        newG = addNewGroup.execute(newG).get();
                        if (newG.getId() != -1) {
                            sessionData.getGroups().add(newG);
                            saveGroupInSession(newG);
                            GroupsFragment.addNewGroup(newG);
                            InputMethodManager imm = (InputMethodManager) getSystemService(
                                    Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(nameNewGroup.getWindowToken(), 0);
                            finish();
                            overridePendingTransition(R.animator.push_left_inverted, R.animator.push_right_inverted);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    SnackBar.show(GroupCreateInfActivity.this, R.string.no_connection, R.string.change_connection, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                        }
                    });
                }
            }
            else{
                SnackBar.show(GroupCreateInfActivity.this,"Name, Photo or Contacts not specified. Try Again");
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
        return super.onOptionsItemSelected(item);
    }

    public void search(View view){
        handleMenuSearch();
    }

    public List<Person> performSearch(List<Person> actualFriends, String query){
        String[] queryByWords = query.toLowerCase().split("\\s+");
        List<Person> filtred = new ArrayList<>();
        for (Person actual: actualFriends){
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
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        MenuItem savegroup = myMenu.findItem(R.id.saveGroup_button);
        MenuItem search = myMenu.findItem(R.id.searchCancelContact);
        FloatingActionButton searchButton = (FloatingActionButton) findViewById(R.id.search_button);

        LinearLayout mainLayout = (LinearLayout)this.findViewById(R.id.header);
        LinearLayout layout_friends = (LinearLayout) this.findViewById(R.id.body);

        mainLayout.animate().setStartDelay(getResources().getInteger(R.integer.animation_search_contact_create));
        layout_friends.animate().setStartDelay(getResources().getInteger(R.integer.animation_search_contact_create));
        searchButton.animate().setStartDelay(getResources().getInteger(R.integer.animation_search_contact_create));
        // http://stackoverflow.com/questions/19765938/show-and-hide-a-view-with-a-slide-up-down-animation

        if (isSearchOpened){

            InputMethodManager imm = (InputMethodManager)getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(nameNewGroup.getWindowToken(), 0);
            mainLayout.animate().translationY(0).start();
            searchButton.animate().translationY(0).start();
            layout_friends.animate().translationY(0).start();

            friendsFiltred.clear();
            friendsFiltred.addAll(friends);
            checkListAdapter.notifyDataSetChanged();
            actionBar.setDisplayShowCustomEnabled(false);
            actionBar.setDisplayShowTitleEnabled(true);

            searchButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_ic_search_black_48dp));
            search.setVisible(false);
            savegroup.setVisible(true);
            isSearchOpened = false;
        } else{
            mainLayout.animate().translationY(-mainLayout.getHeight()).start();
            searchButton.animate().translationY(-mainLayout.getHeight()+actionBar.getHeight()).start();
            layout_friends.animate().translationY(-mainLayout.getHeight()).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
                    imm.hideSoftInputFromWindow(nameNewGroup.getWindowToken(), 0);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(searchText,0);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
                    imm.hideSoftInputFromWindow(nameNewGroup.getWindowToken(), 0);
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            }).start();

            savegroup.setVisible(false);
            search.setVisible(true);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setCustomView(R.layout.search_contact_bar);
            actionBar.setShowHideAnimationEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);

            searchText = (MaterialEditText) actionBar.getCustomView().findViewById(R.id.search_contact_text);
            searchText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    mSearchQuery = searchText.getText().toString();
                    friendsFiltred.clear();
                    friendsFiltred.addAll(performSearch(friends, mSearchQuery));
                    checkListAdapter.notifyDataSetChanged();
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
            searchText.requestFocus();

            searchButton.bringToFront();
            searchButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_ic_close_black_48dp));

            isSearchOpened = true;
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void selectTypeImage(){
        MaterialSimpleListAdapter materialAdapter = new MaterialSimpleListAdapter(this);
        materialAdapter.add(new MaterialSimpleListItem.Builder(this)
                .content("From Camera")
                .icon(R.drawable.ic_photo_camera_black_48dp)
                .build());
        materialAdapter.add(new MaterialSimpleListItem.Builder(this)
                .content("From SD Card")
                .icon(R.drawable.ic_sim_card_black_48dp)
            .build());

        final MaterialDialog.Builder materialDialog = new MaterialDialog.Builder(this)
                .title("Select Image")
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
                            startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);
                            materialDialog.cancel();
                        }
                    }
            });

        avatarGroup = (ImageButton) findViewById(R.id.imageView);
        avatarGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialog.show();
                materialDialog.show();
            }
        });
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
        b = Bitmap.createScaledBitmap(b, 60, 60, false);
        return b;
    }

    public String getRealPathFromURI(Uri contentUri){
        String [] proj      = {MediaStore.Images.Media.DATA};
        Cursor cursor       = managedQuery( contentUri, proj, null, null,null);
        if (cursor == null) return null;
        int column_index    = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private void performCrop(){
        try {
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setType("image/*");

            List<ResolveInfo> list = getPackageManager().queryIntentActivities( intent, 0 );
            int size = list.size();

            if (size >= 0) {
                intent.setData(mImageCaptureUri);
                intent.putExtra("crop", "true");
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("outputX", 250);
                intent.putExtra("outputY", 250);
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
            SnackBar.show(GroupCreateInfActivity.this, errorMessage);
        }
    }

    private void saveGroupInSession(Group group) throws JSONException {
        SharedPreferences prefe = getSharedPreferences
                (MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefe.edit();

        JSONObject mySession = new JSONObject(prefe.getString("session", "{}"));
        Log.e(TAG, mySession.toString());

        JSONArray myGroups = mySession.getJSONArray("groups");

        JSONObject obj = new JSONObject();
        obj.put("id", group.getId());
        obj.put("name", group.getName());
        obj.put("photo", group.getNameImage());
        obj.put("limit", group.getLimit());
        obj.put("state", group.getState());

        JSONArray arr = new JSONArray();
        for(Person p: group.getFriendsInGroup()){
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

    private String guardarImagen(Context context, String name, Bitmap image){
        ContextWrapper cw = new ContextWrapper(context);
        File dirImages = cw.getDir("Profiles",Context.MODE_PRIVATE);
        File myPath = new File(dirImages, name+".png");

        FileOutputStream fos = null;
        try{
            fos = new FileOutputStream(myPath);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.flush();
        }catch (Exception e){e.printStackTrace();}
        Log.i("IMAGE SAVED","PATH: "+myPath);
        return myPath.getAbsolutePath();
    }

    private class CheckListAdapter extends ArrayAdapter<Person> {
        private List<Person> friends;

        public CheckListAdapter(Context context, int textViewResourceId, List<Person> friends) {
            super(context, textViewResourceId, friends);
            this.friends = friends;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Holder holder;
            if (convertView == null) {
                LayoutInflater inflater = getLayoutInflater();
                convertView = inflater.inflate(R.layout.select_contact_group, parent, false);

                holder = new Holder();
                holder.avatar = (ImageView) convertView.findViewById(R.id.avatar_friends);
                holder.name = (TextView) convertView.findViewById(R.id.name_friend);
                holder.check = (com.gc.materialdesign.views.CheckBox) convertView.findViewById(R.id.checkBox1);
                convertView.setTag(holder);
                holder.check.setOncheckListener(new CheckBox.OnCheckListener() {
                    @Override
                    public void onCheck(CheckBox cb, boolean b) {
                        Person friend = (Person) cb.getTag();
                        friend.setSelected(cb.isCheck());
                    }
                });
            }
            else {
                holder = (Holder) convertView.getTag();
            }

            Person friend = friends.get(position);
            holder.avatar.setImageBitmap(cargarImagen(GroupCreateInfActivity.this, friend.getId() + ""));
            holder.name.setText(friend.getName());
            holder.check.setSelected(friend.isSelected());
            holder.check.setChecked(friend.isSelected());
            holder.check.setTag(friend);
            return convertView;

        }
        private class Holder {
            ImageView avatar;
            TextView name;
            com.gc.materialdesign.views.CheckBox check;
        }
    }

}
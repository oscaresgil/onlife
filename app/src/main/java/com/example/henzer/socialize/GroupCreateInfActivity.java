package com.example.henzer.socialize;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.henzer.socialize.Controller.AddNewGroup;
import com.example.henzer.socialize.Models.Group;
import com.example.henzer.socialize.Models.Person;
import com.example.henzer.socialize.Models.SessionData;

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
    private EditText groupName;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_FILE = 2;
    private static final int PIC_CROP = 3;

    private Bitmap bitmap = null;
    private String path = "";
    private EditText nameNewGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.group_create_information);
        nameNewGroup = (EditText)findViewById(R.id.nameNewGroup);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.orange_light)));
        actionBar.setTitle((Html.fromHtml("<b><font color=\"#000000\">" + getString(R.string.new_group) + "</font></b>")));
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_48dp);

        Intent i = getIntent();
        sessionData = (SessionData) i.getSerializableExtra("data");
        friends = sessionData.getFriends();
        Log.i("Friends in Create Group",friends.toString());

        selectTypeImage();

        ListView listView = (ListView) findViewById(R.id.listView);
        checkListAdapter = new CheckListAdapter(this,R.layout.select_contact_group,friends);
        listView.setAdapter(checkListAdapter);
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
        return true;
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
                        finish();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else{
                Toast.makeText(getApplicationContext(),"Name, Photo or Contacts not specified. Try Again",Toast.LENGTH_LONG).show();
            }
        } else{
            finish();
            overridePendingTransition(R.animator.push_left, R.animator.push_right);
        }
        return super.onOptionsItemSelected(item);
    }

    public void selectTypeImage(){
        final String [] items = new String [] {"From Camera", "From SD Card"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String> (this, android.R.layout.select_dialog_item,items);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which==0){
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File file = new File(Environment.getExternalStorageDirectory(), "tmp_avatar_"+String.valueOf(System.currentTimeMillis()) + ".jpg");
                    mImageCaptureUri = Uri.fromFile(file);
                    try{
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                        intent.putExtra("return-data",true);
                        startActivityForResult(intent, PICK_FROM_CAMERA);
                    } catch(Exception e){e.printStackTrace();}
                    dialog.cancel();
                }
                else{
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Complete action using"),PICK_FROM_FILE);
                }
            }
        });
        final AlertDialog dialog = builder.create();
        avatarGroup = (ImageButton) findViewById(R.id.imageView);
        avatarGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
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
                intent.putExtra("outputX", 256);
                intent.putExtra("outputY", 256);
                intent.putExtra("scale", false);
                intent.putExtra("return-data", true);

                Intent i = new Intent(intent);
                ResolveInfo res = list.get(0);
                i.setComponent( new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

                startActivityForResult(i, PIC_CROP);
            }
        }
        catch(ActivityNotFoundException anfe){
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
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
                holder.check = (CheckBox) convertView.findViewById(R.id.checkBox1);
                convertView.setTag(holder);

                holder.check.setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v ;
                        Person friend = (Person) cb.getTag();
                        //Toast.makeText(getApplicationContext(), "Clicked on Checkbox: " + cb.getText() +" is " + cb.isChecked(),Toast.LENGTH_LONG).show();
                        friend.setSelected(cb.isChecked());
                    }
                });
            }
            else {
                holder = (Holder) convertView.getTag();
            }

            Person friend = friends.get(position);
            holder.avatar.setImageBitmap(cargarImagen(GroupCreateInfActivity.this,friend.getId()+""));
            holder.check.setText(friend.getName());
            holder.check.setSelected(friend.isSelected());
            holder.check.setTag(friend);
            return convertView;

        }
        private class Holder {
            ImageView avatar;
            TextView name;
            CheckBox check;
        }
    }

}
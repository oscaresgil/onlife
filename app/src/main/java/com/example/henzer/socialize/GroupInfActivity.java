package com.example.henzer.socialize;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by Boris on 02/05/2015.
 */
public class GroupInfActivity extends ActionBarActivity {
    private SessionData sessionData;
    private List<UserData> friends;
    private CheckListAdapter checkListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_information);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        sessionData = (SessionData) i.getSerializableExtra("data");
        friends = sessionData.getFriends();

        for(UserData u: friends){
            try {
                Bitmap b =new DownloadImageTask().execute(u.getId()).get();
                u.setIcon(b);
            }catch (Exception e){e.printStackTrace();}
        }

        ListView listView = (ListView) findViewById(R.id.listView);
        checkListAdapter = new CheckListAdapter(this,R.layout.select_contact_group,friends);
        listView.setAdapter(checkListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox cb = (CheckBox) view;
                UserData friend = (UserData) parent.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(),"Clicked on Friend: "+friend.getName(),Toast.LENGTH_SHORT).show();
                friend.setSelected(cb.isChecked());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_group, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.saveGroup_button) {
            List<UserData> friendsChecked = checkListAdapter.friends;
            for (UserData userData: friendsChecked){
                if (userData.isSelected())
                    Log.i("User is Checked",userData.getName());
            }
        } else{
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        Bitmap mIcon11;
        protected Bitmap doInBackground(String... urls) {
            try {
                String userID = urls[0];
                String urlStr = "https://graph.facebook.com/" + userID + "/picture?width=60&height=60";
                Bitmap img = null;

                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet(urlStr);
                HttpResponse response;
                try {
                    response = (HttpResponse)client.execute(request);
                    HttpEntity entity = response.getEntity();
                    BufferedHttpEntity bufferedEntity = new BufferedHttpEntity(entity);
                    InputStream inputStream = bufferedEntity.getContent();
                    img = BitmapFactory.decodeStream(inputStream);
                } catch (ClientProtocolException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return img;
            }catch(Exception e){e.printStackTrace();}
            return mIcon11;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            Log.i("BITMAP Icon", bitmap.toString());
        }
    }

    private class CheckListAdapter extends ArrayAdapter<UserData> {
        private List<UserData> friends;

        public CheckListAdapter(Context context, int textViewResourceId, List<UserData> friends) {
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
                        UserData friend = (UserData) cb.getTag();
                        Toast.makeText(getApplicationContext(),
                                "Clicked on Checkbox: " + cb.getText() +
                                        " is " + cb.isChecked(),
                                Toast.LENGTH_LONG).show();
                        friend.setSelected(cb.isChecked());
                    }
                });
            }
            else {
                holder = (Holder) convertView.getTag();
            }

            UserData friend = friends.get(position);
            holder.avatar.setImageBitmap(friend.getIcon());
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
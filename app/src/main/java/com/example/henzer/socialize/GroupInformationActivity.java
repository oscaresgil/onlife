package com.example.henzer.socialize;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.henzer.socialize.BlockActivity.FriendActionActivity;
import com.example.henzer.socialize.Models.Person;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrPosition;

import java.io.File;
import java.util.List;

/**
 * Created by Boris on 03/05/2015.
 */
public class GroupInformationActivity extends ActionBarActivity {
    private GroupAdapter adapter;
    private List<Person> friendsInGroup;

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

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.orange_light)));
        actionBar.setTitle((Html.fromHtml("<b><font color=\"#000000\">" + "Information" + "</font></b>")));
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_48dp);
        setContentView(R.layout.group_information);

        Intent i = getIntent();
        friendsInGroup = (List<Person>) i.getSerializableExtra("data");

        adapter = new GroupAdapter(this, R.layout.friends_in_group, friendsInGroup);
        ListView listView = (ListView) findViewById(R.id.friendInGroupList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Person friend = friendsInGroup.get(position);
                Intent intent = new Intent(GroupInformationActivity.this, FriendActionActivity.class);
                intent.putExtra("data",friend);
                startActivity(intent);
                Toast.makeText(GroupInformationActivity.this, "Clicked on Friend " + friend.getName(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        overridePendingTransition(R.animator.push_left_inverted, R.animator.push_right_inverted);
        return true;
    }

    class GroupAdapter extends ArrayAdapter<Person> {
        List<Person> objects;

        public GroupAdapter(Context context, int resource, List<Person> objects) {
            super(context, resource, objects);
            this.objects = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View rowView = inflater.inflate(R.layout.friends_in_group, null, true);
            ImageView avatar = (ImageView) rowView.findViewById(R.id.avatar_friend_in_group);
            TextView text = (TextView) rowView.findViewById(R.id.name_group);
            avatar.setImageBitmap(cargarImagen(GroupInformationActivity.this,objects.get(position).getId()));
            text.setText(objects.get(position).getName());
            return rowView;
        }
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
        return b;
    }
}

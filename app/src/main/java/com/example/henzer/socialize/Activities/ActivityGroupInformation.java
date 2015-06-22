package com.example.henzer.socialize.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import com.example.henzer.socialize.BlockActivity.ActivityFriendBlock;
import com.example.henzer.socialize.Models.Person;
import com.example.henzer.socialize.R;
import com.jpardogo.listbuddies.lib.adapters.CircularLoopAdapter;
import com.jpardogo.listbuddies.lib.views.ListBuddiesLayout;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrPosition;

import java.util.ArrayList;
import java.util.List;

import static com.example.henzer.socialize.Controller.StaticMethods.loadImage;

public class ActivityGroupInformation extends ActionBarActivity {
    private ListBuddiesLayout listViewBuddy;
    private ListView listView;

    private Person userData;
    private GroupAdapter adapter;
    private List<Person> friendsInGroup;

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

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.orange_light)));
        actionBar.setTitle((Html.fromHtml("<b><font color=\"#000000\">" + getResources().getString(R.string.title_activity_information) + "</font></b>")));
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_48dp);
        setContentView(R.layout.group_information);

        Intent i = getIntent();
        friendsInGroup = (List<Person>) i.getSerializableExtra("data");
        userData = (Person) i.getSerializableExtra("user");

        if (friendsInGroup.size()>=4){
            final List<Person> friends1 = new ArrayList<>();
            final List<Person> friends2 = new ArrayList<>();
            for (int it=0; it<friendsInGroup.size(); it++){
                if (it<(friendsInGroup.size() /2)){
                    friends1.add(friendsInGroup.get(it));
                }
                else{
                    friends2.add(friendsInGroup.get(it));
                }
            }

            Adapter adapter1 = new Adapter(this,friends1);
            Adapter adapter2 = new Adapter(this,friends2);
            listViewBuddy = (ListBuddiesLayout) findViewById(R.id.friendInGroupList);
            listViewBuddy.setVisibility(View.VISIBLE);
            listViewBuddy.setAdapters(adapter1,adapter2);
            listViewBuddy.setOnItemClickListener(new ListBuddiesLayout.OnBuddyItemClickListener() {
                @Override
                public void onBuddyItemClicked(AdapterView<?> adapterView, View view, int i, int i2, long l) {
                    Person friend;
                    if (i==0){
                        friend = friends1.get(i2);
                    }
                    else{
                        friend = friends2.get(i2);
                    }
                    Intent intent = new Intent(ActivityGroupInformation.this, ActivityFriendBlock.class);
                    intent.putExtra("data",friend);

                    startActivity(intent);
                    listViewBuddy.setSpeed(0);
                    overridePendingTransition(R.animator.push_right, R.animator.push_left);
                }
            });
        }
        else{
            adapter = new GroupAdapter(this, R.layout.friends_in_group_simple, friendsInGroup);
            listView = (ListView) findViewById(R.id.friendInGroupListSimple);
            listView.setVisibility(View.VISIBLE);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Person friend = friendsInGroup.get(position);
                    Intent intent = new Intent(ActivityGroupInformation.this, ActivityFriendBlock.class);
                    intent.putExtra("data",friend);
                    intent.putExtra("actualuser",userData);
                    startActivity(intent);
                    overridePendingTransition(R.animator.push_right, R.animator.push_left);
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (listViewBuddy!=null){
            listViewBuddy.setSpeed(ListBuddiesLayout.DEFAULT_SPEED);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.animator.push_left_inverted, R.animator.push_right_inverted);
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

    class Adapter extends CircularLoopAdapter{
        private Context context;
        private List<Person> objects;

        public Adapter(Context context, List<Person> objects) {
            this.objects = objects;
            this.context = context;
        }

        @Override
        protected int getCircularCount() {
            return objects.size();
        }

        @Override
        public Person getItem(int position) {
            return objects.get(getCircularPosition(position));
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            View rowView = inflater.inflate(R.layout.friends_in_group, null, true);
            ImageView avatar = (ImageView) rowView.findViewById(R.id.avatar_friend_in_group);
            avatar.setScaleType(ImageView.ScaleType.CENTER_CROP);
            avatar.setImageBitmap(loadImage(ActivityGroupInformation.this, getItem(position).getId()));
            return rowView;
        }
    }

    class GroupAdapter extends ArrayAdapter<Person> {
        private List<Person> objects;
        private int resource;
        private Context context;

        public GroupAdapter(Context context, int resource, List<Person> objects) {
            super(context, resource, objects);
            this.objects = objects;
            this.resource = resource;
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            View rowView = inflater.inflate(resource, null, true);
            ImageView avatar = (ImageView) rowView.findViewById(R.id.avatar_friend_in_group);
            TextView text = (TextView) rowView.findViewById(R.id.name_group);
            avatar.setImageBitmap(loadImage(ActivityGroupInformation.this, objects.get(position).getId()));
            text.setText(objects.get(position).getName());
            return rowView;
        }
    }
}

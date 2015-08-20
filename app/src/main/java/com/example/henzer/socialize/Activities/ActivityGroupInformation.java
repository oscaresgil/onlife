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

import com.example.henzer.socialize.Adapters.AdapterGroup;
import com.example.henzer.socialize.BlockActivity.ActivityFriendBlock;
import com.example.henzer.socialize.Models.ModelPerson;
import com.example.henzer.socialize.R;
import com.jpardogo.listbuddies.lib.adapters.CircularLoopAdapter;
import com.jpardogo.listbuddies.lib.views.ListBuddiesLayout;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrPosition;

import java.util.List;

import static com.example.henzer.socialize.Controller.StaticMethods.loadImage;

public class ActivityGroupInformation extends ActionBarActivity {
    private ListBuddiesLayout listViewBuddy;
    private ListView listView;

    private ModelPerson userData;
    private AdapterGroup adapter;
    private List<ModelPerson> friendsInGroup;

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
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        setContentView(R.layout.activity_group_information);

        Intent i = getIntent();
        friendsInGroup = (List<ModelPerson>) i.getSerializableExtra("data");
        userData = (ModelPerson) i.getSerializableExtra("user");

        /*if (friendsInGroup.size()>=4){
            final List<ModelPerson> friends1 = new ArrayList<>();
            final List<ModelPerson> friends2 = new ArrayList<>();
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
            listViewBuddy = (ListBuddiesLayout) findViewById(R.id.ActivityGroupInformation_ListNonSimpleFriend);
            listViewBuddy.setVisibility(View.VISIBLE);
            listViewBuddy.setAdapters(adapter1,adapter2);
            listViewBuddy.setOnItemClickListener(new ListBuddiesLayout.OnBuddyItemClickListener() {
                @Override
                public void onBuddyItemClicked(AdapterView<?> adapterView, View view, int i, int i2, long l) {
                    ModelPerson friend;
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
        else{*/

        //adapter = new GroupAdapter(this, R.layout.layout_friends_in_group_simple, friendsInGroup);
        /*listView = (ListView) findViewById(R.id.ActivityGroupInformation_ListSimpleFriend);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ModelPerson friend = friendsInGroup.get(position);
                Intent intent = new Intent(ActivityGroupInformation.this, ActivityFriendBlock.class);
                intent.putExtra("data",friend);
                intent.putExtra("actualuser",userData);
                startActivity(intent);
                overridePendingTransition(R.animator.push_right, R.animator.push_left);
            }
        });*/
        //}
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
        private List<ModelPerson> objects;

        public Adapter(Context context, List<ModelPerson> objects) {
            this.objects = objects;
            this.context = context;
        }

        @Override
        protected int getCircularCount() {
            return objects.size();
        }

        @Override
        public ModelPerson getItem(int position) {
            return objects.get(getCircularPosition(position));
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            View rowView = inflater.inflate(R.layout.layout_friends_in_group, null, true);
            ImageView avatar = (ImageView) rowView.findViewById(R.id.LayoutFriendsInGroup_ImageViewContact);
            avatar.setScaleType(ImageView.ScaleType.CENTER_CROP);
            avatar.setImageBitmap(loadImage(ActivityGroupInformation.this, getItem(position).getId()));
            return rowView;
        }
    }
}

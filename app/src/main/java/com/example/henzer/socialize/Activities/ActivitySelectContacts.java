package com.example.henzer.socialize.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.example.henzer.socialize.Adapters.AdapterStickyTitle;
import com.example.henzer.socialize.Listeners.ListenerFlipCheckbox;
import com.example.henzer.socialize.Models.Person;
import com.example.henzer.socialize.Models.SessionData;
import com.example.henzer.socialize.R;
import com.example.henzer.socialize.Tasks.TaskFacebookFriendRequest;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.HttpMethod;
import com.melnykov.fab.FloatingActionButton;

import java.util.List;

import ca.barrenechea.widget.recyclerview.decoration.DividerDecoration;
import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderDecoration;

public class ActivitySelectContacts extends Activity {

    private final String TAG = "ActivitySelectContacts";
    private StickyHeaderDecoration decor;
    private RecyclerView mList;
    private SessionData sessionData;
    private List<Person> friends;
    private List<Person> allFriends;
    private Animation animation1,animation2;
    private ListenerFlipCheckbox listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contact);

        sessionData = (SessionData)getIntent().getExtras().getSerializable("data");
        friends = sessionData.getFriends();

        mList = (RecyclerView) findViewById(R.id.ActivitySelectContact_RecyclerViewList);
        mList.setHasFixedSize(true);
        mList.setLayoutManager(new LinearLayoutManager(this));
        mList.addItemDecoration(new DividerDecoration(this));

        mList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView = (TextView) v;
                Toast.makeText(ActivitySelectContacts.this,textView.getText().toString(),Toast.LENGTH_SHORT).show();
            }
        });

        listener = new ListenerFlipCheckbox(ActivitySelectContacts.this);
        animation1 = AnimationUtils.loadAnimation(this, R.anim.flip_left_out);
        animation1.setAnimationListener(listener);
        animation2 = AnimationUtils.loadAnimation(this,R.anim.flip_left_in);
        animation2.setAnimationListener(listener);
        listener.setAnimation1(animation1);
        listener.setAnimation2(animation2);

        FloatingActionButton chooseContact = (FloatingActionButton) findViewById(R.id.ActivitySelectContact_ButtonOk);
        chooseContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        chooseContact.attachToRecyclerView(mList);
        Bundle params = new Bundle();
        params.putString("fields","id,name");
        new GraphRequest(AccessToken.getCurrentAccessToken(),"/me/friends",params, HttpMethod.GET, new TaskFacebookFriendRequest(ActivitySelectContacts.this,TAG)).executeAsync();
    }

    public void setAdapterAndDecor() {
        AdapterStickyTitle adapter = new AdapterStickyTitle(this,mList,allFriends,listener,animation1);
        decor = new StickyHeaderDecoration(adapter);

        mList.setAdapter(adapter);
        mList.addItemDecoration(decor, 1);
    }

    public void setAllFriends(List<Person> allFriends) {
        this.allFriends = allFriends;
        for (Person f: this.allFriends){
            if (isSelected(f.getId())){
                f.setHomeSelected(true);
            }
            else{
                f.setHomeSelected(false);
            }
        }
        Log.i("AllFriends", allFriends.toString());
    }

    public boolean isSelected(String id){
        for (Person f: friends){
            if (f.getId().equals(id)){
                return f.isHomeSelected();
            }
        }
        return false;
    }
}

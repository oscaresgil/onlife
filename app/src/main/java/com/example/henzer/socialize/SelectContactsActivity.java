package com.example.henzer.socialize;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.example.henzer.socialize.Adapters.FlipListener;
import com.example.henzer.socialize.Adapters.StickyTitleAdapter;
import com.example.henzer.socialize.Models.Person;
import com.example.henzer.socialize.Models.SessionData;
import com.melnykov.fab.FloatingActionButton;

import java.util.List;

import ca.barrenechea.widget.recyclerview.decoration.DividerDecoration;
import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderDecoration;

/**
 * Created by Boris on 6/19/2015.
 */
public class SelectContactsActivity extends Activity {

    private StickyHeaderDecoration decor;
    private RecyclerView mList;
    private SessionData sessionData;
    private List<Person> friends;
    private Animation animation1,animation2;
    private FlipListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_recycler_view_title);

        sessionData = (SessionData)getIntent().getExtras().getSerializable("data");
        friends = sessionData.getFriends();

        mList = (RecyclerView) findViewById(R.id.list);
        mList.setHasFixedSize(true);
        mList.setLayoutManager(new LinearLayoutManager(this));
        mList.addItemDecoration(new DividerDecoration(this));

        mList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView = (TextView) v;
                Toast.makeText(SelectContactsActivity.this,textView.getText().toString(),Toast.LENGTH_SHORT).show();
            }
        });

        listener = new FlipListener(SelectContactsActivity.this);
        animation1 = AnimationUtils.loadAnimation(this, R.anim.flip_left_out);
        animation1.setAnimationListener(listener);
        animation2 = AnimationUtils.loadAnimation(this,R.anim.flip_left_in);
        animation2.setAnimationListener(listener);
        listener.setAnimation1(animation1);
        listener.setAnimation2(animation2);

        FloatingActionButton chooseContact = (FloatingActionButton) findViewById(R.id.okButton);
        chooseContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        chooseContact.attachToRecyclerView(mList);
        setAdapterAndDecor(mList);
    }

    protected void setAdapterAndDecor(RecyclerView list) {
        StickyTitleAdapter adapter = new StickyTitleAdapter(this,list,friends,listener,animation1);
        decor = new StickyHeaderDecoration(adapter);

        list.setAdapter(adapter);
        list.addItemDecoration(decor, 1);
    }

}

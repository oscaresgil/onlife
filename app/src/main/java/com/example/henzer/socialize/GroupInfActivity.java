package com.example.henzer.socialize;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Boris on 02/05/2015.
 */
public class GroupInfActivity extends ActionBarActivity {
    private String nameGroup;
    private GroupAdapter adapter;
    private List<UserData> friendsInGroup;
    private NumberPicker minPicker;
    private NumberPicker secPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.group_information);

        Intent i = getIntent();
        nameGroup = i.getStringExtra("name");
        getSupportActionBar().setTitle(nameGroup);
        friendsInGroup = ((SessionData) i.getSerializableExtra("data")).getFriends();

        adapter = new GroupAdapter(this, R.layout.friends_in_group, friendsInGroup);
        ListView listView = (ListView) findViewById(R.id.friendInGroupList);
        listView.setAdapter(adapter);

        minPicker = (NumberPicker) findViewById(R.id.timeMinBlock);
        secPicker = (NumberPicker) findViewById(R.id.timeSecBlock);
        minPicker.setMinValue(1);
        minPicker.setMaxValue(3);
        secPicker.setMinValue(0);
        secPicker.setMaxValue(9);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    class GroupAdapter extends ArrayAdapter<UserData> {
        List<UserData> objects;

        public GroupAdapter(Context context, int resource, List<UserData> objects) {
            super(context, resource, objects);
            this.objects = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View rowView = inflater.inflate(R.layout.friends_in_group, null, true);
            TextView text = (TextView) rowView.findViewById(R.id.name_group);
            text.setText(objects.get(position).getName());
            return rowView;
        }
    }
}

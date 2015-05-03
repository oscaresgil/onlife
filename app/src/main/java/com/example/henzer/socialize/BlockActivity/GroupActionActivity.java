package com.example.henzer.socialize.BlockActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.NumberPicker;

import com.example.henzer.socialize.GroupInformationActivity;
import com.example.henzer.socialize.Models.Person;
import com.example.henzer.socialize.R;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Boris on 02/05/2015.
 */
public class GroupActionActivity extends ActionBarActivity {
    private String nameGroup;
    private List<Person> friendsInGroup;
    private NumberPicker minPicker;
    private NumberPicker secPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.group_action);

        Intent i = getIntent();
        nameGroup = i.getStringExtra("name");
        friendsInGroup = (List<Person>) i.getSerializableExtra("data");
        getSupportActionBar().setTitle(nameGroup);

        minPicker = (NumberPicker) findViewById(R.id.timeMinBlock);
        secPicker = (NumberPicker) findViewById(R.id.timeSecBlock);
        minPicker.setMinValue(1);
        minPicker.setMaxValue(3);
        secPicker.setMinValue(0);
        secPicker.setMaxValue(9);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_in_group, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.information_button){
            Intent intent = new Intent(this,GroupInformationActivity.class);
            intent.putExtra("data",(Serializable)friendsInGroup);
            startActivity(intent);
        }
        else if(i == R.id.delete_group){

        }
        else {
            finish();
        }
        return true;
    }
}

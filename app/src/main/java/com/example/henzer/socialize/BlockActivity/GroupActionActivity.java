package com.example.henzer.socialize.BlockActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.NumberPicker;

import com.example.henzer.socialize.Controller.SendNotification;
import com.example.henzer.socialize.GroupInformationActivity;
import com.example.henzer.socialize.GroupsFragment;
import com.example.henzer.socialize.Models.Group;
import com.example.henzer.socialize.Models.Person;
import com.example.henzer.socialize.R;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Boris on 02/05/2015.
 */
public class GroupActionActivity extends ActionBarActivity {
    public static final String TAG = "GroupActionActivity";
    private String nameGroup;
    private Group group;
    private List<Person> friendsInGroup;
    private NumberPicker minPicker;
    private NumberPicker secPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.orange_light)));
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_48dp);
        setContentView(R.layout.group_action);

        Intent i = getIntent();
        nameGroup = i.getStringExtra("name");
        group = (Group) i.getSerializableExtra("data");
        friendsInGroup = group.getFriendsInGroup();
        actionBar.setTitle((Html.fromHtml("<b><font color=\"#000000\">" + nameGroup + "</font></b>")));

        minPicker = (NumberPicker) findViewById(R.id.timeMinBlock);
        secPicker = (NumberPicker) findViewById(R.id.timeSecBlock);
        minPicker.setMinValue(1); minPicker.setMaxValue(3);
        secPicker.setMinValue(0); secPicker.setMaxValue(9);
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
            GroupsFragment.removeGroup(group);
            finish();
        }
        else {
            finish();
        }
        return true;
    }
    public void bloquear(View view){
        SendNotification gcm = new SendNotification(this, "Enjoy your life. \nLeave the phone", "5 min");
        Log.e(TAG, "Bloquear a: " + friendsInGroup.toString());
        gcm.execute(friendsInGroup.toArray(new Person[friendsInGroup.size()]));
    }

}

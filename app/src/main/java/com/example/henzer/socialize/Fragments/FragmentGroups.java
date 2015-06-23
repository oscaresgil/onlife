package com.example.henzer.socialize.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.henzer.socialize.Activities.ActivityGroupCreateInformation;
import com.example.henzer.socialize.Activities.ActivityMain;
import com.example.henzer.socialize.BlockActivity.ActivityGroupBlock;
import com.example.henzer.socialize.Models.Group;
import com.example.henzer.socialize.Models.Person;
import com.example.henzer.socialize.Models.SessionData;
import com.example.henzer.socialize.R;
import com.melnykov.fab.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

import static com.example.henzer.socialize.Controller.StaticMethods.loadImage;

public class FragmentGroups extends Fragment {
    public static final String TAG = "GroupsFragment";
    private SessionData sessionData;
    private GroupAdapter adapter;
    private ListView list;
    private FloatingActionButton addGroupButton;
    private static List<Group> groups;
    private static SharedPreferences preferences;

    public static FragmentGroups newInstance(Bundle arguments){
        FragmentGroups myfragment = new FragmentGroups();
        if(arguments !=null){
            myfragment.setArguments(arguments);
        }
        return myfragment;
    }

    public FragmentGroups(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        preferences = getActivity().getSharedPreferences(ActivityMain.MyPREFERENCES, Context.MODE_PRIVATE);
        Log.e(TAG, "OnCreateView");
        setHasOptionsMenu(true);
        View v = inflater.inflate(R.layout.groups_view, container, false);
        sessionData = (SessionData) getArguments().getSerializable("data");

        groups = sessionData.getGroups();
        adapter = new GroupAdapter(getActivity(), R.layout.groups, groups);
        list = (ListView)v.findViewById(R.id.list_group);
        list.setAdapter(adapter);
        addGroupButton = (FloatingActionButton) v.findViewById(R.id.addNewGroupButton);
        addGroupButton.attachToListView(list);
        addGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addGroup(v);
            }
        });
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter = new GroupAdapter(getActivity(), R.layout.groups, groups);
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        list.setLongClickable(true);
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final Group actualGroup = groups.get(position);

                new MaterialDialog.Builder(getActivity())
                        .title(R.string.delete)
                        .content(R.string.really_delete)
                        .positiveText(R.string.yes)
                        .positiveColorRes(R.color.orange_light)
                        .negativeText(R.string.no)
                        .negativeColorRes(R.color.red)
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                removeGroup(actualGroup);
                                adapter.notifyDataSetChanged();
                                dialog.dismiss();
                                dialog.cancel();

                                new MaterialDialog.Builder(getActivity())
                                        .title(getResources().getString(R.string.group) +" "+ actualGroup.getName() + " "+getResources().getString(R.string.deleted)+"!")
                                        .positiveText(R.string.ok)
                                        .positiveColorRes(R.color.orange_light)
                                        .show();
                            }
                        }).show();

                return true;
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Group group = groups.get(position);
                Intent intent = new Intent(getActivity(), ActivityGroupBlock.class);
                intent.putExtra("data", (Serializable) group);
                intent.putExtra("name", group.getName());
                intent.putExtra("user", sessionData.getUser());
                startActivity(intent);
                getActivity().overridePendingTransition(R.animator.push_right, R.animator.push_left);
            }
        });
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e(TAG, "OnViewCreated");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.findItem(R.id.searchContact).setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void addGroup(View view){
        Intent i = new Intent(getActivity(), ActivityGroupCreateInformation.class);
        i.putExtra("data", sessionData);
        startActivity(i);
        getActivity().overridePendingTransition(R.animator.push_right, R.animator.push_left);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    class GroupAdapter extends ArrayAdapter<Group> {
        List<Group> objects;
        public GroupAdapter(Context context, int resource, List<Group> objects) {
            super(context, resource, objects);
            this.objects = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View rowView = inflater.inflate(R.layout.groups, null, true);
            TextView text = (TextView) rowView.findViewById(R.id.LayoutFriendsInGroup_TextViewName);
            ImageView image = (ImageView) rowView.findViewById(R.id.image_group);
            Bitmap b = loadImage(getContext(), objects.get(position).getName());
            b = Bitmap.createBitmap(b,(b.getWidth()/2)-150,(b.getHeight()/2)-150,300,300);
            image.setImageBitmap(b);
            image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            text.setText(objects.get(position).getName());
            return rowView;
        }
    }
    public static void addNewGroup(Group group){
        groups.add(group);
    }
    public static void removeGroup(Group group){
        for (int i=0; i<groups.size(); i++){
            if (groups.get(i).getId()==group.getId()){
                groups.remove(i);
            }
        }
        try {
            saveInSession(groups);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public static boolean alreadyGroup(String name){
        for (Group g:groups){
            if (g.getName().equals(name)){
                return true;
            }
        }
        return false;
    }
    public static void saveInSession(List<Group> groups) throws JSONException {
        SharedPreferences.Editor editor = preferences.edit();
        JSONObject mySession = new JSONObject(preferences.getString("session", "{}"));
        Log.e(TAG, mySession.toString());

        JSONArray myGroups = new JSONArray();

        for(Group group: groups) {
            JSONObject obj = new JSONObject();
            obj.put("id", group.getId());
            obj.put("name", group.getName());
            obj.put("photo", group.getNameImage());
            obj.put("limit", group.getLimit());
            obj.put("state", group.getState());

            JSONArray arr = new JSONArray();
            for (Person p : group.getFriendsInGroup()) {
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
        }
        mySession.put("groups", myGroups);

        Log.e(TAG, mySession.toString());
        editor.putString("session", mySession.toString());
        editor.commit();
    }

}

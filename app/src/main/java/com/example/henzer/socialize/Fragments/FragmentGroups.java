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
import com.example.henzer.socialize.Models.ModelGroup;
import com.example.henzer.socialize.Models.ModelPerson;
import com.example.henzer.socialize.Models.ModelSessionData;
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
    private ModelSessionData modelSessionData;
    private GroupAdapter adapter;
    private ListView list;
    private FloatingActionButton addGroupButton;
    private static List<ModelGroup> modelGroups;
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
        View v = inflater.inflate(R.layout.fragment_groups, container, false);
        modelSessionData = (ModelSessionData) getArguments().getSerializable("data");

        modelGroups = modelSessionData.getModelGroups();
        adapter = new GroupAdapter(getActivity(), R.layout.layout_groups, modelGroups);
        list = (ListView)v.findViewById(R.id.FragmentGroups_ListGroup);
        list.setAdapter(adapter);
        addGroupButton = (FloatingActionButton) v.findViewById(R.id.FragmentGroup_ButtonAddGroup);
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
        adapter = new GroupAdapter(getActivity(), R.layout.layout_groups, modelGroups);
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        list.setLongClickable(true);
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final ModelGroup actualModelGroup = modelGroups.get(position);

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
                                removeGroup(actualModelGroup);
                                adapter.notifyDataSetChanged();
                                dialog.dismiss();
                                dialog.cancel();

                                new MaterialDialog.Builder(getActivity())
                                        .title(getResources().getString(R.string.group) +" "+ actualModelGroup.getName() + " "+getResources().getString(R.string.deleted)+"!")
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
                ModelGroup modelGroup = modelGroups.get(position);
                Intent intent = new Intent(getActivity(), ActivityGroupBlock.class);
                intent.putExtra("data", (Serializable) modelGroup);
                intent.putExtra("name", modelGroup.getName());
                intent.putExtra("user", modelSessionData.getUser());
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
        i.putExtra("data", modelSessionData);
        startActivity(i);
        getActivity().overridePendingTransition(R.animator.push_right, R.animator.push_left);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    class GroupAdapter extends ArrayAdapter<ModelGroup> {
        List<ModelGroup> objects;
        public GroupAdapter(Context context, int resource, List<ModelGroup> objects) {
            super(context, resource, objects);
            this.objects = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View rowView = inflater.inflate(R.layout.layout_groups, null, true);
            TextView text = (TextView) rowView.findViewById(R.id.LayoutGroups_TextViewGroupName);
            ImageView image = (ImageView) rowView.findViewById(R.id.LayoutGroups_CircleImageViewGroup);
            Bitmap b = loadImage(getContext(), objects.get(position).getName());
            b = Bitmap.createBitmap(b,(b.getWidth()/2)-150,(b.getHeight()/2)-150,300,300);
            image.setImageBitmap(b);
            image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            text.setText(objects.get(position).getName());
            return rowView;
        }
    }
    public static void addNewGroup(ModelGroup modelGroup){
        modelGroups.add(modelGroup);
    }
    public static void removeGroup(ModelGroup modelGroup){
        for (int i=0; i< modelGroups.size(); i++){
            if (modelGroups.get(i).getId()== modelGroup.getId()){
                modelGroups.remove(i);
            }
        }
        try {
            saveInSession(modelGroups);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public static boolean alreadyGroup(String name){
        for (ModelGroup g: modelGroups){
            if (g.getName().equals(name)){
                return true;
            }
        }
        return false;
    }
    public static void saveInSession(List<ModelGroup> modelGroups) throws JSONException {
        SharedPreferences.Editor editor = preferences.edit();
        JSONObject mySession = new JSONObject(preferences.getString("session", "{}"));
        Log.e(TAG, mySession.toString());

        JSONArray myGroups = new JSONArray();

        for(ModelGroup modelGroup : modelGroups) {
            JSONObject obj = new JSONObject();
            obj.put("id", modelGroup.getId());
            obj.put("name", modelGroup.getName());
            obj.put("photo", modelGroup.getNameImage());
            obj.put("limit", modelGroup.getLimit());
            obj.put("state", modelGroup.getState());

            JSONArray arr = new JSONArray();
            for (ModelPerson p : modelGroup.getFriendsInGroup()) {
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
        mySession.put("activity_groups", myGroups);

        Log.e(TAG, mySession.toString());
        editor.putString("session", mySession.toString());
        editor.commit();
    }

}

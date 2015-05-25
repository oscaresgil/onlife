package com.example.henzer.socialize;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.example.henzer.socialize.BlockActivity.GroupActionActivity;
import com.example.henzer.socialize.Models.Group;
import com.example.henzer.socialize.Models.SessionData;
import com.melnykov.fab.FloatingActionButton;

import java.io.File;
import java.io.Serializable;
import java.util.List;

/**
 * Created by Boris on 01/05/2015.
 */
public class GroupsFragment extends Fragment {
    public static final String TAG = "GroupsFragment";
    private SessionData sessionData;
    private GroupAdapter adapter;
    private ListView list;
    private FloatingActionButton addGroupButton;
    private static List<Group> groups;

    public static GroupsFragment newInstance(Bundle arguments){
        GroupsFragment myfragment = new GroupsFragment();
        if(arguments !=null){
            myfragment.setArguments(arguments);
        }
        return myfragment;
    }

    public GroupsFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
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
                                        .title("Group " + actualGroup.getName() + " deleted!")
                                        .positiveText("OK")
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
                Intent intent = new Intent(getActivity(), GroupActionActivity.class);
                intent.putExtra("data", (Serializable) group);
                intent.putExtra("name", group.getName());
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
        //menu.findItem(R.id.addGroup).setVisible(true);
        menu.findItem(R.id.searchContact).setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void addGroup(View view){
        Intent i = new Intent(getActivity(), GroupCreateInfActivity.class);
        i.putExtra("data", sessionData);
        startActivity(i);
        getActivity().overridePendingTransition(R.animator.push_right, R.animator.push_left);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
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
            TextView text = (TextView) rowView.findViewById(R.id.name_group);
            ImageView image = (ImageView) rowView.findViewById(R.id.image_group);
            image.setImageBitmap(cargarImagen(getContext(),objects.get(position).getName()));
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
    }
}

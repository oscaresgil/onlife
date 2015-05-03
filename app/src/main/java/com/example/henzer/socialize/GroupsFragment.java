package com.example.henzer.socialize;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Boris on 01/05/2015.
 */
public class GroupsFragment extends ListFragment {
    public static final String TAG = "GroupsFragment";
    private SessionData sessionData;
    private GroupAdapter adapter;
    private List<UserDataYaNoSeUsa> groups;

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
        setHasOptionsMenu(true);
        View v = inflater.inflate(R.layout.groups_view, container, false);
        sessionData = (SessionData)getArguments().getSerializable("data");

        groups = new ArrayList<>();
        groups.add(new UserDataYaNoSeUsa("12345", "Nombre Prueba", null));
        groups.add(new UserDataYaNoSeUsa("12335", "Nombre Prueba 1", null));
        groups.add(new UserDataYaNoSeUsa("12325", "Nombre Prueba 2", null));
        groups.add(new UserDataYaNoSeUsa("12315", "Nombre Prueba 3", null));
        groups.add(new UserDataYaNoSeUsa("12315", "Nombre Prueba 4", null));
        groups.add(new UserDataYaNoSeUsa("12315", "Nombre Prueba 5", null));
        groups.add(new UserDataYaNoSeUsa("12315", "Nombre Prueba 6", null));
        groups.add(new UserDataYaNoSeUsa("12315", "Nombre Prueba 7", null));


        adapter = new GroupAdapter(getActivity(), R.layout.groups, groups);
        setListAdapter(adapter);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        UserDataYaNoSeUsa group = groups.get(position);
        Toast.makeText(getActivity(), "CLICKED ON "+groups.get(position).getName(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getActivity(),GroupInfActivity.class);
        intent.putExtra("data",sessionData);
        intent.putExtra("name",group.getName());
        startActivity(intent);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.findItem(R.id.addGroup).setVisible(true);
        menu.findItem(R.id.refreshContacts).setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i = new Intent(getActivity(),GroupCreateInfActivity.class);
        i.putExtra("data",sessionData);
        startActivity(i);
        return super.onOptionsItemSelected(item);
    }

    class GroupAdapter extends ArrayAdapter<UserDataYaNoSeUsa> {
        List<UserDataYaNoSeUsa> objects;
        public GroupAdapter(Context context, int resource, List<UserDataYaNoSeUsa> objects) {
            super(context, resource, objects);
            this.objects = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View rowView = inflater.inflate(R.layout.groups, null, true);
            TextView text = (TextView) rowView.findViewById(R.id.name_group);
            ImageView image = (ImageView) rowView.findViewById(R.id.image_group);

            text.setText(objects.get(position).getName());

            return rowView;
        }
    }
}

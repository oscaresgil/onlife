package com.example.henzer.socialize.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.henzer.socialize.Activities.ActivityGroupCreateInformation;
import com.example.henzer.socialize.Activities.ActivityHome;
import com.example.henzer.socialize.Adapters.AdapterGroup;
import com.example.henzer.socialize.BlockActivity.ActivityGroupBlock;
import com.example.henzer.socialize.Models.ModelGroup;
import com.example.henzer.socialize.Models.ModelSessionData;
import com.example.henzer.socialize.R;
import com.melnykov.fab.FloatingActionButton;

import java.util.List;

import static com.example.henzer.socialize.Controller.StaticMethods.animationStart;
import static com.example.henzer.socialize.Controller.StaticMethods.getModelGroupIndex;
import static com.example.henzer.socialize.Controller.StaticMethods.removeGroup;

public class FragmentGroups extends Fragment {
    public static final String TAG = "GroupsFragment";
    public static final int CREATE_GROUP_ACTIVITY_ID = 1;
    public static final int GROUP_BLOCK_ACTIVITY_ID = 3;

    private AdapterGroup adapter;
    private ListView list;
    private FloatingActionButton addGroupButton;
    private List<ModelGroup> modelGroups;

    public FragmentGroups(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.e(TAG, "OnCreateView");
        setHasOptionsMenu(true);
        View v = inflater.inflate(R.layout.fragment_groups, container, false);
        //modelSessionData = (ModelSessionData) getArguments().getSerializable("data");

        modelGroups = ModelSessionData.getInstance().getModelGroups();
        //adapter = new AdapterGroup(getActivity(), R.layout.layout_groups, modelGroups);
        adapter = ((ActivityHome)getActivity()).getAdapterGroup();
        list = (ListView)v.findViewById(R.id.FragmentGroups_ListGroup);
        list.setAdapter(adapter);
        list.setLongClickable(true);
        list.setOnItemLongClickListener(new LongItemClickListener());
        list.setOnItemClickListener(new ItemClickListener());

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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.findItem(R.id.searchContact).setVisible(false);
        /*if (FragmentContacts.isIsSearchOpened()){
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getWindow().getCurrentFocus().getWindowToken(), 0);
        }

        android.support.v7.app.ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();

        actionBar.setDisplayShowCustomEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);*/
        FragmentContacts.setIsSearchOpened(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == CREATE_GROUP_ACTIVITY_ID){
                ModelGroup newG = (ModelGroup) data.getSerializableExtra("new_group");
                adapter.add(newG);
            }else if(requestCode == GROUP_BLOCK_ACTIVITY_ID){
                ModelGroup delG = (ModelGroup) data.getSerializableExtra("delete_group");
                int pos = getModelGroupIndex(delG,modelGroups);
                adapter.remove(adapter.getItem(pos));
            }
        }
        adapter.notifyDataSetChanged();
    }

    public void addGroup(View view){
        Intent i = new Intent(getActivity(), ActivityGroupCreateInformation.class);
        startActivityForResult(i, CREATE_GROUP_ACTIVITY_ID);
        animationStart(getActivity());
    }

    class LongItemClickListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            final ModelGroup actualModelGroup = adapter.getItem(position);

            new MaterialDialog.Builder(getActivity())
                    .title(getResources().getString(R.string.delete)+" "+actualModelGroup.getName())
                    .content(R.string.really_delete)
                    .positiveText(R.string.yes)
                    .positiveColorRes(R.color.orange_light)
                    .negativeText(R.string.no)
                    .negativeColorRes(R.color.red)
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            removeGroup(actualModelGroup);
                            adapter.remove(actualModelGroup);
                            adapter.notifyDataSetChanged();
                            dialog.dismiss();
                            dialog.cancel();
                        }
                    }).show();

            return true;
        }
    }
    class ItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ModelGroup modelGroup = adapter.getItem(position);
            Intent intent = new Intent(getActivity(), ActivityGroupBlock.class);
            intent.putExtra("model_group", modelGroup);
            startActivityForResult(intent,GROUP_BLOCK_ACTIVITY_ID);
            getActivity().overridePendingTransition(R.animator.push_right, R.animator.push_left);
        }
    }

}

package com.example.henzer.socialize.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.henzer.socialize.Activities.ActivityGroupCreateInformation;
import com.example.henzer.socialize.Activities.ActivityMain;
import com.example.henzer.socialize.Adapters.AdapterGroup;
import com.example.henzer.socialize.BlockActivity.ActivityGroupBlock;
import com.example.henzer.socialize.Models.ModelGroup;
import com.example.henzer.socialize.Models.ModelPerson;
import com.example.henzer.socialize.Models.ModelSessionData;
import com.example.henzer.socialize.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.melnykov.fab.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.example.henzer.socialize.Controller.StaticMethods.hideSoftKeyboard;
import static com.example.henzer.socialize.Controller.StaticMethods.removeGroup;

public class FragmentGroups extends Fragment {
    public static final String TAG = "GroupsFragment";
    //private ModelSessionData modelSessionData;
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
        adapter = new AdapterGroup(getActivity(), R.layout.layout_groups, modelGroups);
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

        final SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        modelGroups = gson.fromJson(sharedPreferences.getString("groups", ""), (new TypeToken<ArrayList<ModelGroup>>(){}.getType()));
        ModelSessionData.getInstance().setModelGroups(modelGroups);

        adapter = new AdapterGroup(getActivity(), R.layout.layout_groups, modelGroups);
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        list.setLongClickable(true);
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final ModelGroup actualModelGroup = modelGroups.get(position);

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
                                //removeGroup(actualModelGroup,sharedPreferences,modelSessionData);
                                removeGroup(actualModelGroup);
                                adapter.notifyDataSetChanged();
                                dialog.dismiss();
                                dialog.cancel();
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
                intent.putExtra("modelgroup", modelGroup);
                /*intent.putExtra("name", modelGroup.getName());
                intent.putExtra("user", modelSessionData.getUser());*/
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
        if (FragmentContacts.isIsSearchOpened()){
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getWindow().getCurrentFocus().getWindowToken(), 0);
        }

        android.support.v7.app.ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();

        actionBar.setDisplayShowCustomEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);
        FragmentContacts.setIsSearchOpened(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void addGroup(View view){
        Intent i = new Intent(getActivity(), ActivityGroupCreateInformation.class);
        startActivity(i);
        getActivity().overridePendingTransition(R.animator.push_right, R.animator.push_left);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}

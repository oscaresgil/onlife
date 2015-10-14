package com.objective4.app.onlife.Fragments.Social;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.objective4.app.onlife.Activities.ActivityGroupCreateInformation;
import com.objective4.app.onlife.Activities.ActivityHome;
import com.objective4.app.onlife.Adapters.AdapterBaseElements;
import com.objective4.app.onlife.BlockActivity.ActivityGroupBlock;
import com.objective4.app.onlife.Models.ModelGroup;
import com.objective4.app.onlife.Models.ModelSessionData;
import com.objective4.app.onlife.R;

import java.util.List;

import static com.objective4.app.onlife.Controller.StaticMethods.animationStart;

public class FragmentGroups extends Fragment {
    public static final int CREATE_GROUP_ACTIVITY_ID = 1;
    public static final int GROUP_BLOCK_ACTIVITY_ID = 3;

    private AdapterBaseElements adapter;
    private List<ModelGroup> modelGroups;

    public FragmentGroups(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);
        View v = inflater.inflate(R.layout.fragment_groups, container, false);

        modelGroups = ModelSessionData.getInstance().getModelGroups();
        adapter = new AdapterBaseElements<>(getActivity(), ModelSessionData.getInstance().getModelGroups(), FragmentGroups.class, ActivityGroupBlock.class);
        ((ActivityHome)getActivity()).setAdapterGroup(adapter);

        RecyclerView list = (RecyclerView) v.findViewById(R.id.FragmentGroups_ListGroup);
        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(getActivity()));
        list.setAdapter(adapter);

        /*final FloatingActionButton addGroupButton = (FloatingActionButton) v.findViewById(R.id.FragmentGroup_ButtonAddGroup);
        addGroupButton.attachToRecyclerView(list);
        addGroupButton.attachToRecyclerView(list);
        addGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addGroup();
            }
        });*/
        return v;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i==R.id.addGroup){
            Intent intent = new Intent(getActivity(), ActivityGroupCreateInformation.class);
            getActivity().startActivityForResult(intent, CREATE_GROUP_ACTIVITY_ID);
            animationStart(getActivity());
        }

        return super.onOptionsItemSelected(item);
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
}

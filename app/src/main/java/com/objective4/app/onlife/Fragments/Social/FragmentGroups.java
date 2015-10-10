package com.objective4.app.onlife.Fragments.Social;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.melnykov.fab.FloatingActionButton;
import com.objective4.app.onlife.Activities.ActivityGroupCreateInformation;
import com.objective4.app.onlife.Activities.ActivityHome;
import com.objective4.app.onlife.Adapters.AdapterGroup;
import com.objective4.app.onlife.BlockActivity.ActivityGroupBlock;
import com.objective4.app.onlife.Models.ModelGroup;
import com.objective4.app.onlife.Models.ModelPerson;
import com.objective4.app.onlife.Models.ModelSessionData;
import com.objective4.app.onlife.R;
import com.objective4.app.onlife.Tasks.TaskSendNotification;

import java.util.List;

import static com.objective4.app.onlife.Controller.StaticMethods.activateDeviceAdmin;
import static com.objective4.app.onlife.Controller.StaticMethods.animationStart;
import static com.objective4.app.onlife.Controller.StaticMethods.checkDeviceAdmin;
import static com.objective4.app.onlife.Controller.StaticMethods.getModelGroupIndex;

public class FragmentGroups extends Fragment {
    public static final int CREATE_GROUP_ACTIVITY_ID = 1;
    public static final int GROUP_BLOCK_ACTIVITY_ID = 3;

    private AdapterGroup adapter;
    private List<ModelGroup> modelGroups;

    public FragmentGroups(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);
        View v = inflater.inflate(R.layout.fragment_groups, container, false);

        modelGroups = ModelSessionData.getInstance().getModelGroups();
        adapter = ((ActivityHome)getActivity()).getAdapterGroup();
        ListView list = (ListView) v.findViewById(R.id.FragmentGroups_ListGroup);
        list.setAdapter(adapter);
        list.setLongClickable(true);
        list.setOnItemLongClickListener(new LongItemClickListener());
        list.setOnItemClickListener(new ItemClickListener());

        FloatingActionButton addGroupButton = (FloatingActionButton) v.findViewById(R.id.FragmentGroup_ButtonAddGroup);
        addGroupButton.attachToListView(list);
        addGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addGroup();
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

    public void addGroup(){
        Intent i = new Intent(getActivity(), ActivityGroupCreateInformation.class);
        startActivityForResult(i, CREATE_GROUP_ACTIVITY_ID);
        animationStart(getActivity());
    }

    class LongItemClickListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            ModelGroup actualModelGroup = adapter.getItem(position);
            boolean activeDev = checkDeviceAdmin(getActivity());
            if (activeDev) {
                new TaskSendNotification(getActivity(), ModelSessionData.getInstance().getUser().getName(), "", "").execute(actualModelGroup.getFriendsInGroup().toArray(new ModelPerson[actualModelGroup.getFriendsInGroup().size()]));
            } else{
                activateDeviceAdmin(getActivity());
            }

            return true;
        }
    }
    class ItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ModelGroup modelGroup = adapter.getItem(position);
            Intent intent = new Intent(getActivity(), ActivityGroupBlock.class);
            intent.putExtra("data", modelGroup);
            startActivityForResult(intent,GROUP_BLOCK_ACTIVITY_ID);
            getActivity().overridePendingTransition(R.animator.push_right, R.animator.push_left);
        }
    }

}

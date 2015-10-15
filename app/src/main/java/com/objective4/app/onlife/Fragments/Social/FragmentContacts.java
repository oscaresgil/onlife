package com.objective4.app.onlife.Fragments.Social;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;


import com.kenny.snackbar.SnackBar;
import com.objective4.app.onlife.Activities.ActivityHome;
import com.objective4.app.onlife.Adapters.AdapterBaseElements;
import com.objective4.app.onlife.BlockActivity.ActivityFriendBlock;
import com.objective4.app.onlife.Models.ModelPerson;
import com.objective4.app.onlife.Models.ModelSessionData;
import com.objective4.app.onlife.R;
import com.objective4.app.onlife.Tasks.TaskRefresh;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.List;

import static com.objective4.app.onlife.Controller.StaticMethods.activateDeviceAdmin;
import static com.objective4.app.onlife.Controller.StaticMethods.getItemPosition;
import static com.objective4.app.onlife.Controller.StaticMethods.isNetworkAvailable;
import static com.objective4.app.onlife.Controller.StaticMethods.showSoftKeyboard;

public class FragmentContacts extends Fragment {
    private ModelPerson actualUser;
    private List<ModelPerson> friends;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView listView;
    private AdapterBaseElements adapter;

    private MenuItem mSearchAction;
    private MaterialEditText searchText;
    private List<ModelPerson> friendsFiltred;
    public static boolean isSearchOpened = false;
    private String mSearchQuery;
    private TextView addFriends;

    public FragmentContacts(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);
        View v = inflater.inflate (R.layout.fragment_contacts, container, false);
        actualUser = ModelSessionData.getInstance().getUser();
        friends = ModelSessionData.getInstance().getFriends();

        if(friends.isEmpty()){
            addFriends= (TextView)v.findViewById(R.id.addFriendsButton);
            addFriends.setVisibility(View.VISIBLE);
        }

        friendsFiltred = new ArrayList<>();

        adapter = new AdapterBaseElements<>(getActivity(), ModelSessionData.getInstance().getFriends(), FragmentContacts.class, ActivityFriendBlock.class);
        ((ActivityHome)getActivity()).setAdapterContact(adapter);

        listView = (RecyclerView) v.findViewById(R.id.FragmentContacts_ListView);
        listView.setHasFixedSize(true);
        listView.setLayoutManager(new LinearLayoutManager(getActivity()));
        listView.setAdapter(adapter);

        //listView.getItemAnimator().setSupportsChangeAnimations(false);

        getActivity().registerReceiver(broadcastReceiver, new IntentFilter("com.objective4.app.onlife.Fragments.Social.FragmentContacts"));

        return v;
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            String tag = extras.getString("tag");
            if ("update".equals(tag)) {
                String id = extras.getString("id");
                String state = extras.getString("state");

                List<ModelPerson> friendsT = ModelSessionData.getInstance().getFriends();
                if ("O".equals(state)) {
                    for (int i = 0; i < friendsT.size(); i++) {
                        ModelPerson p = friendsT.get(i);
                        if (id.equals(p.getId())){
                            ModelSessionData.getInstance().getFriends().remove(i);
                            adapter.notifyItemRemoved(i);
                            break;
                        }
                    }
                } else {
                    for (int i=0; i<friendsT.size(); i++) {
                        ModelPerson p = friendsT.get(i);
                        if (p.getId().equals(id)) {
                            p.setState(state);
                            adapter.notifyItemChanged(i);
                            break;
                        }
                    }
                }
            }else if("new_user".equals(tag)){
                ModelPerson newUser = (ModelPerson) extras.getSerializable("new_user");
                ModelSessionData.getInstance().getFriends().add(newUser);
                int pos = getItemPosition(ModelSessionData.getInstance().getFriends(),newUser.getId());
                adapter.notifyItemInserted(pos);
            } else if("no_device_admin".equals(tag)){
                activateDeviceAdmin(getActivity());
            }
        }
    };


    @Override
    public void onResume() {
        super.onResume();
        mSwipeRefreshLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.FragmentContacts_SwipeRefreshLayout);

        listView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                boolean enable = false;
                if (listView != null && listView.getChildCount() > 0) {

                    android.support.v7.app.ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
                    boolean firstItemVisible = actionBar.isShowing();
                    boolean topOfFirstItemVisible = listView.getChildAt(0).getTop() == 0;
                    enable = firstItemVisible && topOfFirstItemVisible;

                    addFriends= (TextView)getActivity().findViewById(R.id.addFriendsButton);
                    addFriends.setVisibility(View.INVISIBLE);
                }
                mSwipeRefreshLayout.setEnabled(enable);
            }

        });
        mSwipeRefreshLayout.setColorSchemeResources(R.color.orange_light, R.color.orange);
        mSwipeRefreshLayout.setSize(R.integer.fragment_contacts_size_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContact();
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mSwipeRefreshLayout!=null) {
            mSwipeRefreshLayout.setRefreshing(false);
            mSwipeRefreshLayout.destroyDrawingCache();
            mSwipeRefreshLayout.clearAnimation();
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        mSearchAction = menu.findItem(R.id.searchContact);
        menu.findItem(R.id.addGroup).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i==R.id.searchContact){
            handleMenuSearch();
            adapter.notifyDataSetChanged();
        }

        return super.onOptionsItemSelected(item);
    }

    public List<ModelPerson> performSearch(List<ModelPerson> actualFriends, String query){
        String[] queryByWords = query.toLowerCase().split("\\s+");
        List<ModelPerson> filtred = new ArrayList<>();
        for (ModelPerson actual: actualFriends){
            String content = (actual.getName()).toLowerCase();

            for (String word: queryByWords){
                int numberOfMatches = queryByWords.length;
                if (content.contains(word)){
                    numberOfMatches--;
                }
                else{
                    break;
                }

                if (numberOfMatches == 0){
                    filtred.add(actual);
                }
            }
        }
        return filtred;
    }

    public void handleMenuSearch(){
        android.support.v7.app.ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (isSearchOpened){
            listView.setAdapter(adapter);
            friendsFiltred.clear();

            assert actionBar != null;
            actionBar.setDisplayShowCustomEnabled(false);
            actionBar.setDisplayShowTitleEnabled(true);

            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getWindow().getCurrentFocus().getWindowToken(), 0);

            mSearchAction.setIcon(getResources().getDrawable(R.drawable.ic_search_black_24dp));
            isSearchOpened = false;
        }
        else{
            friendsFiltred.clear();
            friendsFiltred.addAll(friends);
            listView.setAdapter(new AdapterBaseElements<>(getActivity(), friendsFiltred, FragmentContacts.class, ActivityFriendBlock.class));
            assert actionBar != null;
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setCustomView(R.layout.layout_search_contact_bar);
            actionBar.setDisplayShowTitleEnabled(false);

            searchText = (MaterialEditText) actionBar.getCustomView().findViewById(R.id.LayoutSearchContactBar_EditTextSearch);
            searchText.setVisibility(View.VISIBLE);
            searchText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    mSearchQuery = searchText.getText().toString();
                    AdapterBaseElements adapterContact = (AdapterBaseElements) listView.getAdapter();
                    adapterContact.clear();
                    adapterContact.addAll(performSearch(friends, mSearchQuery));
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
            searchText.requestFocus();
            showSoftKeyboard(getActivity(),searchText);
            mSearchAction.setIcon(getResources().getDrawable(R.drawable.ic_close_black_24dp));
            isSearchOpened = true;
        }
    }

    public void refreshContact(){
        if (isNetworkAvailable(getActivity())) {
            new TaskRefresh(getActivity(),mSwipeRefreshLayout).execute(actualUser.getId());
        }else{
            mSwipeRefreshLayout.setRefreshing(false);
            SnackBar.show(getActivity(), R.string.no_connection, R.string.button_change_connection, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                }
            });
        }
    }

    public static void setIsSearchOpened(boolean isSearchOpened) {
        FragmentContacts.isSearchOpened = isSearchOpened;
    }


}
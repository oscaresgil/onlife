package com.objective4.app.onlife.Fragments.Social;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
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

import com.objective4.app.onlife.Activities.ActivityHome;
import com.objective4.app.onlife.Adapters.AdapterBaseElements;
import com.objective4.app.onlife.BlockActivity.ActivityFriendBlock;
import com.objective4.app.onlife.Models.ModelPerson;
import com.objective4.app.onlife.Models.ModelSessionData;
import com.objective4.app.onlife.R;
import com.objective4.app.onlife.Tasks.TaskRefresh;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.objective4.app.onlife.Controller.StaticMethods.getModelPersonIndex;
import static com.objective4.app.onlife.Controller.StaticMethods.isNetworkAvailable;
import static com.objective4.app.onlife.Controller.StaticMethods.makeSnackbar;
import static com.objective4.app.onlife.Controller.StaticMethods.setHashToList;
import static com.objective4.app.onlife.Controller.StaticMethods.showSoftKeyboard;

public class FragmentContacts extends Fragment {
    private ModelPerson actualUser;

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

        addFriends= (TextView)v.findViewById(R.id.addFriendsButton);
        if (!ModelSessionData.getInstance().getFriends().isEmpty()) addFriends.setVisibility(View.GONE);

        friendsFiltred = new ArrayList<>();

        adapter = new AdapterBaseElements<>(getActivity(), setHashToList(ModelSessionData.getInstance().getFriends()), FragmentContacts.class, ActivityFriendBlock.class);
        ((ActivityHome)getActivity()).setAdapterContact(adapter);

        listView = (RecyclerView) v.findViewById(R.id.FragmentContacts_ListView);
        listView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        listView.setLayoutManager(linearLayoutManager);
        listView.setAdapter(adapter);

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

            AdapterBaseElements adapter = ((ActivityHome) getActivity()).getAdapterContact();
            if ("update".equals(tag)) {
                String id = extras.getString("id");
                String state = extras.getString("state");
                if ("O".equals(state)) {
                    boolean b = ModelSessionData.getInstance().getFriends().containsKey(id);
                    if (b){
                        ModelSessionData.getInstance().getFriends().remove(id);
                        adapter.removeFriend(id);
                    }
                } else {
                    boolean b = ModelSessionData.getInstance().getFriends().containsKey(id);
                    if (b){
                        ModelSessionData.getInstance().getFriends().get(id).setState(state);
                        adapter.notifyItemChanged(getModelPersonIndex(setHashToList(ModelSessionData.getInstance().getFriends()), id));
                    }
                }
            }else if("new_user".equals(tag)){
                ModelPerson newUser = (ModelPerson) extras.getSerializable("new_user");
                assert newUser != null;
                int pos = getModelPersonIndex(setHashToList(ModelSessionData.getInstance().getFriends()),newUser.getId());
                if (pos==-1){
                    ModelSessionData.getInstance().getFriends().put(newUser.getId(),newUser);
                    adapter.addFriend(newUser);
                    addFriends.setVisibility(View.GONE);
                }
            } else if("friends_updated".equals(tag)){
                if(ModelSessionData.getInstance().getFriends().isEmpty()){
                    addFriends.setVisibility(View.VISIBLE);
                }else{
                    addFriends.setVisibility(View.GONE);
                }
            } else if("friends_state".equals(tag)){
                String data = extras.getString("friends_state");
                JSONObject dataObject = null;
                try {
                    dataObject = new JSONObject(data);
                    JSONArray dataArray = dataObject.getJSONArray("states");
                    HashMap<String, ModelPerson> friends = ModelSessionData.getInstance().getFriends();
                    for (int i=0; i<dataArray.length(); i++){
                        JSONObject actualFriendObject = (JSONObject) dataArray.get(i);
                        String id = actualFriendObject.getString("id");
                        String state = actualFriendObject.getString("state");

                        ModelPerson actualFriend = friends.get(id);
                        if (!actualFriend.getState().equals(state)){
                            actualFriend.setState(state);
                            adapter.notifyItemChanged(getModelPersonIndex(setHashToList(ModelSessionData.getInstance().getFriends()), id));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };


    @Override
    public void onResume() {
        super.onResume();
        mSwipeRefreshLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.FragmentContacts_SwipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.accent, R.color.primary, R.color.primary_dark);
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
        mSearchAction = menu.findItem(R.id.MenuHome_SearchContact);
        menu.findItem(R.id.MenuHome_AddGroup).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i==R.id.MenuHome_SearchContact){
            handleMenuSearch();
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
            friendsFiltred.addAll(setHashToList(ModelSessionData.getInstance().getFriends()));
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
                    adapterContact.updateElements(performSearch(setHashToList(ModelSessionData.getInstance().getFriends()), mSearchQuery));
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
            mSwipeRefreshLayout.setEnabled(false);
            new TaskRefresh(getActivity(),mSwipeRefreshLayout).execute(actualUser.getId());
        }else{
            mSwipeRefreshLayout.setRefreshing(false);
            mSwipeRefreshLayout.setEnabled(true);
            makeSnackbar(getActivity(),getView(), R.string.no_connection, Snackbar.LENGTH_LONG, R.string.button_change_connection, new View.OnClickListener() {
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
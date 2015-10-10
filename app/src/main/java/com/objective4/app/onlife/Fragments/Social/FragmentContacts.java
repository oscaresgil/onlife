package com.objective4.app.onlife.Fragments.Social;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.kenny.snackbar.SnackBar;
import com.objective4.app.onlife.Activities.ActivityHome;
import com.objective4.app.onlife.Adapters.AdapterContact;
import com.objective4.app.onlife.BlockActivity.ActivityFriendBlock;
import com.objective4.app.onlife.Models.ModelPerson;
import com.objective4.app.onlife.Models.ModelSessionData;
import com.objective4.app.onlife.R;
import com.objective4.app.onlife.Tasks.TaskRefresh;
import com.objective4.app.onlife.Tasks.TaskSendNotification;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.List;

import static com.objective4.app.onlife.Controller.StaticMethods.activateDeviceAdmin;
import static com.objective4.app.onlife.Controller.StaticMethods.animationStart;
import static com.objective4.app.onlife.Controller.StaticMethods.checkDeviceAdmin;
import static com.objective4.app.onlife.Controller.StaticMethods.isNetworkAvailable;
import static com.objective4.app.onlife.Controller.StaticMethods.showSoftKeyboard;

public class FragmentContacts extends Fragment {
    private ModelPerson actualUser;
    private List<ModelPerson> friends;

    private AdapterContact adapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private ListView listView;

    private MenuItem mSearchAction;
    private MaterialEditText searchText;
    private List<ModelPerson> friendsFiltred;
    public static boolean isSearchOpened = false;
    private String mSearchQuery;

    public FragmentContacts(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        setHasOptionsMenu(true);
        View v = inflater.inflate(R.layout.fragment_contacts, container, false);

        actualUser = ModelSessionData.getInstance().getUser();
        friends = ModelSessionData.getInstance().getFriends();

        friendsFiltred = new ArrayList<>();

        adapter = ((ActivityHome)getActivity()).getAdapterContact();
        listView = (ListView) v.findViewById(R.id.FragmentContacts_ListView);
        listView.setAdapter(adapter);
        listView.setSelector(R.drawable.list_selector);
        listView.setLongClickable(true);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!mSwipeRefreshLayout.isRefreshing()) {
                    ModelPerson user = adapter.getItem(position);
                    Intent i = new Intent(getActivity(), ActivityFriendBlock.class);
                    i.putExtra("data", user);
                    i.putExtra("actualuser", actualUser);
                    startActivity(i);
                    animationStart(getActivity());
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.toast_wait_until_contacts_refreshed), Toast.LENGTH_SHORT).show();
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                if (!mSwipeRefreshLayout.isRefreshing() && isNetworkAvailable(getActivity())) {
                    ModelPerson user = ModelSessionData.getInstance().getFriends().get(position);
                    boolean devAdmin = checkDeviceAdmin(getActivity());
                    if (user.getState().equals("A") && devAdmin) {
                        new TaskSendNotification(getActivity(), actualUser.getName(), "", "").execute(user);
                    } else if (!devAdmin) {
                        activateDeviceAdmin(getActivity());
                    } else {
                        SnackBar.show(getActivity(), R.string.friend_inactive);
                    }
                } else if (!isNetworkAvailable(getActivity())) {
                    SnackBar.show(getActivity(), R.string.no_connection, R.string.button_change_connection, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                        }
                    });
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.toast_wait_until_contacts_refreshed), Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
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
            AdapterContact adapterContact = ((ActivityHome)context).getAdapterContact();

            if ("update".equals(tag)) {
                String id = extras.getString("id");
                String state = extras.getString("state");

                List<ModelPerson> friendsT = adapterContact.getFriends();
                if ("O".equals(state)) {
                    ModelSessionData.getInstance().removeUser(id);

                    for (int i = 0; i < friendsT.size(); i++) {
                        ModelPerson p = friendsT.get(i);
                        if (id.equals(p.getId())){
                            adapterContact.remove(adapterContact.getItem(i));
                            break;
                        }
                    }
                    adapterContact.notifyDataSetChanged();
                } else {
                    for (ModelPerson p : friendsT) {
                        if (p.getId().equals(id)) {
                            p.setState(state);
                            break;
                        }
                    }
                    adapterContact.notifyDataSetChanged();
                }
            }else if("new_user".equals(tag)){
                ModelPerson newUser = (ModelPerson) extras.getSerializable("new_user");
                adapterContact.add(newUser);
                adapterContact.notifyDataSetChanged();
                listView.setAdapter(adapterContact);

                    adapterContact.notifyDataSetChanged();
                    listView.setAdapter(adapterContact);
                } else if("no_device_admin".equals(tag)){
                activateDeviceAdmin(getActivity());
            }
        }
    };


    @Override
    public void onResume() {
        super.onResume();
        mSwipeRefreshLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.FragmentContacts_SwipeRefreshLayout);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                boolean enable = false;
                if (listView != null && listView.getChildCount() > 0) {
                    boolean firstItemVisible = listView.getFirstVisiblePosition() == 0;
                    boolean topOfFirstItemVisible = listView.getChildAt(0).getTop() == 0;
                    enable = firstItemVisible && topOfFirstItemVisible;
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
        android.support.v7.app.ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
        if (isSearchOpened){
            listView.setAdapter(((ActivityHome) getActivity()).getAdapterContact());
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
            listView.setAdapter(new AdapterContact(getActivity(), R.layout.layout_contact, friendsFiltred));
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
                    AdapterContact adapterContact = (AdapterContact) listView.getAdapter();
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
            new TaskRefresh(getActivity(),mSwipeRefreshLayout, listView).execute(actualUser.getId());
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
    public static boolean isIsSearchOpened() {
        return isSearchOpened;
    }

}
package com.example.henzer.socialize;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.ListView;

import com.example.henzer.socialize.Adapters.ContactsAdapter;
import com.example.henzer.socialize.Adapters.DownloadImageTask;
import com.example.henzer.socialize.BlockActivity.FriendActionActivity;
import com.example.henzer.socialize.Models.Person;
import com.example.henzer.socialize.Models.SessionData;
import com.kenny.snackbar.SnackBar;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.yalantis.flipviewpager.utils.FlipSettings;

import java.util.ArrayList;
import java.util.List;

import static com.example.henzer.socialize.Adapters.StaticMethods.isNetworkAvailable;

/**
 * Created by hp1 on 21-01-2015.
 */
public class ContactsFragment extends ListFragment {
    private Person actualUser;
    private List<Person> friends;
    private ContactsAdapter adapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Menu optionsMenu;

    private MenuItem mSearchAction;
    private MaterialEditText searchText;
    private List<Person> friendsFiltred;
    private boolean isSearchOpened = false;
    private String mSearchQuery;

    public static final String TAG = "ContactsFragment";
    public static ContactsFragment newInstance(Bundle arguments){
        ContactsFragment myfragment = new ContactsFragment();
        if(arguments !=null){
            myfragment.setArguments(arguments);
        }
        return myfragment;
    }
    public ContactsFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);
        View v = inflater.inflate(R.layout.contacts_view, container, false);

        friendsFiltred = new ArrayList<>();

        FlipSettings settings = new FlipSettings.Builder().defaultPage(1).build();
        actualUser = ((SessionData)getArguments().getSerializable("data")).getUser();
        friends = ((SessionData)getArguments().getSerializable("data")).getFriends();
        friendsFiltred.addAll(friends);

        adapter =  new ContactsAdapter(getActivity(), friendsFiltred, settings);
        setListAdapter(adapter);

        return v;
    }



    @Override
    public void onResume() {
        super.onResume();

        mSwipeRefreshLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.contacts_refresh_swipelayout);
        getListView().setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {}
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                boolean enable = false;
                if (getListView() != null && getListView().getChildCount() > 0) {
                    boolean firstItemVisible = getListView().getFirstVisiblePosition() == 0;
                    boolean topOfFirstItemVisible = getListView().getChildAt(0).getTop() == 0;
                    enable = firstItemVisible && topOfFirstItemVisible;
                }
                mSwipeRefreshLayout.setEnabled(enable);
            }
        });
        mSwipeRefreshLayout.setColorSchemeResources(R.color.orange_light, R.color.orange);
        mSwipeRefreshLayout.setSize(15);
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
        Log.i("SI entre", "Logre entrar");
        if (i==R.id.searchContact){
            handleMenuSearch();
            adapter.notifyDataSetChanged();
        }

        return super.onOptionsItemSelected(item);
    }

    public List<Person> performSearch(List<Person> actualFriends, String query){
        String[] queryByWords = query.toLowerCase().split("\\s+");
        List<Person> filtred = new ArrayList<>();
        for (Person actual: actualFriends){
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
            friendsFiltred.clear();
            friendsFiltred.addAll(friends);
            actionBar.setDisplayShowCustomEnabled(false);
            actionBar.setDisplayShowTitleEnabled(true);

            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getWindow().getCurrentFocus().getWindowToken(), 0);
            mSearchAction.setIcon(getResources().getDrawable(R.drawable.ic_ic_search_black_48dp));
            isSearchOpened = false;
        }
        else{
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setCustomView(R.layout.search_contact_bar);
            actionBar.setDisplayShowTitleEnabled(false);

            searchText = (MaterialEditText) actionBar.getCustomView().findViewById(R.id.search_contact_text);
            searchText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    mSearchQuery = searchText.getText().toString();
                    friendsFiltred.clear();
                    friendsFiltred.addAll(performSearch(friends, mSearchQuery));
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
            searchText.requestFocus();
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(searchText, InputMethodManager.SHOW_IMPLICIT);

            mSearchAction.setIcon(getResources().getDrawable(R.drawable.ic_ic_close_black_48dp));

            isSearchOpened = true;
        }
    }

    public void refreshContact(){
        if (isNetworkAvailable(getActivity())) {
            String[] ids = new String[friends.size()];
            for (int i=0; i<friends.size(); i++){
                ids[i] = friends.get(i).getId();
            }
            new DownloadImageTask(getActivity(),mSwipeRefreshLayout,false,adapter).execute(ids);
            /*FacebookFriendRequest fbRequest = new FacebookFriendRequest(getActivity(),actualUser,friends,adapter);
            Bundle params = new Bundle();
            params.putString("fields","id,name");
            new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/friends", params, HttpMethod.GET, fbRequest).executeAsync();
            adapter.notifyDataSetChanged();*/
        }else{
            mSwipeRefreshLayout.setRefreshing(false);
            SnackBar.show(getActivity(), R.string.no_connection, R.string.change_connection, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                }
            });
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Person user = (Person)getListAdapter().getItem(position);
        Intent i = new Intent(getActivity(),FriendActionActivity.class);
        i.putExtra("data",user);
        i.putExtra("actualuser", actualUser);
        startActivity(i);
        getActivity().overridePendingTransition(R.animator.push_right, R.animator.push_left);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.optionsMenu = menu;
        //super.onCreateOptionsMenu(menu, inflater);
    }


}
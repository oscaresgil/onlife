package com.example.henzer.socialize.Fragments;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
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
import android.widget.GridView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.henzer.socialize.Adapters.AdapterContact;
import com.example.henzer.socialize.BlockActivity.ActivityFriendBlock;
import com.example.henzer.socialize.Models.ModelPerson;
import com.example.henzer.socialize.R;
import com.example.henzer.socialize.Tasks.TaskGPS;
import com.example.henzer.socialize.Tasks.TaskImageDownload;
import com.example.henzer.socialize.Models.ModelSessionData;
import com.example.henzer.socialize.Tasks.TaskSendNotification;
import com.kenny.snackbar.SnackBar;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.yalantis.flipviewpager.utils.FlipSettings;

import net.steamcrafted.loadtoast.LoadToast;

import java.util.ArrayList;
import java.util.List;

import static com.example.henzer.socialize.Controller.StaticMethods.isNetworkAvailable;

public class FragmentContacts extends Fragment {
    public static final String TAG = "ContactsFragment";

    private ModelPerson actualUser;
    private List<ModelPerson> friends;
    private AdapterContact adapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private GridView gridView;

    private MenuItem mSearchAction;
    private MaterialEditText searchText;
    private List<ModelPerson> friendsFiltred;
    private boolean isSearchOpened = false;
    private String mSearchQuery;

    private static boolean typeUrl = false;

    public static FragmentContacts newInstance(Bundle arguments){
        FragmentContacts myfragment = new FragmentContacts();
        if(arguments !=null){
            myfragment.setArguments(arguments);
        }
        return myfragment;
    }
    public FragmentContacts(){}

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);
        View v = inflater.inflate(R.layout.fragment_contacts, container, false);

        friendsFiltred = new ArrayList<>();

        actualUser = ((ModelSessionData)getArguments().getSerializable("data")).getUser();
        friends = ((ModelSessionData)getArguments().getSerializable("data")).getFriends();
        friendsFiltred.addAll(friends);

        adapter = new AdapterContact(getActivity(),friendsFiltred);
        gridView = (GridView) v.findViewById(R.id.FragmentContacts_GridView);
        gridView.setAdapter(adapter);
        gridView.setSelector(R.drawable.list_selector);
        gridView.setLongClickable(true);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!mSwipeRefreshLayout.isRefreshing()){
                    ModelPerson user = (ModelPerson)gridView.getItemAtPosition(position);
                    Intent i = new Intent(getActivity(),ActivityFriendBlock.class);
                    i.putExtra("data",user);
                    i.putExtra("actualuser", actualUser);
                    startActivity(i);
                    getActivity().overridePendingTransition(R.animator.push_right, R.animator.push_left);
                }else{
                    Toast.makeText(getActivity(),getResources().getString(R.string.toast_wait_until_contacts_refreshed),Toast.LENGTH_SHORT).show();
                }
            }
        });

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                if (!mSwipeRefreshLayout.isRefreshing() && isNetworkAvailable(getActivity())){
                    new MaterialDialog.Builder(getActivity())
                            .title(R.string.button_block)
                            .content(R.string.really_delete)
                            .positiveText(R.string.yes)
                            .positiveColorRes(R.color.orange_light)
                            .negativeText(R.string.no)
                            .negativeColorRes(R.color.red)
                            .callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    new TaskGPS(getActivity(),TAG,actualUser,(ModelPerson)gridView.getItemAtPosition(position)).execute();
                                    dialog.dismiss();
                                    dialog.cancel();
                                }
                            }).show();
                }else if(!isNetworkAvailable(getActivity())){
                    SnackBar.show(getActivity(), R.string.no_connection, R.string.button_change_connection, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                        }
                    });
                }
                else{
                    Toast.makeText(getActivity(),getResources().getString(R.string.toast_wait_until_contacts_refreshed),Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        mSwipeRefreshLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.FragmentContacts_SwipeRefreshLayout);
        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                boolean enable = false;
                if (gridView != null && gridView.getChildCount() > 0) {
                    boolean firstItemVisible = gridView.getFirstVisiblePosition() == 0;
                    boolean topOfFirstItemVisible = gridView.getChildAt(0).getTop() == 0;
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
            new TaskImageDownload(getActivity(),mSwipeRefreshLayout,false,adapter,friends).execute(typeUrl);
            /*FacebookFriendRequest fbRequest = new FacebookFriendRequest(getActivity(),actualUser,friends,adapter);
            Bundle params = new Bundle();
            params.putString("fields","id,name");
            new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/friends", params, HttpMethod.GET, fbRequest).executeAsync();
            adapter.notifyDataSetChanged();*/
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

    public static void setTypeUrl(boolean tUrl){
        typeUrl = tUrl;
    }

}
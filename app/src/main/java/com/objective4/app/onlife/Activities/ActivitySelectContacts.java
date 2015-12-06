package com.objective4.app.onlife.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.objective4.app.onlife.Adapters.AdapterStickyTitle;
import com.objective4.app.onlife.Models.ModelPerson;
import com.objective4.app.onlife.R;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderDecoration;

import static com.objective4.app.onlife.Controller.StaticMethods.makeSnackbar;
import static com.objective4.app.onlife.Controller.StaticMethods.performSearch;
import static com.objective4.app.onlife.Controller.StaticMethods.showSoftKeyboard;

public class ActivitySelectContacts extends AppCompatActivity {
    private RecyclerView mList;
    private StickyHeaderDecoration decoration;
    private StickyHeaderDecoration decorationSearch;
    private AdapterStickyTitle adapter;
    private List<ModelPerson> friends;

    private MaterialEditText searchText;
    private boolean isSearchOpened = false;
    private String mSearchQuery;
    private MenuItem mSearchAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contact);

        friends = new ArrayList<>();

        setSupportActionBar((Toolbar) findViewById(R.id.ActivitySelectContact_Toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.select_friend);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        }

        mList = (RecyclerView) findViewById(R.id.ActivitySelectContact_RecyclerViewList);

        adapter = new AdapterStickyTitle(this, friends);
        mList.setHasFixedSize(true);
        mList.setLayoutManager(new LinearLayoutManager(this));
        mList.setAdapter(adapter);
        mList.addItemDecoration(decoration = new StickyHeaderDecoration(adapter));

        GraphRequest.newMyFriendsRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONArrayCallback() {
            @Override
            public void onCompleted(JSONArray objects, GraphResponse response) {
                if (objects!=null) {
                    for (int i = 0; i < objects.length(); i++) {
                        try {
                            JSONObject actualF = (JSONObject) objects.get(i);
                            friends.add(new ModelPerson(actualF.getString("id"), actualF.getString("name")));

                            Collections.sort(friends, new Comparator<ModelPerson>() {
                                @Override
                                public int compare(ModelPerson modelPerson1, ModelPerson modelPerson2) {
                                    return modelPerson1.getName().compareTo(modelPerson2.getName());
                                }
                            });

                        } catch (JSONException ignored) {
                        }
                    }
                    adapter.setFriends(friends);
                }
            }


        }).executeAsync();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.ActivitySelectContact_FAB);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeSnackbar(ActivitySelectContacts.this,v,"Blacklist", Snackbar.LENGTH_LONG);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_group, menu);
        mSearchAction = menu.findItem(R.id.search_contact);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.search_contact){
            handleMenuSearch();
        }else{
            if (isSearchOpened) handleMenuSearch();
            else finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void handleMenuSearch(){
        ActionBar actionBar = getSupportActionBar();
        if (isSearchOpened){
            AdapterStickyTitle adapter = new AdapterStickyTitle(ActivitySelectContacts.this,friends);
            mList.removeItemDecoration(decorationSearch);
            mList.setAdapter(adapter);
            mList.addItemDecoration(decoration = new StickyHeaderDecoration(adapter));

            assert actionBar != null;
            actionBar.setDisplayShowCustomEnabled(false);
            actionBar.setDisplayShowTitleEnabled(true);

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);

            mSearchAction.setIcon(getResources().getDrawable(R.drawable.ic_search_black_24dp));
            isSearchOpened = false;
        }
        else{
            assert actionBar != null;
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setCustomView(R.layout.layout_search_contact_bar);
            actionBar.setDisplayShowTitleEnabled(false);

            mList.removeItemDecoration(decoration);
            mList.addItemDecoration(decorationSearch = new StickyHeaderDecoration(adapter));

            searchText = (MaterialEditText) actionBar.getCustomView().findViewById(R.id.LayoutSearchContactBar_EditTextSearch);
            searchText.setVisibility(View.VISIBLE);
            searchText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    mSearchQuery = searchText.getText().toString();

                    AdapterStickyTitle adapter = new AdapterStickyTitle(ActivitySelectContacts.this, performSearch(friends, mSearchQuery));
                    mList.removeItemDecoration(decorationSearch);
                    mList.setAdapter(adapter);
                    mList.addItemDecoration(decorationSearch = new StickyHeaderDecoration(adapter));
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
            searchText.requestFocus();
            showSoftKeyboard(this, searchText);
            mSearchAction.setIcon(getResources().getDrawable(R.drawable.ic_close_black_24dp));
            isSearchOpened = true;
        }
    }

}

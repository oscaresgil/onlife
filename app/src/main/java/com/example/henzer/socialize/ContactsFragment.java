package com.example.henzer.socialize;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.henzer.socialize.BlockActivity.FriendActionActivity;
import com.example.henzer.socialize.Models.Person;
import com.example.henzer.socialize.Models.SessionData;
import com.yalantis.flipviewpager.adapter.BaseFlipAdapter;
import com.yalantis.flipviewpager.utils.FlipSettings;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by hp1 on 21-01-2015.
 */
public class ContactsFragment extends ListFragment {
    private Person actualUser;
    private List<Person> friends;
    private ContactsAdapter adapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Menu optionsMenu;

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
        FlipSettings settings = new FlipSettings.Builder().defaultPage(1).build();
        actualUser = ((SessionData)getArguments().getSerializable("data")).getUser();
        friends = ((SessionData)getArguments().getSerializable("data")).getFriends();
        adapter =  new ContactsAdapter(getActivity(), friends, settings);
        setListAdapter(adapter);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        mSwipeRefreshLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.contacts_refresh_swipelayout);
        getListView().setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                boolean enable = false;
                if (getListView() != null && getListView().getChildCount() > 0) {
                    // check if the first item of the list is visible
                    boolean firstItemVisible = getListView().getFirstVisiblePosition() == 0;
                    // check if the top of the first item is visible
                    boolean topOfFirstItemVisible = getListView().getChildAt(0).getTop() == 0;
                    // enabling or disabling the refresh layout
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        Log.i("SI entre", "Logre entrar");
        if (i == R.id.refreshContacts) {
            refreshContact();
            adapter.notifyDataSetChanged();
            //setRefreshActionButtonState(false);
        }
        else{

        }
        return super.onOptionsItemSelected(item);
    }

    public void setRefreshActionButtonState(final boolean refreshing) {
        Log.i("Options Menu is null",(optionsMenu==null)+"");
        if (optionsMenu != null) {
            final MenuItem refreshItem = optionsMenu
                    .findItem(R.id.refreshContacts);
            Log.i("Refresh Item is null",(refreshItem==null)+"");
            if (refreshItem != null) {
                if (refreshing) {
                    refreshItem.setActionView(R.layout.refresh_contacts_actionbarlayout);
                } else {
                    refreshItem.setActionView(null);
                }
            }
        }
        else{
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void refreshContact(){
        if (isNetworkAvailable()) {
            String[] ids = new String[friends.size()];
            for (int i=0; i<friends.size(); i++){
                ids[i] = friends.get(i).getId();
            }
            new DownloadImageTask().execute(ids);
        }else{
            Toast.makeText(getActivity(), "No connection", Toast.LENGTH_LONG).show();
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
        //getActivity().overridePendingTransition(R.animator.push_left, R.animator.push_right);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.optionsMenu = menu;
        //super.onCreateOptionsMenu(menu, inflater);
    }

    private String guardarImagen(Context context, String name, Bitmap image){
        ContextWrapper cw = new ContextWrapper(context);
        File dirImages = cw.getDir("Profiles",Context.MODE_PRIVATE);
        File myPath = new File(dirImages, name+".png");

        FileOutputStream fos = null;
        try{
            fos = new FileOutputStream(myPath);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.flush();
        }catch (Exception e){e.printStackTrace();}
        Log.i("IMAGE SAVED","PATH: "+myPath);
        return myPath.getAbsolutePath();
    }

    private Bitmap cargarImagen(Context context, String name){
        ContextWrapper cw = new ContextWrapper(context);
        File dirImages = cw.getDir("Profiles",Context.MODE_APPEND);
        File myPath = new File(dirImages, name+".png");
        Bitmap b = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            b = BitmapFactory.decodeFile(myPath.getAbsolutePath(), options);
        }catch (Exception e){e.printStackTrace();}
        Log.i("IMAGE LOADED","PATH: "+myPath);
        return b;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            mSwipeRefreshLayout.setRefreshing(true);
            setRefreshActionButtonState(true);
        }

        protected Void doInBackground(String... urls) {
            try {
                for (String userID: urls){
                    Log.i("Actual BackGround User",userID);
                    String urlStr = "https://graph.facebook.com/" + userID + "/picture?width=700&height=700";

                    HttpClient client = new DefaultHttpClient();
                    HttpGet request = new HttpGet(urlStr);
                    HttpResponse response;
                    try {
                        response = (HttpResponse)client.execute(request);
                        HttpEntity entity = response.getEntity();
                        BufferedHttpEntity bufferedEntity = new BufferedHttpEntity(entity);
                        InputStream inputStream = bufferedEntity.getContent();
                        guardarImagen(getActivity(),userID,BitmapFactory.decodeStream(inputStream));
                    } catch (ClientProtocolException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }catch(Exception e){e.printStackTrace();}
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mSwipeRefreshLayout.setRefreshing(false);
            setRefreshActionButtonState(false);
            adapter.notifyDataSetChanged();
        }
    }

    class ContactsAdapter extends BaseFlipAdapter<Person> {
        private final int PAGES = 3;
        public ContactsAdapter(Context context, List<Person> items, FlipSettings settings) {
            super(context, items, settings);
        }

        @Override
        public View getPage(int i, View view, ViewGroup viewGroup, Person userData, Person userData2) {
            final ContactsHolder holder;
            if (view == null){
                holder = new ContactsHolder();
                view = getActivity().getLayoutInflater().inflate(R.layout.contacts, viewGroup, false);
                holder.leftAvatar = (ImageView) view.findViewById(R.id.first_image);
                holder.leftName = (TextView) view.findViewById(R.id.first_name);
                holder.rightAvatar = (ImageView) view.findViewById(R.id.second_image);
                holder.rightName = (TextView) view.findViewById(R.id.second_name);
                view.setTag(holder);
            }
            else{
                holder = (ContactsHolder) view.getTag();
            }
            holder.leftAvatar.setImageBitmap(cargarImagen(getActivity(), userData.getId()));
            holder.leftName.setText(userData.getName());
            if (userData2 !=null){
                holder.rightAvatar.setImageBitmap(cargarImagen(getActivity(), userData2.getId()));
                holder.rightName.setText(userData2.getName());
            }
            return view;
        }

        @Override
        public int getPagesCount() {
            return PAGES;
        }
    }

    class ContactsHolder{
        ImageView leftAvatar;
        ImageView rightAvatar;
        TextView leftName;
        TextView rightName;
    }
}
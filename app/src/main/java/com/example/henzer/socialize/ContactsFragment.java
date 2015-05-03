package com.example.henzer.socialize;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.henzer.socialize.Models.Person;
import com.yalantis.flipviewpager.adapter.BaseFlipAdapter;
import com.yalantis.flipviewpager.utils.FlipSettings;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by hp1 on 21-01-2015.
 */
public class ContactsFragment extends ListFragment {
    private List<Person> friends;
    private ContactsAdapter adapter;

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
        setHasOptionsMenu(false);
        View v = inflater.inflate(R.layout.contacts_view, container, false);
        FlipSettings settings = new FlipSettings.Builder().defaultPage(1).build();
        friends = ((SessionData)getArguments().getSerializable("data")).getFriends();

        for(Person u: friends){
            try {
                Bitmap b =new DownloadImageTask().execute(u.getId()).get();
                Log.i("BITMAP",b.toString());
                u.setIcon(b);
            }catch (Exception e){e.printStackTrace();}
        }
        adapter =  new ContactsAdapter(getActivity(), friends, settings);
        setListAdapter(adapter);
        return v;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Person user = (Person)getListAdapter().getItem(position);
        Toast.makeText(getActivity(), user.getName(), Toast.LENGTH_SHORT).show();
        Intent i = new Intent(getActivity(),FriendInfActivity.class);
        i.putExtra("data",user);
        startActivity(i);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.getItem(R.id.addGroup).setVisible(false);
        menu.getItem(R.id.refreshContacts).setVisible(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        Bitmap mIcon11;
        protected Bitmap doInBackground(String... urls) {
            try {
                String userID = urls[0];
                String urlStr = "https://graph.facebook.com/" + userID + "/picture?width=400&height=400";
                Bitmap img = null;

                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet(urlStr);
                HttpResponse response;
                try {
                    response = (HttpResponse)client.execute(request);
                    HttpEntity entity = response.getEntity();
                    BufferedHttpEntity bufferedEntity = new BufferedHttpEntity(entity);
                    InputStream inputStream = bufferedEntity.getContent();
                    img = BitmapFactory.decodeStream(inputStream);
                } catch (ClientProtocolException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return img;
            }catch(Exception e){e.printStackTrace();}
            return mIcon11;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            Log.i("BITMAP Icon", bitmap.toString());
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
                holder.rightAvatar = (ImageView) view.findViewById(R.id.second_image);
                holder.infoPage = getActivity().getLayoutInflater().inflate(R.layout.contacts_layout_values,viewGroup,false);
                holder.name = (TextView) holder.infoPage.findViewById(R.id.name);
                view.setTag(holder);
            }
            else{
                holder = (ContactsHolder) view.getTag();
            }
            if (i==1){
                holder.leftAvatar.setImageBitmap(userData.getIcon());
                userData.setIcon(null);
                if (userData2 !=null){
                    holder.rightAvatar.setImageBitmap(userData2.getIcon());
                    userData2.setIcon(null);
                }
            }
            else{
                fillContact(holder,i==0? userData:userData2);
                holder.infoPage.setTag(holder);
                return holder.infoPage;
            }
            return view;
        }

        @Override
        public int getPagesCount() {
            return PAGES;
        }
    }

    private void fillContact(ContactsHolder holder, Person friend){
        if (friend==null) return;
        holder.infoPage.setBackgroundColor(Color.BLACK);
        holder.name.setText(friend.getName());
    }

    class ContactsHolder{
        ImageView leftAvatar;
        ImageView rightAvatar;
        View infoPage;
        TextView name;
    }
}
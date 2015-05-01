package com.example.henzer.socialize;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by hp1 on 21-01-2015.
 */
public class ContactsFragment extends ListFragment {
    private List<UserData> friends;
    private ContactsAdapter adapter;

    public static final String TAG = "ContactsFragment";
    public static ContactsFragment newInstance(Bundle arguments){
        ContactsFragment myfragment = new ContactsFragment();
        if(arguments !=null){
            myfragment.setArguments(arguments);
        }
        return myfragment;
    }
    public ContactsFragment(){

    }

    public static Bitmap getFacebookProfilePicture(String userID){
        try {
            URL imageURL = new URL("https://graph.facebook.com/" + userID + "/picture?type=large");
            Bitmap bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
            return bitmap;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.contacts_view, container, false);
        FlipSettings settings = new FlipSettings.Builder().defaultPage(0).build();
        friends = ((SessionData)getArguments().getSerializable("data")).getFriends();

        for(UserData u: friends){
            u.setIcon(getFacebookProfilePicture(u.getId()));
        }
        adapter =  new ContactsAdapter(getActivity(), friends, settings);
        setListAdapter(adapter);
        return v;
    }

    class ContactsAdapter extends BaseFlipAdapter<UserData> {
        private final int PAGES = 3;
        public ContactsAdapter(Context context, List<UserData> items, FlipSettings settings) {
            super(context, items, settings);
        }

        @Override
        public View getPage(int i, View view, ViewGroup viewGroup, UserData userData, UserData userData2) {
            final ContactsHolder holder;
            if (view == null){
                holder = new ContactsHolder();
                view = getActivity().getLayoutInflater().inflate(R.layout.contacts, viewGroup, false);
                holder.leftAvatar = (ImageView) view.findViewById(R.id.first_image);
                holder.rightAvatar = (ImageView) view.findViewById(R.id.second_image);
                //holder.infoPage = getActivity().getLayoutInflater().inflate(R.layout.contacts_layout_values,viewGroup,false);
                //holder.name = (TextView) holder.infoPage.findViewById(R.id.name);
                view.setTag(holder);
            }
            else{
                holder = (ContactsHolder) view.getTag();
            }
            if (i==1){
                holder.leftAvatar.setImageBitmap(userData.getIcon());
                if (userData2 !=null){
                    holder.rightAvatar.setImageBitmap(userData2.getIcon());
                }
            }
            else{
                //fillContact(holder,i==0? allContactsFriends:allContactsFriends2);
                //holder.infoPage.setTag(holder);
                //return holder.infoPage;
            }
            return view;
        }

        @Override
        public int getPagesCount() {
            return PAGES;
        }
    }

    private void fillContact(ContactsHolder holder, UserData friend){
        if (friend==null) return;
        //holder.infoPage.setBackgroundColor(getResources().getColor(friend.getBackground()));
        //holder.name.setText(friend.getName());
    }

    class ContactsHolder{
        ImageView leftAvatar;
        ImageView rightAvatar;
        //View infoPage;
        TextView name;
    }
}
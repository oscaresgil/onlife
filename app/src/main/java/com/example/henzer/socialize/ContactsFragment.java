package com.example.henzer.socialize;

import android.content.Context;
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

import java.util.List;

/**
 * Created by hp1 on 21-01-2015.
 */
public class ContactsFragment extends ListFragment {
    List<UserData> friends = MainActivity.getFriends();
    private ContactsAdapter adapter;

    public ContactsFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.contacts_view, container, false);
        FlipSettings settings = new FlipSettings.Builder().defaultPage(0).build();
        Log.i("Friends ", friends.toString());
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
                holder.leftAvatar.setImageBitmap(userData.getImageAvatar());
                if (userData2 !=null){
                    holder.rightAvatar.setImageBitmap(userData2.getImageAvatar());
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
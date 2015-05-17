package com.example.henzer.socialize;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
                guardarImagen(getActivity(), u.getId(), new DownloadImageTask().execute(u.getId()).get());
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
        Intent i = new Intent(getActivity(),FriendActionActivity.class);
        i.putExtra("data",user);
        startActivity(i);
        getActivity().overridePendingTransition(R.animator.push_left, R.animator.push_right);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.getItem(R.id.addGroup).setVisible(false);
        menu.getItem(R.id.refreshContacts).setVisible(true);
        super.onCreateOptionsMenu(menu, inflater);
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
        return b;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        Bitmap mIcon11;
        protected Bitmap doInBackground(String... urls) {
            try {
                String userID = urls[0];
                String urlStr = "https://graph.facebook.com/" + userID + "/picture?width=700&height=700";
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
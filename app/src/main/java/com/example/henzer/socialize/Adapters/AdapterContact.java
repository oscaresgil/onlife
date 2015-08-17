package com.example.henzer.socialize.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.henzer.socialize.Models.ModelPerson;
import com.example.henzer.socialize.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.example.henzer.socialize.Controller.StaticMethods.loadImage;
import static com.example.henzer.socialize.Controller.StaticMethods.loadImagePath;

public class AdapterContact extends BaseAdapter {
    private Context context;
    private List<ModelPerson> friends;

    public AdapterContact(Context context, List<ModelPerson> friends) {
        this.context = context;
        this.friends = friends;
    }

    @Override
    public int getCount() {
        return friends.size();
    }

    @Override
    public ModelPerson getItem(int position) {
        return friends.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ModelPerson userData = friends.get(position);
        ContactHolder contactHolder;
        if (view == null){
            view = ((Activity)context).getLayoutInflater().inflate(R.layout.layout_contact, parent, false);
            contactHolder = new ContactHolder();
            contactHolder.avatar = (ImageView) view.findViewById(R.id.LayoutContact_ImageViewLeft);
            contactHolder.name = (TextView) view.findViewById(R.id.LayoutContact_TextViewNameLeft);

            Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x/2;

            Picasso.with(context).load(loadImagePath(context,userData.getId())).resize(width,width).into(contactHolder.avatar);
            contactHolder.name.setText(userData.getName());

            view.setTag(contactHolder);
        }
        else{
            contactHolder = (ContactHolder) view.getTag();
        }

        return view;
    }
    static class ContactHolder{
        ImageView avatar;
        TextView name;
    }
}

package com.example.henzer.socialize.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.henzer.socialize.Models.ModelPerson;
import com.example.henzer.socialize.R;

import net.soulwolf.widget.ratiolayout.widget.RatioImageView;

import java.util.List;

import static com.example.henzer.socialize.Controller.StaticMethods.loadImage;

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
            contactHolder.avatar = (RatioImageView) view.findViewById(R.id.LayoutContact_ImageViewLeft);
            contactHolder.name = (TextView) view.findViewById(R.id.LayoutContact_TextViewNameLeft);

            view.setTag(contactHolder);
        }
        else{
            contactHolder = (ContactHolder) view.getTag();
        }

        contactHolder.avatar.setImageBitmap(loadImage(context,userData.getId()));
        contactHolder.name.setText(userData.getName());

        return view;
    }
    static class ContactHolder{
        RatioImageView avatar;
        TextView name;
    }
}

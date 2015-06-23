package com.example.henzer.socialize.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.henzer.socialize.Models.Person;
import com.example.henzer.socialize.R;
import com.yalantis.flipviewpager.adapter.BaseFlipAdapter;
import com.yalantis.flipviewpager.utils.FlipSettings;

import java.util.List;

import static com.example.henzer.socialize.Controller.StaticMethods.loadImage;

public class AdapterContacts extends BaseFlipAdapter<Person> {
    private final int PAGES = 3;
    private Activity context;

    public AdapterContacts(Context context, List<Person> items, FlipSettings settings) {
        super(context, items, settings);
        this.context = (Activity)context;
    }

    @Override public View getPage(int i, View view, ViewGroup viewGroup, Person userData, Person userData2) {
        final ContactsHolder holder;
        if (view == null){
            holder = new ContactsHolder();
            view = context.getLayoutInflater().inflate(R.layout.layout_contact, viewGroup, false);

            holder.leftAvatar = (ImageView) view.findViewById(R.id.LayoutContact_ImageViewLeft);
            holder.leftName = (TextView) view.findViewById(R.id.LayoutContact_TextViewNameLeft);
            holder.rightAvatar = (ImageView) view.findViewById(R.id.LayoutContact_ImageViewRight);
            holder.rightName = (TextView) view.findViewById(R.id.LayoutContact_TextViewNameRight);
            view.setTag(holder);
        }
        else{
            holder = (ContactsHolder) view.getTag();
        }

        holder.leftAvatar.setImageBitmap(loadImage(context, userData.getId()));
        holder.leftName.setText(userData.getName());
        if (userData2 !=null){
            holder.rightAvatar.setImageBitmap(loadImage(context, userData2.getId()));
            holder.rightName.setText(userData2.getName());
            view.findViewById(R.id.LayoutContact_LinearLayoutRightContact).setVisibility(View.VISIBLE);
        }
        else{
            view.findViewById(R.id.LayoutContact_LinearLayoutRightContact).setVisibility(View.INVISIBLE);
        }

        return view;
    }

    @Override public int getPagesCount() {
        return PAGES;
    }

    class ContactsHolder{
        ImageView leftAvatar;
        ImageView rightAvatar;
        TextView leftName;
        TextView rightName;
    }
}



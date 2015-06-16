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

import static com.example.henzer.socialize.Adapters.StaticMethods.cargarImagen;

public class ContactsAdapter extends BaseFlipAdapter<Person> {
    private final int PAGES = 3;
    private Activity context;

    public ContactsAdapter(Context context, List<Person> items, FlipSettings settings) {
        super(context, items, settings);
        this.context = (Activity)context;
    }

    @Override
    public View getPage(int i, View view, ViewGroup viewGroup, Person userData, Person userData2) {
        final ContactsHolder holder;
        if (view == null){
            holder = new ContactsHolder();
            view = context.getLayoutInflater().inflate(R.layout.contacts, viewGroup, false);

            holder.leftAvatar = (ImageView) view.findViewById(R.id.first_image);
            holder.leftName = (TextView) view.findViewById(R.id.first_name);
            holder.rightAvatar = (ImageView) view.findViewById(R.id.second_image);
            holder.rightName = (TextView) view.findViewById(R.id.second_name);
            view.setTag(holder);
        }
        else{
            holder = (ContactsHolder) view.getTag();
        }
        holder.leftAvatar.setImageBitmap(cargarImagen(context, userData.getId()));
        holder.leftName.setText(userData.getName());
        if (userData2 !=null){
            holder.rightAvatar.setImageBitmap(cargarImagen(context, userData2.getId()));
            holder.rightName.setText(userData2.getName());
        }
        else{
            view.findViewById(R.id.secondContact).setVisibility(View.INVISIBLE);
        }
        return view;
    }

    @Override
    public int getPagesCount() {
        return PAGES;
    }

    class ContactsHolder{
        ImageView leftAvatar;
        ImageView rightAvatar;
        TextView leftName;
        TextView rightName;
    }
}



package com.example.henzer.socialize.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.henzer.socialize.Listeners.ListenerFlipCheckbox;
import com.example.henzer.socialize.Models.ModelPerson;
import com.example.henzer.socialize.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.example.henzer.socialize.Controller.StaticMethods.loadImagePath;

public class AdapterCheckList extends ArrayAdapter<ModelPerson> {
    private List<ModelPerson> friends;
    private Context context;

    public AdapterCheckList(Context context, int textViewResourceId, List<ModelPerson> friends) {
        super(context, textViewResourceId, friends);
        this.friends = friends;
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Holder holder;
        if (convertView == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.layout_select_contact_group, parent, false);

            holder = new Holder();
            holder.avatar = (ImageView) convertView.findViewById(R.id.LayoutSelectContactGroup_ImageViewFriend);
            holder.name = (TextView) convertView.findViewById(R.id.LayoutSelectContactGroup_TextViewNameFriend);
            convertView.setTag(holder);
        }
        else {
            holder = (Holder) convertView.getTag();
        }

        ModelPerson friend = friends.get(position);
        Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x/4;
        if (friend.isSelected()){
            Picasso.with(getContext()).load(R.drawable.ic_navigation_check).resize(width,width).into(holder.avatar);
        }
        else{
            Picasso.with(getContext()).load(loadImagePath(getContext(),friend.getId())).resize(width,width).into(holder.avatar);
        }
        holder.avatar.setTag(friend);
        holder.name.setText(friend.getName());
        return convertView;

    }
    private class Holder {
        ImageView avatar;
        TextView name;
    }
}

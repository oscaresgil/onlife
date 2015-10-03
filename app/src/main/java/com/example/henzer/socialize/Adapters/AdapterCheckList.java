package com.example.henzer.socialize.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.henzer.socialize.Models.ModelPerson;
import com.example.henzer.socialize.R;

import net.soulwolf.widget.ratiolayout.widget.RatioImageView;

import java.util.List;

import static com.example.henzer.socialize.Controller.StaticMethods.loadImage;

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
            holder.avatar = (RatioImageView) convertView.findViewById(R.id.LayoutSelectContactGroup_ImageViewFriend);
            holder.avatar.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),R.drawable.loading_friend_icon));
            holder.name = (TextView) convertView.findViewById(R.id.LayoutSelectContactGroup_TextViewNameFriend);
            convertView.setTag(holder);
        }
        else {
            holder = (Holder) convertView.getTag();
        }

        ModelPerson friend = friends.get(position);
        if (friend.isSelected()){
            holder.avatar.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_done_large));
        }
        else{
            holder.avatar.setImageBitmap(loadImage(context,friend.getId()+"_"+context.getResources().getInteger(R.integer.adapter_contact_size_little)));
        }

        holder.avatar.setTag(friend);
        holder.name.setText(friend.getName());
        return convertView;

    }
    private class Holder {
        RatioImageView avatar;
        TextView name;
    }
}

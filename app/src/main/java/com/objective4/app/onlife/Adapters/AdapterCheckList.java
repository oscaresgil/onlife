package com.objective4.app.onlife.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.objective4.app.onlife.Models.ModelPerson;
import com.objective4.app.onlife.R;

import java.util.List;

import static com.objective4.app.onlife.Controller.StaticMethods.imageInDisk;
import static com.objective4.app.onlife.Controller.StaticMethods.loadImage;

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
            if (imageInDisk(context,friend.getId()+"_"+context.getResources().getInteger(R.integer.adapter_contact_size_little))){
                holder.avatar.setImageBitmap(loadImage(context,friend.getId()+"_"+context.getResources().getInteger(R.integer.adapter_contact_size_little)));
            }else{
                holder.avatar.setImageDrawable(context.getResources().getDrawable(R.drawable.loading_friend_icon));
            }
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

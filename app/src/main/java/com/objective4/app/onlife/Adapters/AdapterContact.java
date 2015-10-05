package com.objective4.app.onlife.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.objective4.app.onlife.Models.ModelPerson;
import com.objective4.app.onlife.R;
import com.objective4.app.onlife.Tasks.TaskSimpleImageDownload;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.objective4.app.onlife.Controller.StaticMethods.imageInDisk;
import static com.objective4.app.onlife.Controller.StaticMethods.loadImage;

public class AdapterContact extends ArrayAdapter<ModelPerson> {
    private Context context;
    private List<ModelPerson> friends;

    public AdapterContact(Context context, int resource, List<ModelPerson> friends) {
        super(context, resource, friends);
        this.context = context;
        this.friends = friends;
    }

    @Override
    public int getCount() {
        return friends.size();
    }

    @Override
    public void notifyDataSetChanged() {
        Collections.sort(friends, new Comparator<ModelPerson>() {
            @Override
            public int compare(ModelPerson modelPerson1, ModelPerson modelPerson2) {
                return modelPerson1.getName().compareTo(modelPerson2.getName());
            }
        });
        super.notifyDataSetChanged();
    }

    @Override
    public ModelPerson getItem(int position) {
        return friends.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public List<ModelPerson> getFriends() {
        return friends;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ModelPerson userData = friends.get(position);
        ContactHolder contactHolder;

        if (view == null){
            view = ((Activity)context).getLayoutInflater().inflate(R.layout.layout_contact, parent, false);
            contactHolder = new ContactHolder();
            contactHolder.avatar = (ImageView) view.findViewById(R.id.LayoutContact_ImageViewFriend);
            contactHolder.name = (TextView) view.findViewById(R.id.LayoutContact_TextViewNameFriend);
            contactHolder.visibility = (ImageView)view.findViewById(R.id.LayoutContact_VisibilityImageView);
            view.setTag(contactHolder);
        }
        else{
            contactHolder = (ContactHolder) view.getTag();
        }

        contactHolder.visibility.bringToFront();
        contactHolder.name.bringToFront();
        contactHolder.avatar.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),R.drawable.loading_friend_icon));

        if (imageInDisk(context, userData.getId()+"_"+context.getResources().getInteger(R.integer.adapter_contact_size_little))){
            contactHolder.avatar.setImageBitmap(loadImage(context,userData.getId()+"_"+context.getResources().getInteger(R.integer.adapter_contact_size_little)));
        }else{
            new TaskSimpleImageDownload(context,contactHolder.avatar,context.getResources().getInteger(R.integer.adapter_contact_size_little)).execute(userData);
            new TaskSimpleImageDownload(context,contactHolder.avatar,context.getResources().getInteger(R.integer.adapter_contact_size_large)).execute(userData);
        }
        contactHolder.name.setText(userData.getName());
        if (userData.getState().equals("I")){
            contactHolder.visibility.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_action_visibility_off));
        }else if(userData.getState().equals("A")){
            contactHolder.visibility.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_action_visibility_on));
        }

        return view;
    }

    static class ContactHolder{
        ImageView avatar;
        TextView name;
        ImageView visibility;
    }
}

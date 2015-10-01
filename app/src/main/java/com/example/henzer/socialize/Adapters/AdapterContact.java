package com.example.henzer.socialize.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.henzer.socialize.Models.ModelPerson;
import com.example.henzer.socialize.R;
import com.example.henzer.socialize.Tasks.TaskSimpleImageDownload;

import net.soulwolf.widget.ratiolayout.widget.RatioImageView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.example.henzer.socialize.Controller.StaticMethods.imageInDisk;
import static com.example.henzer.socialize.Controller.StaticMethods.loadImage;

public class AdapterContact extends ArrayAdapter<ModelPerson> {
    public static final String TAG="AdapterContact";
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

        Log.i(TAG,"Position: "+position+". View Null?: "+(view==null)+" User: "+userData.toString());
        if (view == null){
            view = ((Activity)context).getLayoutInflater().inflate(R.layout.layout_contact, parent, false);
            contactHolder = new ContactHolder();
            contactHolder.avatar = (RatioImageView) view.findViewById(R.id.LayoutContact_GifImageView);
            contactHolder.name = (TextView) view.findViewById(R.id.LayoutContact_TextViewNameLeft);
            contactHolder.visibility = (ImageView)view.findViewById(R.id.LayoutContact_RadioButton);
            view.setTag(contactHolder);
        }
        else{
            contactHolder = (ContactHolder) view.getTag();
        }

        contactHolder.visibility.bringToFront();
        contactHolder.name.bringToFront();
        contactHolder.avatar.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),R.drawable.loading_friend_icon));

        if (imageInDisk(context, userData.getId()+"_"+context.getResources().getInteger(R.integer.adapter_contact_size_large))){
            contactHolder.avatar.setImageBitmap(loadImage(context,userData.getId()+"_"+context.getResources().getInteger(R.integer.adapter_contact_size_large)));
        }else{
            new TaskSimpleImageDownload(context,contactHolder.avatar,context.getResources().getInteger(R.integer.adapter_contact_size_large)).execute(userData);
            new TaskSimpleImageDownload(context,contactHolder.avatar,context.getResources().getInteger(R.integer.adapter_contact_size_little)).execute(userData);
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
        RatioImageView avatar;
        TextView name;
        ImageView visibility;
    }
}

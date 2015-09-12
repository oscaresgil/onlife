package com.example.henzer.socialize.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.henzer.socialize.Models.ModelPerson;
import com.example.henzer.socialize.R;
import com.example.henzer.socialize.Tasks.TaskSimpleImageDownload;

import net.soulwolf.widget.ratiolayout.widget.RatioImageView;

import java.util.List;

import static com.example.henzer.socialize.Controller.StaticMethods.imageInDisk;
import static com.example.henzer.socialize.Controller.StaticMethods.loadImage;

public class AdapterContact extends ArrayAdapter<ModelPerson> {
    public static final String TAG="AdapterContact";
    private Context context;
    private List<ModelPerson> friends;
    private int size;

    public AdapterContact(Context context, int resource, List<ModelPerson> friends) {
        super(context, resource, friends);
        this.context = context;
        this.friends = friends;
        size = context.getResources().getInteger(R.integer.adapter_contact_size_little);
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
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ModelPerson userData = friends.get(position);
        ContactHolder contactHolder;
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

        if (imageInDisk(context, userData.getId())){
            contactHolder.avatar.setImageBitmap(loadImage(context,userData.getId()));
        }else{
            new TaskSimpleImageDownload(context,contactHolder.avatar,size).execute(userData);
        }
        contactHolder.name.setText(userData.getName());
        Log.i(TAG, "Adapter User: "+userData.toString());
        if (userData.getState().equals("O")){
            contactHolder.visibility.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_visib_red));
        }else if (userData.getState().equals("I")){
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

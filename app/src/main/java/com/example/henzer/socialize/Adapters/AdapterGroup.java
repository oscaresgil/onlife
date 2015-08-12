package com.example.henzer.socialize.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.henzer.socialize.Models.ModelPerson;
import com.example.henzer.socialize.R;

import java.util.List;

import static com.example.henzer.socialize.Controller.StaticMethods.loadImage;

public class AdapterGroup extends ArrayAdapter<ModelPerson> {
    private List<ModelPerson> objects;
    private int resource;
    private Context context;

    public AdapterGroup(Context context, int resource, List<ModelPerson> objects) {
        super(context, resource, objects);
        this.objects = objects;
        this.resource = resource;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        View rowView = inflater.inflate(resource, null, true);
        ImageView avatar = (ImageView) rowView.findViewById(R.id.LayoutFriendsInGroup_ImageViewContact);
        TextView text = (TextView) rowView.findViewById(R.id.LayoutGroups_TextViewGroupName);
        avatar.setImageBitmap(loadImage(context, objects.get(position).getId()));
        text.setText(objects.get(position).getName());
        return rowView;
    }
}
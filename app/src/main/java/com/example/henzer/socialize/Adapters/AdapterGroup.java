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
        GroupHolder groupHolder;

        if (convertView == null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            convertView = inflater.inflate(resource, null, true);

            groupHolder = new GroupHolder();
            groupHolder.avatar = (ImageView) convertView.findViewById(R.id.LayoutFriendsInGroup_ImageViewContact);
            groupHolder.text = (TextView) convertView.findViewById(R.id.LayoutGroups_TextViewGroupName);
            groupHolder.avatar.setImageBitmap(loadImage(context, objects.get(position).getId()));
            groupHolder.text.setText(objects.get(position).getName());

            convertView.setTag(groupHolder);
        }
        else{
            groupHolder = (GroupHolder) convertView.getTag();
        }

        return convertView;
    }

    static class GroupHolder{
        ImageView avatar;
        TextView text;
    }
}
package com.example.henzer.socialize.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.henzer.socialize.Models.ModelGroup;
import com.example.henzer.socialize.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.example.henzer.socialize.Controller.StaticMethods.loadImage;
import static com.example.henzer.socialize.Controller.StaticMethods.loadImagePath;

public class AdapterGroup extends ArrayAdapter<ModelGroup> {
    private Context context;
    private List<ModelGroup> objects;
    private int resource;

    public AdapterGroup(Context context, int resource, List<ModelGroup> objects) {
        super(context, resource, objects);
        this.objects = objects;
        this.context = context;
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HolderGroup holderGroup;

        Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x/4;

        if (convertView==null){
            holderGroup = new HolderGroup();

            convertView = ((Activity)context).getLayoutInflater().inflate(resource,null,true);
            holderGroup.textView = (TextView) convertView.findViewById(R.id.LayoutGroups_TextViewGroupName);
            holderGroup.imageView = (ImageView) convertView.findViewById(R.id.LayoutGroups_CircleImageViewGroup);
            convertView.setTag(holderGroup);
        }else{
            holderGroup = (HolderGroup) convertView.getTag();
        }

        holderGroup.imageView.setImageBitmap(null);
        Picasso.with(context).load(loadImagePath(getContext(),objects.get(position).getName())).resize(width,width).into(holderGroup.imageView);
        holderGroup.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        holderGroup.textView.setText(objects.get(position).getName());

        return convertView;
    }
    static class HolderGroup{
        TextView textView;
        ImageView imageView;
    }
}
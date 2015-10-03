package com.example.henzer.socialize.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.henzer.socialize.Models.ModelGroup;
import com.example.henzer.socialize.R;

import net.soulwolf.widget.ratiolayout.widget.RatioImageView;

import java.util.List;

import static com.example.henzer.socialize.Controller.StaticMethods.loadImage;

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

        if (convertView==null){
            holderGroup = new HolderGroup();

            convertView = ((Activity)context).getLayoutInflater().inflate(resource,null,true);
            holderGroup.textView = (TextView) convertView.findViewById(R.id.LayoutGroups_TextViewGroupName);
            holderGroup.imageView = (RatioImageView) convertView.findViewById(R.id.LayoutGroups_CircleImageViewGroup);
            convertView.setTag(holderGroup);
        }else{
            holderGroup = (HolderGroup) convertView.getTag();
        }

        holderGroup.imageView.setImageBitmap(null);
        holderGroup.imageView.setImageBitmap(loadImage(getContext(),objects.get(position).getName()));
        
        holderGroup.textView.setText(objects.get(position).getName());

        return convertView;
    }
    static class HolderGroup{
        TextView textView;
		RatioImageView imageView;
    }
}
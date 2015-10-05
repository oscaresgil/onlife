package com.objective4.app.onlife.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.objective4.app.onlife.Models.ModelGroup;
import com.objective4.app.onlife.R;

import java.util.List;

import static com.objective4.app.onlife.Controller.StaticMethods.loadImage;

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

            convertView = ((Activity)context).getLayoutInflater().inflate(resource,parent,false);
            holderGroup.textView = (TextView) convertView.findViewById(R.id.LayoutGroups_TextViewGroupName);
            holderGroup.imageView = (ImageView) convertView.findViewById(R.id.LayoutGroups_CircleImageViewGroup);
            convertView.setTag(holderGroup);
        }else{
            holderGroup = (HolderGroup) convertView.getTag();
        }

        holderGroup.imageView.setImageBitmap(loadImage(getContext(),objects.get(position).getName()));
        holderGroup.textView.setText(objects.get(position).getName());

        return convertView;
    }
    static class HolderGroup{
        TextView textView;
		ImageView imageView;
    }
}
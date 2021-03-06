package com.objective4.app.onlife.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.objective4.app.onlife.R;

import java.util.List;

import static com.objective4.app.onlife.Controller.StaticMethods.decodeDrawable;

public class AdapterEmoticon extends BaseAdapter {
    private Context context;
    private List<String> emoticonImages;

    public AdapterEmoticon(Context context, List<String> emoticonImages) {
        this.context = context;
        this.emoticonImages = emoticonImages;
    }

    @Override
    public int getCount() {
        return emoticonImages.size();
    }

    @Override
    public String getItem(int position) {
        return emoticonImages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EmoticonHolder emoticonHolder;
        if (convertView==null){
            convertView = ((Activity)context).getLayoutInflater().inflate(R.layout.layout_single_emoticon,parent,false);
            emoticonHolder = new EmoticonHolder();

            emoticonHolder.emoticonImageView = (ImageView) convertView;
            convertView.setTag(emoticonHolder);
        }else{
            emoticonHolder = (EmoticonHolder) convertView.getTag();
        }

        String name = emoticonImages.get(position);
        int resourceId = context.getResources().getIdentifier(name, "drawable", context.getPackageName());
        Bitmap b = decodeDrawable(context,resourceId,50);

        //emoticonHolder.emoticonImageView.setImageDrawable(context.getResources().getDrawable(resourceId));
        emoticonHolder.emoticonImageView.setImageDrawable(new BitmapDrawable(context.getResources(),b));
        return convertView;
    }

    static class EmoticonHolder {
        ImageView emoticonImageView;
    }
}

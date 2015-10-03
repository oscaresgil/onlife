package com.objective4.app.onlife.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.objective4.app.onlife.R;

import java.util.List;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class AdapterEmoticon extends BaseAdapter {
    private Context context;
    private List<String> gifImages;

    public AdapterEmoticon(Context context, List<String> gifImages) {
        this.context = context;
        this.gifImages = gifImages;
    }

    @Override
    public int getCount() {
        return gifImages.size();
    }

    @Override
    public String getItem(int position) {
        return gifImages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GifHolder gifHolder;
        if (convertView==null){
            convertView = ((Activity)context).getLayoutInflater().inflate(R.layout.layout_emoticon,parent,false);
            gifHolder = new GifHolder();

            gifHolder.gifImageView = (GifImageView) convertView;
            convertView.setTag(gifHolder);
        }else{
            gifHolder = (GifHolder) convertView.getTag();
        }

        String name = gifImages.get(position);

        int resourceId = context.getResources().getIdentifier(name, "drawable", context.getPackageName());
        GifDrawable gif = null;
        try {
            gif = new GifDrawable(context.getResources(), resourceId);
        }catch(Exception e){
            e.printStackTrace();
        }
        gifHolder.gifImageView.setImageDrawable(gif);
        gifHolder.gifImageView.setId(position);
        gifHolder.gifImageView.setAdjustViewBounds(true);
        gifHolder.gifImageView.setScaleType(ImageView.ScaleType.CENTER);


        return convertView;
    }

    static class GifHolder{
        GifImageView gifImageView;
    }
}

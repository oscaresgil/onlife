package com.example.henzer.socialize.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.henzer.socialize.R;
import com.kenny.snackbar.SnackBar;
import com.squareup.picasso.Picasso;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.List;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageButton;
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
        GifImageView imageView = (GifImageView) ((Activity)context).getLayoutInflater().inflate(R.layout.layout_emoticon,parent,false);
        String name = gifImages.get(position);
        GifDrawable gif = null;
        try {
            switch (name){
                case "gif1":
                    gif = new GifDrawable(context.getResources(), R.drawable.gif1);
                    break;
                case "gif2":
                    gif = new GifDrawable(context.getResources(), R.drawable.gif2);
                    break;
                case "gif3":
                    gif = new GifDrawable(context.getResources(), R.drawable.gif3);
                    break;
                case "gif4":
                    gif = new GifDrawable(context.getResources(), R.drawable.gif4);
                    break;
                case "gif5":
                    gif = new GifDrawable(context.getResources(), R.drawable.gif5);
                    break;
                case "gif6":
                    gif = new GifDrawable(context.getResources(), R.drawable.gif6);
                    break;
                case "gif7":
                    gif = new GifDrawable(context.getResources(), R.drawable.gif7);
                    break;
                case "gif8":
                    gif = new GifDrawable(context.getResources(), R.drawable.gif8);
                    break;
                case "gif9":
                    gif = new GifDrawable(context.getResources(), R.drawable.gif9);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageView.setImageDrawable(gif);
        imageView.setId(position);
        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        /*imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SnackBar.show((Activity)context,""+v.getId());
            }
        });*/
        return imageView;
    }
}

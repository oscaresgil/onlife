package com.example.henzer.socialize.Adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.henzer.socialize.Models.ModelMessages;
import com.example.henzer.socialize.R;

import java.util.List;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class AdapterMessageInBlock extends ArrayAdapter<ModelMessages> {
    public static final String TAG = "AdapterMessageInBlock";
    private List<ModelMessages> messages;
    private int resource;

    public AdapterMessageInBlock(Context context, int resource, List<ModelMessages> objects) {
        super(context, resource, objects);
        messages = objects;
        this.resource = resource;
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public ModelMessages getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        ModelMessages message = messages.get(position);
        Log.i(TAG, "Actual Message: " + message.toString());
        MessageHolder holder;
        if (v==null){
            v = ((Activity)getContext()).getLayoutInflater().inflate(resource, parent, false);
            holder = new MessageHolder();
            holder.message = (TextView) v.findViewById(R.id.LayoutMessagesInBlock_TextViewMessage);
            holder.userName = (TextView) v.findViewById(R.id.LayoutMessagesInBlock_TextViewUser);
            holder.gifName = (GifImageView) v.findViewById(R.id.LayoutMessagesInBlock_GifImageView);

            v.setTag(holder);
        }else{
            holder = (MessageHolder) v.getTag();

        }
        holder.userName.setText(message.getUserName());
        if (!message.getMessage().equals("")){
            holder.message.setText(message.getMessage());
        }else{
            holder.message.setVisibility(View.GONE);
        }
        if (!message.getGifName().equals("")){
            int resourceId = getContext().getResources().getIdentifier(message.getGifName(), "drawable", getContext().getPackageName());
            GifDrawable gif = null;
            try {
                gif = new GifDrawable(getContext().getResources(), resourceId);
            }catch(Exception e){
                e.printStackTrace();
            }
            holder.gifName.setImageDrawable(gif);
        }else{
            holder.gifName.setVisibility(View.GONE);
        }

        return v;
    }

    class MessageHolder{
        TextView userName;
        TextView message;
        GifImageView gifName;
    }
}

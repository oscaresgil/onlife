package com.objective4.app.onlife.Adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.objective4.app.onlife.Listeners.ListenerFlipCheckbox;
import com.objective4.app.onlife.Models.ModelPerson;
import com.objective4.app.onlife.R;
import com.objective4.app.onlife.Tasks.TaskSimpleImageDownload;

import java.util.List;

import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderAdapter;

import static com.objective4.app.onlife.Controller.StaticMethods.imageInDisk;
import static com.objective4.app.onlife.Controller.StaticMethods.loadImage;

public class AdapterStickyTitle extends RecyclerView.Adapter<AdapterStickyTitle.ViewHolder> implements StickyHeaderAdapter<AdapterStickyTitle.HeaderHolder> {
    private Context context;
    private List<ModelPerson> friends;

    public AdapterStickyTitle(Context context, List<ModelPerson> friends) {
        this.context = context;
        this.friends = friends;
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_select_contact_group,parent,false));
    }

    @Override public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.textView.setText(friends.get(i).getName());
        ModelPerson userData = friends.get(i);

        if (userData.isSelected()){
            viewHolder.imageView.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_done_large));
        }
        else{
            if (!imageInDisk(context,userData.getId() + "_" + context.getResources().getInteger(R.integer.adapter_contact_size_little))){
                if (AdapterBaseElements.AsyncDrawable.cancelPotentialWork(userData.getId(), viewHolder.imageView)){
                    final TaskSimpleImageDownload task = new TaskSimpleImageDownload(context,viewHolder.imageView,context.getResources().getInteger(R.integer.adapter_contact_size_little));
                    final AdapterBaseElements.AsyncDrawable asyncDrawable = new AdapterBaseElements.AsyncDrawable(context.getResources(), BitmapFactory.decodeResource(context.getResources(), R.drawable.loading_friend_icon),task);
                    viewHolder.imageView.setImageDrawable(asyncDrawable);
                    task.execute(userData);
                    userData.setRefreshImage(false);
                }
            }else{
                viewHolder.imageView.setImageBitmap(loadImage(context, userData.getId() + "_" + context.getResources().getInteger(R.integer.adapter_contact_size_little)));
            }
        }
    }

    @Override public int getItemCount() {
        return friends.size();
    }

    @Override public long getHeaderId(int position) {
        return friends.get(position).getName().charAt(0);
    }

    @Override public HeaderHolder onCreateHeaderViewHolder(ViewGroup parent) {
        return new HeaderHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_header_select_friends, parent, false));
    }

    @Override public void onBindHeaderViewHolder(HeaderHolder viewholder, int position) {
        viewholder.header.setText(Character.toString ((char) getHeaderId(position)));
    }

    public void setFriends(List<ModelPerson> friends){
        this.friends = friends;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView textView;
        public ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            CardView cardView = (CardView) itemView;
            cardView.setOnClickListener(this);
            imageView = (ImageView) cardView.findViewById(R.id.LayoutSelectContactGroup_ImageViewFriend);
            textView = (TextView) cardView.findViewById(R.id.LayoutSelectContactGroup_TextViewNameFriend);
            textView.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/oldrepublic.ttf"));
        }

        @Override
        public void onClick(View v) {
            ModelPerson actualFriend = friends.get(getLayoutPosition());
            actualFriend.setSelected(!actualFriend.isSelected());
            new ListenerFlipCheckbox(context, AnimationUtils.loadAnimation(context, R.anim.flip_left_out),AnimationUtils.loadAnimation(context, R.anim.flip_left_in)).setFriendAndView(actualFriend, (ImageView) v.findViewById(R.id.LayoutSelectContactGroup_ImageViewFriend));
        }
    }

    class HeaderHolder extends RecyclerView.ViewHolder{
        public TextView header;

        public HeaderHolder(View itemView) {
            super(itemView);
            header = (TextView) itemView.findViewById(R.id.LayoutHeader_TextView);
            header.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/oldrepublic.ttf"));
        }
    }
}

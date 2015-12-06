package com.objective4.app.onlife.Adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
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

import java.util.List;

import static com.objective4.app.onlife.Controller.StaticMethods.imageInDisk;
import static com.objective4.app.onlife.Controller.StaticMethods.loadImage;

public class AdapterSelectFriend extends RecyclerView.Adapter<AdapterSelectFriend.SelectFriendHolder> {
    private Context context;
    private ListenerFlipCheckbox listener;
    private List<ModelPerson> friends;

    public AdapterSelectFriend(Context context, List<ModelPerson> friends) {
        this.context = context;
        this.friends = friends;
        listener = new ListenerFlipCheckbox(context,AnimationUtils.loadAnimation(context, R.anim.flip_left_out),AnimationUtils.loadAnimation(context, R.anim.flip_left_in));
    }

    @Override
    public SelectFriendHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SelectFriendHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_select_contact_group,parent,false));
    }

    @Override
    public void onBindViewHolder(SelectFriendHolder holder, int position) {
        ModelPerson friend = friends.get(position);
        if (friend.isSelected()){
            holder.avatar.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_done_large));
        }
        else{
            if (imageInDisk(context,friend.getId()+"_"+context.getResources().getInteger(R.integer.adapter_contact_size_little))){
                holder.avatar.setImageBitmap(loadImage(context,friend.getId()+"_"+context.getResources().getInteger(R.integer.adapter_contact_size_little)));
            }else{
                holder.avatar.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.loading_friend_icon));
            }
        }

        holder.avatar.setTag(friend);
        holder.name.setText(friend.getName());
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    class SelectFriendHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView avatar;
        private TextView name;

        public SelectFriendHolder(View itemView) {
            super(itemView);
            avatar = (ImageView) itemView.findViewById(R.id.LayoutSelectContactGroup_ImageViewFriend);
            name = (TextView) itemView.findViewById(R.id.LayoutSelectContactGroup_TextViewNameFriend);
            name.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/oldrepublic.ttf"));
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            ModelPerson actualFriend = friends.get(getLayoutPosition());
            actualFriend.setSelected(!actualFriend.isSelected());

            listener.setFriendAndView(actualFriend,avatar);
        }
    }
}

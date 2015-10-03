package com.objective4.app.onlife.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.objective4.app.onlife.Listeners.ListenerFlipCheckbox;
import com.objective4.app.onlife.Models.ModelPerson;
import com.objective4.app.onlife.R;

import net.soulwolf.widget.ratiolayout.widget.RatioImageView;

import java.util.List;

import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderAdapter;

import static com.objective4.app.onlife.Controller.StaticMethods.loadImage;

public class AdapterStickyTitle extends RecyclerView.Adapter<AdapterStickyTitle.ViewHolder> implements StickyHeaderAdapter<AdapterStickyTitle.HeaderHolder> {
    private RecyclerView recyclerView;
    private LayoutInflater mInflater;
    private Context context;
    private List<ModelPerson> friends;
    private ListenerFlipCheckbox listener;
    private Animation animation1;

    public AdapterStickyTitle(Context context, RecyclerView recyclerView, List<ModelPerson> friends, ListenerFlipCheckbox listener, Animation animation1) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
        this.recyclerView = recyclerView;
        this.friends = friends;
        this.animation1 = animation1;
        this.listener = listener;
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.layout_select_contact_group, viewGroup, false);
        view.setOnClickListener(new OnClick(recyclerView));
        return new ViewHolder(view);
    }

    @Override public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.textView.setText(friends.get(i).getName());
        /*if (!friends.get(i).isHomeSelected()){
            viewHolder.imageView.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_navigation_check));
        }
        else{*/
            viewHolder.imageView.setImageBitmap(loadImage(context, friends.get(i).getId()+"_"+context.getResources().getInteger(R.integer.adapter_contact_size_large)));
        //}
    }

    @Override public int getItemCount() {
        return friends.size();
    }

    @Override public long getHeaderId(int position) {
        return friends.get(position).getName().charAt(0);
    }

    @Override public HeaderHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = mInflater.inflate(R.layout.textview_header_title, parent, false);
        return new HeaderHolder(view);
    }

    @Override public void onBindHeaderViewHolder(HeaderHolder viewholder, int position) {
        viewholder.header.setText(Character.toString ((char) getHeaderId(position)));
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);

            LinearLayout linearLayout = (LinearLayout) itemView;
            imageView = (ImageView) linearLayout.findViewById(R.id.LayoutSelectContactGroup_ImageViewFriend);
            textView = (TextView) linearLayout.findViewById(R.id.LayoutSelectContactGroup_TextViewNameFriend);
        }
    }

    class HeaderHolder extends RecyclerView.ViewHolder {
        public TextView header;

        public HeaderHolder(View itemView) {
            super(itemView);
            header = (TextView) itemView;
        }
    }

    class OnClick implements View.OnClickListener{
        private RecyclerView recyclerView;
        public OnClick(RecyclerView recyclerView){
            this.recyclerView = recyclerView;
        }
        @Override
        public void onClick(View v) {
            int item = recyclerView.getChildAdapterPosition(v);
            friends.get(item).setSelected(!friends.get(item).isSelected());

            RatioImageView avatar = (RatioImageView) v.findViewById(R.id.LayoutSelectContactGroup_ImageViewFriend);
            listener.setFriend(friends.get(item));
            listener.setView(avatar);
            avatar.clearAnimation();
            avatar.setAnimation(animation1);
            avatar.startAnimation(animation1);
        }
    }
}

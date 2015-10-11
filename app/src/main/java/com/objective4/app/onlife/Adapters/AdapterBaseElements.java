package com.objective4.app.onlife.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kenny.snackbar.SnackBar;
import com.objective4.app.onlife.Fragments.Social.FragmentContacts;
import com.objective4.app.onlife.Fragments.Social.FragmentGroups;
import com.objective4.app.onlife.Models.ModelGroup;
import com.objective4.app.onlife.Models.ModelPerson;
import com.objective4.app.onlife.Models.ModelSessionData;
import com.objective4.app.onlife.R;
import com.objective4.app.onlife.Tasks.TaskSendNotification;
import com.objective4.app.onlife.Tasks.TaskSimpleImageDownload;

import java.io.Serializable;
import java.util.List;

import static com.objective4.app.onlife.Controller.StaticMethods.activateDeviceAdmin;
import static com.objective4.app.onlife.Controller.StaticMethods.animationStart;
import static com.objective4.app.onlife.Controller.StaticMethods.checkDeviceAdmin;
import static com.objective4.app.onlife.Controller.StaticMethods.imageInDisk;
import static com.objective4.app.onlife.Controller.StaticMethods.loadImage;

public class AdapterBaseElements<T> extends RecyclerView.Adapter<AdapterBaseElements.ElementHolder> {
    private Context context;
    private List<T> elements;
    private Class typeClass;
    private Class intentClass;

    public AdapterBaseElements(Context context, List<T> elements, Class typeClass, Class intentClass) {
        this.context = context;
        this.elements = elements;
        this.intentClass = intentClass;
        this.typeClass = typeClass;
    }

    @Override
    public ElementHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ElementHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_base,parent,false));
    }

    @Override
    public void onBindViewHolder(AdapterBaseElements.ElementHolder holder, int position) {
        holder.visibility.bringToFront();
        holder.name.bringToFront();

        if (typeClass == FragmentContacts.class){
            ModelPerson userData = (ModelPerson) elements.get(position);
            if (userData.refreshImage() || !imageInDisk(context, userData.getId() + "_" + context.getResources().getInteger(R.integer.adapter_contact_size_little))){
                holder.avatar.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.loading_friend_icon));
                new TaskSimpleImageDownload(context,holder.avatar,context.getResources().getInteger(R.integer.adapter_contact_size_little)).execute(userData);
                userData.setRefreshImage(false);
            }else{
                holder.avatar.setImageBitmap(loadImage(context, userData.getId() + "_" + context.getResources().getInteger(R.integer.adapter_contact_size_little)));
            }
            holder.name.setText(userData.getName());
            if (userData.getState().equals("I")){
                holder.visibility.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_action_visibility_off));
            }else if(userData.getState().equals("A")){
                holder.visibility.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_action_visibility_on));
            }

        }else if(typeClass == FragmentGroups.class){
            ModelGroup groupData = (ModelGroup) elements.get(position);
            holder.avatar.setImageBitmap(loadImage(context, groupData.getName()));
            holder.name.setText(groupData.getName());
            holder.visibility.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return elements.size();
    }

    public void addAll(List<T> modelPersons) {
        elements.addAll(modelPersons);
        notifyDataSetChanged();
    }

    public void clear(){
        if (elements!=null)
            elements.clear();
        notifyDataSetChanged();
    }

    public List<T> getFriends() {
        return elements;
    }

    public class ElementHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener{
        private ImageView avatar;
        private TextView name;
        private ImageView visibility;

        public ElementHolder(View view) {
            super(view);

            avatar = (ImageView) view.findViewById(R.id.LayoutBase_ImageViewFriend);
            name = (TextView) view.findViewById(R.id.LayoutBase_TextViewNameFriend);
            visibility = (ImageView)view.findViewById(R.id.LayoutBase_VisibilityImageView);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //v.startAnimation(AnimationUtils.loadAnimation(context,R.anim.on_click));
            Intent i = new Intent(context, intentClass);
            i.putExtra("data", (Serializable) elements.get(getLayoutPosition()));
            i.putExtra("actualuser", ModelSessionData.getInstance().getUser());
            context.startActivity(i);
            animationStart(context);
        }

        @Override
        public boolean onLongClick(View v) {
            //v.startAnimation(AnimationUtils.loadAnimation(context,R.anim.on_long_click));
            Activity activity = (Activity)context;

            if (typeClass == FragmentContacts.class){
                ModelPerson user = (ModelPerson) elements.get(getLayoutPosition());
                boolean devAdmin = checkDeviceAdmin(context);
                if (user.getState().equals("A") && devAdmin) {
                    new TaskSendNotification(activity, ModelSessionData.getInstance().getUser().getName(), "", "").execute(user);
                } else if (!devAdmin) {
                    activateDeviceAdmin(activity);
                } else {
                    SnackBar.show(activity, R.string.friend_inactive);
                }
            }else if(typeClass == FragmentGroups.class){
                ModelGroup actualModelGroup = (ModelGroup) elements.get(getLayoutPosition());
                boolean activeDev = checkDeviceAdmin(activity);
                if (activeDev) {
                    new TaskSendNotification(activity, ModelSessionData.getInstance().getUser().getName(), "", "").execute(actualModelGroup.getFriendsInGroup().toArray(new ModelPerson[actualModelGroup.getFriendsInGroup().size()]));
                } else{
                    activateDeviceAdmin(activity);
                }
            }

            return true;
        }
    }


}

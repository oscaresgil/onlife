package com.objective4.app.onlife.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.objective4.app.onlife.BlockActivity.ActivityGroupBlock;
import com.objective4.app.onlife.Fragments.Social.FragmentContacts;
import com.objective4.app.onlife.Fragments.Social.FragmentGroups;
import com.objective4.app.onlife.Models.ModelGroup;
import com.objective4.app.onlife.Models.ModelPerson;
import com.objective4.app.onlife.Models.ModelSessionData;
import com.objective4.app.onlife.R;
import com.objective4.app.onlife.Tasks.TaskSendNotification;
import com.objective4.app.onlife.Tasks.TaskSimpleImageDownload;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.List;

import static com.objective4.app.onlife.Controller.StaticMethods.activateDeviceAdmin;
import static com.objective4.app.onlife.Controller.StaticMethods.animationStart;
import static com.objective4.app.onlife.Controller.StaticMethods.checkDeviceAdmin;
import static com.objective4.app.onlife.Controller.StaticMethods.getModelPersonIndex;
import static com.objective4.app.onlife.Controller.StaticMethods.imageInDisk;
import static com.objective4.app.onlife.Controller.StaticMethods.loadImage;
import static com.objective4.app.onlife.Controller.StaticMethods.makeSnackbar;

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
                if (AsyncDrawable.cancelPotentialWork(userData.getId(), holder.avatar)){
                    final TaskSimpleImageDownload task = new TaskSimpleImageDownload(context,holder.avatar,context.getResources().getInteger(R.integer.adapter_contact_size_little));
                    final AsyncDrawable asyncDrawable = new AsyncDrawable(context.getResources(),BitmapFactory.decodeResource(context.getResources(), R.drawable.loading_friend_icon),task);
                    holder.avatar.setImageDrawable(asyncDrawable);
                    task.execute(userData);
                    userData.setRefreshImage(false);
                }
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
            if (imageInDisk(context,groupData.getName()))
                holder.avatar.setImageBitmap(loadImage(context, groupData.getName()));
            else
                holder.avatar.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_launcher));
            holder.name.setText(groupData.getName());
            holder.visibility.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return elements.size();
    }

    public void addFriend(T element){
        ModelPerson p = (ModelPerson) element;
        int pos = getModelPersonIndex((List<ModelPerson>) elements, p.getId());
        if (pos==-1){
            elements.add(element);
            pos = getModelPersonIndex((List<ModelPerson>) elements, p.getId());
            notifyItemInserted(pos);
        }
    }

    public void removeFriend(String id){
        int pos = getModelPersonIndex((List<ModelPerson>) elements,id);
        if (pos!=-1){
            elements.remove(pos);
            notifyItemRemoved(pos);
        }
    }

    public void updateElements(List<T> modelPersons) {
        if (elements!=null) {
            elements.clear();
            elements.addAll(modelPersons);
            notifyDataSetChanged();
        }
    }

    public class ElementHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener{
        public static final int GROUP_BLOCK_ACTIVITY_ID = 3;
        private ImageView avatar;
        private TextView name;
        private ImageView visibility;

        public ElementHolder(View view) {
            super(view);
            avatar = (ImageView) view.findViewById(R.id.LayoutBase_ImageViewFriend);
            name = (TextView) view.findViewById(R.id.LayoutBase_TextViewNameFriend);
            name.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/oldrepublic.ttf"));
            visibility = (ImageView)view.findViewById(R.id.LayoutBase_VisibilityImageView);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent(context, intentClass);
            i.putExtra("data", (Serializable) elements.get(getLayoutPosition()));
            i.putExtra("actualuser", ModelSessionData.getInstance().getUser());
            i.putExtra("position",getLayoutPosition());
            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(context, R.anim.slide_right, R.anim.slide_left);

                if (intentClass == ActivityGroupBlock.class){
                    i.putExtra("position", getLayoutPosition());
                    ActivityCompat.startActivityForResult((Activity)context, i, GROUP_BLOCK_ACTIVITY_ID, options.toBundle());

                }else{
                    ActivityCompat.startActivity((Activity)context, i, options.toBundle());

                }
            } else {*/
            if (intentClass == ActivityGroupBlock.class){
                ((Activity)context).startActivityForResult(i, GROUP_BLOCK_ACTIVITY_ID);
            }else{
                context.startActivity(i);
            }
            //}

            animationStart(context);
        }

        @Override
        public boolean onLongClick(View v) {
            Activity activity = (Activity)context;

            if (typeClass == FragmentContacts.class){
                ModelPerson user = (ModelPerson) elements.get(getLayoutPosition());
                boolean devAdmin = checkDeviceAdmin(context);
                if (user.getState().equals("A") && devAdmin) {
                    new TaskSendNotification(activity, ModelSessionData.getInstance().getUser().getName(), "", "").execute(user);
                } else if (!devAdmin) {
                    activateDeviceAdmin(activity);
                } else {
                    makeSnackbar(context, v, R.string.friend_inactive, Snackbar.LENGTH_SHORT);
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

    public static class AsyncDrawable extends BitmapDrawable{
        private final WeakReference<TaskSimpleImageDownload> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap, TaskSimpleImageDownload bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference =new WeakReference<>(bitmapWorkerTask);
        }

        public TaskSimpleImageDownload getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }

        public static TaskSimpleImageDownload getBitmapWorkerTask(ImageView imageView){
            if (imageView != null){
                final Drawable drawable = imageView.getDrawable();
                if (drawable instanceof AdapterBaseElements.AsyncDrawable){
                    final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                    return asyncDrawable.getBitmapWorkerTask();
                }
            }
            return null;
        }

        public static boolean cancelPotentialWork(String id, ImageView imageView){
            final TaskSimpleImageDownload bitmapTask = getBitmapWorkerTask(imageView);
            if (bitmapTask != null){
                final String bitmapData = bitmapTask.data;
                if ("".equals(bitmapData) || !id.equals(bitmapData)){
                    bitmapTask.cancel(true);
                }
                else{
                    return false;
                }

            }
            return true;
        }
    }
}
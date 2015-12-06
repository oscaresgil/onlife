package com.objective4.app.onlife.Listeners;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v4.content.ContextCompat;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.objective4.app.onlife.Models.ModelPerson;
import com.objective4.app.onlife.R;

import static com.objective4.app.onlife.Controller.StaticMethods.imageInDisk;
import static com.objective4.app.onlife.Controller.StaticMethods.loadImage;

public class ListenerFlipCheckbox implements Animation.AnimationListener{
    private Context context;
    private ModelPerson f;
    private ImageView imageButton;
    private Animation animation1,animation2;

    public ListenerFlipCheckbox(Context context, Animation animation1, Animation animation2) {
        this.context = context;
        this.animation1 = animation1;
        this.animation2 = animation2;

        animation1.setAnimationListener(this);
        animation2.setAnimationListener(this);
    }

    public void setFriendAndView(ModelPerson f, ImageView imageButton){
        this.f = f;
        this.imageButton = imageButton;
        imageButton.setAnimation(animation1);
        imageButton.startAnimation(animation1);
    }

    @Override public void onAnimationStart(Animation animation) {

    }

    @Override public void onAnimationEnd(Animation animation) {
        if (animation == animation1){
            if (!f.isSelected()){
                if (imageInDisk(context,f.getId()+"_"+context.getResources().getInteger(R.integer.adapter_contact_size_little))){
                    imageButton.setImageBitmap(loadImage(context,f.getId()+"_"+context.getResources().getInteger(R.integer.adapter_contact_size_little)));
                }else{
                    imageButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.loading_friend_icon));
                }
            }
            else{
                imageButton.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_action_done_large));
            }
            imageButton.clearAnimation();
            imageButton.setAnimation(animation2);
            imageButton.startAnimation(animation2);
        }
    }

    @Override public void onAnimationRepeat(Animation animation) {

    }
}

package com.objective4.app.onlife.Listeners;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.Display;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.objective4.app.onlife.Models.ModelPerson;
import com.objective4.app.onlife.R;

import static com.objective4.app.onlife.Controller.StaticMethods.loadImage;

public class ListenerFlipCheckbox implements Animation.AnimationListener{
    private Context context;
    private ModelPerson f;
    private ImageView imageButton;
    private Animation animation1,animation2;

    public ListenerFlipCheckbox(Context context){
        this.context = context;
    }

    public void setFriend(ModelPerson f){
        this.f = f;
    }

    public void setView(ImageView imageButton){
        this.imageButton = imageButton;
    }

    public void setAnimation1(Animation animation1) {
        this.animation1 = animation1;
    }

    public void setAnimation2(Animation animation2) {
        this.animation2 = animation2;
    }

    @Override public void onAnimationStart(Animation animation) {

    }

    @Override public void onAnimationEnd(Animation animation) {
        if (animation == animation1){
            if (!f.isSelected()){
                imageButton.setImageBitmap(loadImage(context,f.getId()+"_"+context.getResources().getInteger(R.integer.adapter_contact_size_large)));
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

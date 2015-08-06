package com.example.henzer.socialize.Listeners;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.example.henzer.socialize.Models.ModelPerson;
import com.example.henzer.socialize.R;

import static com.example.henzer.socialize.Controller.StaticMethods.loadImage;

public class ListenerFlipCheckbox implements Animation.AnimationListener{
    private Context context;
    private ModelPerson f;
    private ImageView imageButton;
    private Animation animation1,animation2;
    private boolean home;

    public ListenerFlipCheckbox(Context context){
        this.context = context;
    }

    public void setHome(boolean home) {
        this.home = home;
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
        if (home) {
            if (animation == animation1) {
                if (!f.isHomeSelected()) {
                    imageButton.setImageBitmap(loadImage(context, f.getId()));
                } else {
                    imageButton.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_navigation_check));
                }
                imageButton.clearAnimation();
                imageButton.setAnimation(animation2);
                imageButton.startAnimation(animation2);
            } else {
                f.setHomeSelected(!f.isHomeSelected());
            }
        }
        else{
            if (animation == animation1){
                if (!f.isSelected()){
                    imageButton.setImageBitmap(loadImage(context, f.getId()));
                }
                else{
                    imageButton.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_navigation_check));
                }
                imageButton.clearAnimation();
                imageButton.setAnimation(animation2);
                imageButton.startAnimation(animation2);
            }
            else{
                f.setDeleted(!f.isSelected());
            }
        }
    }

    @Override public void onAnimationRepeat(Animation animation) {

    }
}

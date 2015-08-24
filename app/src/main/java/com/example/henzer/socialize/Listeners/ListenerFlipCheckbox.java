package com.example.henzer.socialize.Listeners;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.view.Display;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.example.henzer.socialize.Models.ModelPerson;
import com.example.henzer.socialize.R;
import com.squareup.picasso.Picasso;

import static com.example.henzer.socialize.Controller.StaticMethods.loadImage;
import static com.example.henzer.socialize.Controller.StaticMethods.loadImagePath;

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
        Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x/4;
        if (home) {
            if (animation == animation1) {
                if (!f.isHomeSelected()) {
                    Picasso.with(context).load(loadImagePath(context,f.getId())).resize(width,width).into(imageButton);
                } else {
                    Picasso.with(context).load(R.drawable.ic_done_black_24dp).resize(width,width).into(imageButton);
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
                    Picasso.with(context).load(loadImagePath(context,f.getId())).resize(width,width).into(imageButton);
                }
                else{
                    Picasso.with(context).load(R.drawable.ic_done_black_24dp).resize(width,width).into(imageButton);
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

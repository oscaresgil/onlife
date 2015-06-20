package com.example.henzer.socialize.Adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.example.henzer.socialize.Models.Person;
import com.example.henzer.socialize.R;

import static com.example.henzer.socialize.Adapters.StaticMethods.loadImage;

/**
 * Created by Boris on 6/20/2015.
 */
public class FlipListener implements Animation.AnimationListener{
    private Context context;
    private Person f;
    private ImageView imageButton;
    private boolean isShowingBack;
    private Animation animation1;
    private Animation animation2;

    public FlipListener(Context context){
        this.context = context;
    }

    public void setFriend(Person f){
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

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        Log.i("AnimationsEqual", (animation == animation1) + "");
        Log.i("BackImageShowing",isShowingBack+"");
        if (animation == animation1){
            if (!f.isSelected()){
                imageButton.setImageBitmap(loadImage(context, f.getId()));
            }
            else{
                imageButton.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher));
            }
            imageButton.clearAnimation();
            imageButton.setAnimation(animation2);
            imageButton.startAnimation(animation2);
        }
        else{
            isShowingBack=!isShowingBack;
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}

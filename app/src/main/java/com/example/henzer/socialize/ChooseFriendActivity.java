package com.example.henzer.socialize;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.transition.Transition;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;

/**
 * Created by Boris on 6/17/2015.
 */
public class ChooseFriendActivity extends Activity{

    private final int ANIM_DURATION=1000;
    private ViewGroup viewGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_friend_activity);

        viewGroup = (ViewGroup) findViewById(R.id.bgChoose_Friend);
        animate();

    }

    public void animate(){
        enterAnimation();
        exitAnimation();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void enterAnimation(){
        Transition transitionEnter = getWindow().getSharedElementEnterTransition();
        transitionEnter.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {

            }

            @Override
            public void onTransitionEnd(Transition transition) {
                revealShow(viewGroup);
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        });
    }
    public void exitAnimation(){
        Transition returnTransition = getWindow().getSharedElementReturnTransition();
        returnTransition.setStartDelay(ANIM_DURATION);

        Transition rTransition = getWindow().getReturnTransition();
        rTransition.setDuration(ANIM_DURATION);
        rTransition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                revealHide(viewGroup);
            }

            @Override
            public void onTransitionEnd(Transition transition) {

            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void revealShow(ViewGroup viewRoot){
        int cx = (viewRoot.getLeft() + viewRoot.getRight()) / 2;
        int cy = (viewRoot.getTop() + viewRoot.getBottom()) / 2;
        int finalRadius = Math.max(viewRoot.getWidth(), viewRoot.getHeight());

        Animator anim = ViewAnimationUtils.createCircularReveal(viewRoot, cx, cy, 0, finalRadius);
        viewRoot.setVisibility(View.VISIBLE);
        anim.setDuration(ANIM_DURATION);
        anim.start();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void revealHide(final ViewGroup viewRoot){
        int cx = (viewRoot.getLeft() + viewRoot.getRight()) / 2;
        int cy = (viewRoot.getTop() + viewRoot.getBottom()) / 2;
        int initialRadius = viewRoot.getWidth();

        Animator anim = ViewAnimationUtils.createCircularReveal(viewRoot, cx, cy, initialRadius, 0);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                viewRoot.setVisibility(View.INVISIBLE);
            }
        });
        anim.setDuration(ANIM_DURATION);
        anim.start();
    }
}

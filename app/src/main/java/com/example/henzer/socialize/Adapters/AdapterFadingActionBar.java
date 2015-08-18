package com.example.henzer.socialize.Adapters;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

import com.manuelpeinado.fadingactionbar.FadingActionBarHelperBase;

public class AdapterFadingActionBar extends FadingActionBarHelperBase{
    private ActionBar actionBar;
    @Override
    protected int getActionBarHeight() {
        return actionBar.getHeight();
    }

    @Override
    protected boolean isActionBarNull() {
        return actionBar == null;
    }

    @Override
    public void initActionBar(Activity activity) {
        actionBar = ((ActionBarActivity) activity).getSupportActionBar();
        super.initActionBar(activity);
    }

    @Override
    protected void setActionBarBackgroundDrawable(Drawable drawable) {
        actionBar.setBackgroundDrawable(drawable);
    }
}

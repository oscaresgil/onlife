package com.objective4.app.onlife.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import com.objective4.app.onlife.Fragments.Emoticons.FragmentAnimals;
import com.objective4.app.onlife.Fragments.Emoticons.FragmentHands;
import com.objective4.app.onlife.Fragments.Emoticons.FragmentSmiley;
import com.objective4.app.onlife.R;

public class AdapterFragmentEmoticon extends FragmentStatePagerAdapter {
    private Context context;
    private int[] imageResId = {R.drawable.smiley19,R.drawable.a23,R.drawable.hand_9};

    public AdapterFragmentEmoticon(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Drawable image = ContextCompat.getDrawable(context, imageResId[position]);
        image.setBounds(0, 0, 45, 45);
        SpannableString sb = new SpannableString(" ");
        ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0 : return new FragmentSmiley();
            case 1 : return new FragmentAnimals();
            case 2 : return new FragmentHands();
            default: return null;
        }
    }

    @Override
    public int getCount() {
        return imageResId.length;
    }
}

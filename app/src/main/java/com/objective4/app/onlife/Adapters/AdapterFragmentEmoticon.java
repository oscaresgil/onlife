package com.objective4.app.onlife.Adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.objective4.app.onlife.Fragments.Emoticons.FragmentAnimals;
import com.objective4.app.onlife.Fragments.Emoticons.FragmentSmiley;
import com.objective4.app.onlife.R;

public class AdapterFragmentEmoticon extends FragmentStatePagerAdapter {
    private Context context;

    public AdapterFragmentEmoticon(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0 : return new FragmentSmiley();
            case 1 : return new FragmentAnimals();
            default: return null;
        }
    }

    @Override
    public int getCount() {
        return context.getResources().getInteger(R.integer.fragment_numbers_emoticons);
    }
}

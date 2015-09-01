package com.example.henzer.socialize.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.henzer.socialize.Fragments.FragmentContacts;
import com.example.henzer.socialize.Fragments.FragmentGroups;
import com.example.henzer.socialize.Models.ModelSessionData;
import com.example.henzer.socialize.R;

public class AdapterFragmentPager extends FragmentPagerAdapter {
    final int PAGE_COUNT = 2;
    private String tabTitles[];

    public AdapterFragmentPager(FragmentManager fm, Context context) {
        super(fm);
        tabTitles = new String[]{context.getResources().getString(R.string.tab_contacts),context.getResources().getString(R.string.tab_groups)};
    }

    @Override public int getCount() {
        return PAGE_COUNT;
    }

    @Override public Fragment getItem(int position) {
        if (position==0) {
            return new FragmentContacts();
        }
        else {
            return new FragmentGroups();
        }
    }

    @Override public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}

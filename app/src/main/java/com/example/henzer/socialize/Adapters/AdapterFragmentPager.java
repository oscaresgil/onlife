package com.example.henzer.socialize.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.henzer.socialize.Fragments.FragmentContacts;
import com.example.henzer.socialize.Fragments.FragmentGroups;
import com.example.henzer.socialize.Models.SessionData;
import com.example.henzer.socialize.R;

public class AdapterFragmentPager extends FragmentPagerAdapter {
    final int PAGE_COUNT = 2;
    private String tabTitles[];
    private SessionData sessionData;

    public AdapterFragmentPager(FragmentManager fm, Context context) {
        super(fm);
        tabTitles = new String[]{context.getResources().getString(R.string.tab_contacts),context.getResources().getString(R.string.tab_groups)};
    }

    @Override public int getCount() {
        return PAGE_COUNT;
    }

    @Override public Fragment getItem(int position) {
        Bundle arguments = new Bundle();
        arguments.putSerializable("data", sessionData);
        if (position==0) {
            return FragmentContacts.newInstance(arguments);
        }
        else {
            return FragmentGroups.newInstance(arguments);
        }
    }

    @Override public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    public void setSessionData(SessionData sessionData) {
        this.sessionData = sessionData;
    }
}

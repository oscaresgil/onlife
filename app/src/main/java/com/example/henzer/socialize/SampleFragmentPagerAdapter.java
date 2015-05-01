package com.example.henzer.socialize;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Henzer on 25/04/2015.
 */
public class SampleFragmentPagerAdapter extends FragmentPagerAdapter {
    private SessionData sessionData;

    public SessionData getSessionData() {
        return sessionData;
    }

    public void setSessionData(SessionData sessionData) {
        this.sessionData = sessionData;
    }

    final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[]{"Contactos", "Grupos", "Favoritos"};
    private Context context;

    public SampleFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        if (position==0) {
            Bundle arguments = new Bundle();
            arguments.putSerializable("data", sessionData);
            return ContactsFragment.newInstance(arguments);
        }
        else{
            return new Fragment();
        }
        //return PageFragment.newInstance(position + 1);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}

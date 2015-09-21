package com.example.henzer.socialize.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ViewGroup;

import com.example.henzer.socialize.Fragments.FragmentContacts;
import com.example.henzer.socialize.Fragments.FragmentGroups;
import com.example.henzer.socialize.Models.ModelSessionData;
import com.example.henzer.socialize.R;

import java.util.HashMap;
import java.util.Map;

public class AdapterFragmentPager extends FragmentPagerAdapter{
    public static final String TAG = "AdapterFragmentPager";
    private Context context;
    private FragmentManager fm;
    private Map<Integer,String> fragmentTags;

    private final int PAGE_COUNT = 2;
    private String tabTitles[];

    public AdapterFragmentPager(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
        this.fm = fm;
        fragmentTags = new HashMap<>();
        tabTitles = new String[]{context.getResources().getString(R.string.tab_contacts),context.getResources().getString(R.string.tab_groups)};
    }

    @Override public int getCount() {
        return PAGE_COUNT;
    }

    @Override public Fragment getItem(int position) {
        Log.i(TAG, "getItem()");
        if (position==0) {
            return Fragment.instantiate(context,FragmentContacts.class.getName(),null);
            //return new FragmentContacts();
        }
        else {
            return Fragment.instantiate(context,FragmentGroups.class.getName(),null);
            //return new FragmentGroups();
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object object = super.instantiateItem(container,position);
        if (object instanceof Fragment){
            Fragment f = (Fragment) object;
            String tag = f.getTag();
            fragmentTags.put(position,tag);
        }
        return object;
    }

    public Fragment getFragmentPosition(int position){
        String tag = fragmentTags.get(position);
        if (tag==null){
            return null;
        }
        return fm.findFragmentByTag(tag);
    }

    @Override public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}

package com.objective4.app.onlife.Adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.objective4.app.onlife.Fragments.FragmentContacts;
import com.objective4.app.onlife.Fragments.FragmentGroups;
import com.objective4.app.onlife.R;

import java.util.HashMap;
import java.util.Map;

public class AdapterFragmentPager extends FragmentPagerAdapter{
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
        if (position==0) {
            return Fragment.instantiate(context,FragmentContacts.class.getName(),null);
        }
        else {
            return Fragment.instantiate(context,FragmentGroups.class.getName(),null);
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

    @Override public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}

package com.objective4.app.onlife.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import com.objective4.app.onlife.Fragments.Onboarding.FragmentOnboarding;
import com.objective4.app.onlife.Fragments.Onboarding.FragmentOnboarding2;
import com.objective4.app.onlife.Fragments.Onboarding.FragmentOnboarding3;
import com.objective4.app.onlife.Fragments.Onboarding.FragmentOnboarding4;
import com.objective4.app.onlife.Fragments.Onboarding.FragmentOnboarding5;
import com.objective4.app.onlife.R;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

public class ActivityOnboarding extends FragmentActivity {
    private ViewPager pager;
    private SmartTabLayout indicator;
    private Button skip;
    private Button next;

    public ActivityOnboarding() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        pager = (ViewPager)findViewById(R.id.pager);
        indicator = (SmartTabLayout)findViewById(R.id.indicator);
        skip = (Button)findViewById(R.id.skip);
        next = (Button)findViewById(R.id.next);

        FragmentStatePagerAdapter adapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public android.support.v4.app.Fragment getItem(int position) {
                switch (position) {
                    case 0 : return new FragmentOnboarding();
                    case 1 : return new FragmentOnboarding2();
                    case 2 : return new FragmentOnboarding3();
                    case 3 : return new FragmentOnboarding4();
                    case 4 : return new FragmentOnboarding5();
                    default: return null;
                }
            }

            @Override
            public int getCount() {
                return 5;
            }
        };

        pager.setAdapter(adapter);
        indicator.setViewPager(pager);

    }

    @Override
    protected void onResume() {
        super.onResume();

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishOnboarding();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pager.getCurrentItem() == 4) { // The last screen
                    SharedPreferences sharedPreferences = getSharedPreferences(ActivityMain.MyPREFERENCES, Context.MODE_PRIVATE);
                    if (sharedPreferences.getBoolean("onboarding_complete",false)){
                        finish();
                    }else {
                        finishOnboarding();
                    }
                } else {
                    pager.setCurrentItem(
                            pager.getCurrentItem() + 1,
                            true
                    );
                }
            }
        });

        indicator.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if(position == 4){
                    skip.setVisibility(View.GONE);
                    next.setText(R.string.done);
                } else {
                    skip.setVisibility(View.VISIBLE);
                    next.setText(R.string.next);
                }
            }
        });



    }

    private void finishOnboarding() {

        // Set onboarding_complete to true
        getSharedPreferences(ActivityMain.MyPREFERENCES, Context.MODE_PRIVATE).edit()
                .putBoolean("onboarding_complete",true).commit();

        if (!getSharedPreferences(ActivityMain.MyPREFERENCES, Context.MODE_PRIVATE).getBoolean("session",false)){
            // Launch the main Activity, called MainActivity
            Intent main = new Intent(this, ActivityMain.class);
            startActivity(main);

        }
        // Close the ActivityOnboarding
        finish();
    }




}

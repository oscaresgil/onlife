package com.example.henzer.socialize.Activities;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import com.example.henzer.socialize.Fragments.Onboarding.FragmentOnboarding;
import com.example.henzer.socialize.Fragments.Onboarding.FragmentOnboarding2;
import com.example.henzer.socialize.Fragments.Onboarding.FragmentOnboarding3;
import com.example.henzer.socialize.Fragments.Onboarding.FragmentOnboarding4;
import com.example.henzer.socialize.R;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

/**
 * Created by fer on 08/09/2015.
 */
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
                    default: return null;
                }
            }

            @Override
            public int getCount() {
                return 4;
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
                if(pager.getCurrentItem() == 3) { // The last screen
                    finishOnboarding();
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
                if(position == 3){
                    skip.setVisibility(View.GONE);
                    next.setText("DONE");
                } else {
                    skip.setVisibility(View.VISIBLE);
                    next.setText("NEXT");
                }
            }
        });



    }

    private void finishOnboarding() {
        // Get the shared preferences
        SharedPreferences preferences =
                getSharedPreferences("my_preferences", MODE_PRIVATE);

        // Set onboarding_complete to true
        preferences.edit()
                .putBoolean("onboarding_complete",true).apply();

        // Launch the main Activity, called MainActivity
        Intent main = new Intent(this, ActivityMain.class);
        startActivity(main);

        // Close the ActivityOnboarding
        finish();
    }




}

package com.example.henzer.socialize.Fragments.Onboarding;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.henzer.socialize.R;

/**
 * Created by fer on 08/09/2015.
 */

public class FragmentOnboarding2 extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle s) {

        return inflater.inflate(
                R.layout.onboarding_screen2,
                container,
                false
        );

    }
}
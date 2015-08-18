package com.example.henzer.socialize.Listeners;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

import com.example.henzer.socialize.R;

public class TextWatcherListener implements TextWatcher {
    private int maximumChars = 30, actualChar = 0;
    private TextView maxCharsView;
    private Context context;

    public TextWatcherListener(Context context, TextView maxCharsView) {
        this.maxCharsView = maxCharsView;
        this.context = context;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        actualChar = s.length();
        if (actualChar > 30) {
            maxCharsView.setTextColor(context.getResources().getColor(R.color.red));
            maxCharsView.setText(actualChar + "/" + maximumChars);
        } else {
            maxCharsView.setTextColor(context.getResources().getColor(R.color.black));
            maxCharsView.setText(actualChar + "/" + maximumChars);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
    }
}
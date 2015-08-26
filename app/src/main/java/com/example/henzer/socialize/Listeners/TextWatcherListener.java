package com.example.henzer.socialize.Listeners;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.TextView;

import com.example.henzer.socialize.R;
import com.rengwuxian.materialedittext.MaterialEditText;

public class TextWatcherListener implements TextWatcher {
    private int maximumChars = 30, actualChar = 0;
    private String text="";
    private TextView maxCharsView;
    private MaterialEditText messageTextView;
    private Context context;

    public TextWatcherListener(Context context, TextView maxCharsView, MaterialEditText messageTextView) {
        this.maxCharsView = maxCharsView;
        this.context = context;
        this.messageTextView = messageTextView;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        actualChar = s.length();
        if (actualChar > 30) {
            maxCharsView.setTextColor(context.getResources().getColor(R.color.red));
            messageTextView.setText(text);
            messageTextView.setSelection(messageTextView.getText().length());
        } else {
            if (actualChar == 30){
                maxCharsView.setTextColor(context.getResources().getColor(R.color.red));
            }else{
                maxCharsView.setTextColor(context.getResources().getColor(R.color.black));
            }
            text = s.toString();
        }
        maxCharsView.setText(actualChar + "/" + maximumChars);
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    public int getActualChar(){
        return actualChar;
    }
}
package com.example.henzer.socialize.Listeners;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.rengwuxian.materialedittext.MaterialEditText;

public class MessageFocusChangedListener implements View.OnFocusChangeListener{
    private Context context;
    private MaterialEditText messageTextView;

    public MessageFocusChangedListener(Context context, MaterialEditText messageTextView) {
        this.context = context;
        this.messageTextView = messageTextView;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(messageTextView.getWindowToken(), 0);
            messageTextView.setFocusable(false);
            messageTextView.clearFocus();
        }
    }
}

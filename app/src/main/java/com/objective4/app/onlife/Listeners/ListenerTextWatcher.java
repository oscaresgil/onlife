package com.objective4.app.onlife.Listeners;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

import com.objective4.app.onlife.R;
import com.rengwuxian.materialedittext.MaterialEditText;

public class ListenerTextWatcher implements TextWatcher {
    private int actualChar = 0;
    private String text="";
    private TextView maxCharsView;
    private MaterialEditText messageTextView;
    private Context context;

    public ListenerTextWatcher(Context context, TextView maxCharsView, MaterialEditText messageTextView) {
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
            maxCharsView.setTextColor(context.getResources().getColor(R.color.accent));
            messageTextView.setText(text);
            messageTextView.setSelection(messageTextView.getText().length());
        } else {
            if (actualChar == 30){
                maxCharsView.setTextColor(context.getResources().getColor(R.color.accent));
            }else{
                maxCharsView.setTextColor(context.getResources().getColor(R.color.black));
            }
            text = s.toString();
        }
        int maximumChars = 30;
        maxCharsView.setText(actualChar + "/" + maximumChars);
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    public int getActualChar(){
        return actualChar;
    }
}
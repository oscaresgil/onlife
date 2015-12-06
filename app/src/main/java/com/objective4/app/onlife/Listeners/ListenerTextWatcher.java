package com.objective4.app.onlife.Listeners;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import com.objective4.app.onlife.R;

public class ListenerTextWatcher implements TextWatcher {
    private int actualChar = 0;
    private String text="";
    private TextView maxCharsView;
    private EditText messageTextView;
    private Context context;

    public ListenerTextWatcher(Context context, TextView maxCharsView, EditText messageTextView) {
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
        if (actualChar > context.getResources().getInteger(R.integer.message_max_chars)) {
            maxCharsView.setTextColor(context.getResources().getColor(R.color.accent));
            messageTextView.setText(text);
            messageTextView.setSelection(messageTextView.getText().length());
        } else {
            if (actualChar == context.getResources().getInteger(R.integer.message_max_chars)){
                maxCharsView.setTextColor(context.getResources().getColor(R.color.accent));
            }else{
                maxCharsView.setTextColor(context.getResources().getColor(R.color.black));
            }
            text = s.toString();
        }
        int maximumChars = context.getResources().getInteger(R.integer.message_max_chars);
        maxCharsView.setText(String.format("%d/%d", actualChar, maximumChars));
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    public int getActualChar(){
        return actualChar;
    }
}
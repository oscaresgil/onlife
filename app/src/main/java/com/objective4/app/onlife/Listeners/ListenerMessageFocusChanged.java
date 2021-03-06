package com.objective4.app.onlife.Listeners;

import android.content.Context;
import android.view.View;

import com.rengwuxian.materialedittext.MaterialEditText;

import static com.objective4.app.onlife.Controller.StaticMethods.hideSoftKeyboard;

public class ListenerMessageFocusChanged implements View.OnFocusChangeListener{
    private Context context;
    private MaterialEditText messageTextView;

    public ListenerMessageFocusChanged(Context context, MaterialEditText messageTextView) {
        this.context = context;
        this.messageTextView = messageTextView;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            hideSoftKeyboard(context,messageTextView);
            messageTextView.setFocusable(false);
            messageTextView.clearFocus();
        }
    }
}

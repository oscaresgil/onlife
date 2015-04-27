package com.example.henzer.socialize;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity {

    public static final String MyPREFERENCES = "MyPrefs";
    public static final String name = "nameKey";
    public static final String pass = "passwordKey";
    SharedPreferences sharedpreferences;
    private EditText username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username = (EditText) findViewById(R.id.editText1);
        password = (EditText) findViewById(R.id.editText2);
    }

    @Override
    protected void onResume() {
        sharedpreferences = getSharedPreferences(MyPREFERENCES,
                Context.MODE_PRIVATE);
        if (sharedpreferences.contains(name)) {
            if (sharedpreferences.contains(pass)) {
                Intent i = new Intent(this,
                        HomeActivity.class);
                startActivity(i);
            }
        }
        super.onResume();
    }

    public void login(View view) {
        Editor editor = sharedpreferences.edit();
        String u = username.getText().toString();
        String p = password.getText().toString();
        editor.putString(name, u);
        editor.putString(pass, p);
        editor.commit();
        Intent i = new Intent(this, com.example.
                henzer.socialize.HomeActivity.class);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

}
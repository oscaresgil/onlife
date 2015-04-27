package com.example.henzer.socialize;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

import com.example.henzer.socialize.GCMClient.GCMHelper;

import java.io.IOException;

public class MainActivity extends Activity {
    // Este es el numero de proyecto para el Google Cloud Messaging (GCM). Este nunca cambia y es estatico.
    private static final String PROJECT_NUMBER = "194566212765";

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
        GetGCM();
        Intent i = new Intent(this, com.example.
                henzer.socialize.HomeActivity.class);
        startActivity(i);
    }

    private void GetGCM() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    GCMHelper gcmRegistrationHelper = new GCMHelper(getApplicationContext());
                    String gcmRegID = gcmRegistrationHelper.GCMRegister(PROJECT_NUMBER);
                    Log.i("GCM", gcmRegID);
                } catch (IOException e) {
                    msg = "Error : " + e.getMessage();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return msg;
            }

            /*
            * En este metodo se deberia de almacenar en la base de datos este numero de ID del dispositivo
             */
            @Override
            public void onPostExecute(String msg) {
            }
        }.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

}
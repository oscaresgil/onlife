package com.example.henzer.socialize;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.henzer.socialize.GCMClient.GCMHelper;

import java.io.IOException;


public class MainActivity extends ActionBarActivity {
    // Este es el numero de proyecto para el Google Cloud Messaging (GCM). Este nunca cambia y es estatico.
    private static final String PROJECT_NUMBER = "194566212765";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new SampleFragmentPagerAdapter(getSupportFragmentManager(),MainActivity.this));

        // Give the SlidingTabLayout the ViewPager
        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        // Center the tabs in the layout
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setViewPager(viewPager);

        /*
        * When the user logs in with fb or other social app; in our app, this will run..
        Button b = (Button) findViewById(R.id.<Button>);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetGCM();
            }
        });
        */



    }

    /*
    *   Este metodo se debera de llamar SOLO UNA VEZ, y es el momento cuando el usuario se linkea con correo de FB o de
    *   GMAIL o de otra red social. Solo con esto sabremos el numero de dispositivo que le pertenece a la persona
     */
    private void GetGCM() {
        new AsyncTask<Void,Void,String>(){
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try{
                    GCMHelper gcmRegistrationHelper = new GCMHelper(getApplicationContext());
                    String gcmRegID = gcmRegistrationHelper.GCMRegister(PROJECT_NUMBER);
                    Log.i("GCM", gcmRegID);
                }catch(IOException e){
                    msg = "Error : "+e.getMessage();
                }catch(Exception e){
                    e.printStackTrace();
                }
                return msg;
            }
            /*
            * En este metodo se deberia de almacenar en la base de datos este numero de ID del dispositivo
             */
            @Override
            public void onPostExecute(String msg){
                //tRegId.setText(msg+"\n");
            }
        }.execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

package com.example.henzer.socialize.GCMClient;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

public class GcmAndroidId extends Activity{
    // Este es el numero de proyecto. Este nunca cambia y es estatico.
    private static final String PROJECT_NUMBER = "194566212765";

    private GoogleCloudMessaging gcm;
    private String regId;
    /*
    *   Metodo que registra el dispositivo Android con Google Cloud Messaging, obteniendo el id del Android y lo deberia de almacenar dentro de la base de datos.
    *   Se utilizo el numero de proyecto para comunicarlo con GCM.
     */
    public GcmAndroidId(){}

    public void getRegId(){
        new AsyncTask<Void,Void,String>(){
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try{
                    if (gcm == null){
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }
                    regId = gcm.register(PROJECT_NUMBER);
                    msg = "Device registered, registration ID="+regId;
                    Log.i("GCM", msg);
                }catch(IOException e){
                    msg = "Error : "+e.getMessage();
                }
                return msg;
            }
            /*
            * En este paso deberia de almacenarse dentro de la base de datos.
            */
            @Override
            public void onPostExecute(String msg){
                //tRegId.setText(msg+"\n");
            }
        }.execute(null,null,null);
    }

    /*
    * Se obtiene el numero de registro que tiene este dispositivo
    public String getRegId(){
        return regId;
    }
    */

}

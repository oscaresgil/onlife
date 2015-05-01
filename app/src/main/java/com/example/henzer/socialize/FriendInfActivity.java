package com.example.henzer.socialize;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Boris on 01/05/2015.
 */
public class FriendInfActivity extends Activity {
    private UserData friend;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_information);
        Intent i = getIntent();
        friend = (UserData)i.getSerializableExtra("data");
        CircleImageView imageView = (CircleImageView) findViewById(R.id.avatar);
        try {
            imageView.setImageBitmap(new DownloadImageTask().execute(friend.getId()).get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        //Slidr.attach(this);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        Bitmap mIcon11;
        protected Bitmap doInBackground(String... urls) {
            try {
                String userID = urls[0];
                String urlStr = "https://graph.facebook.com/" + userID + "/picture?width=400&height=400";
                Bitmap img = null;

                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet(urlStr);
                HttpResponse response;
                try {
                    response = (HttpResponse)client.execute(request);
                    HttpEntity entity = response.getEntity();
                    BufferedHttpEntity bufferedEntity = new BufferedHttpEntity(entity);
                    InputStream inputStream = bufferedEntity.getContent();
                    img = BitmapFactory.decodeStream(inputStream);
                } catch (ClientProtocolException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                Log.i("IMAGE",img.toString());
                return img;
            }catch(Exception e){e.printStackTrace();}
            return mIcon11;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            Log.i("BITMAP Icon", bitmap.toString());
        }
    }

}

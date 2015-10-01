package com.example.henzer.socialize.Tasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.example.henzer.socialize.Activities.ActivityHome;
import com.example.henzer.socialize.Models.ModelPerson;
import com.example.henzer.socialize.R;

import net.soulwolf.widget.ratiolayout.widget.RatioImageView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;

import static com.example.henzer.socialize.Controller.StaticMethods.saveImage;

public class TaskSimpleImageDownload extends AsyncTask<ModelPerson,Void,Void> {
    public final static String TAG="TaskSimpleImageDownload";
    private Context context;
    private RatioImageView avatar;
    private int size;
    private Bitmap imageBitmap;
    private long startTime, stopTime;

    public TaskSimpleImageDownload(Context context, RatioImageView avatar, int size) {
        this.context = context;
        this.avatar = avatar;
        this.size = size;
        startTime = System.currentTimeMillis();
    }

    @Override
    protected Void doInBackground(ModelPerson... params) {
        Log.i(TAG,"InBackground()");
        ModelPerson p = params[0];
        Log.i(TAG,"FriendImage: "+p.toString());

        String urlStr = p.getPhoto()+"width="+size+"&height="+size;
        Log.i(TAG,"FriendUrlImage: "+urlStr);

        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(urlStr);
        HttpResponse response;
        try {
            response = client.execute(request);
            HttpEntity entity = response.getEntity();
            BufferedHttpEntity bufferedEntity = new BufferedHttpEntity(entity);
            InputStream inputStream = bufferedEntity.getContent();
            imageBitmap = BitmapFactory.decodeStream(inputStream);
            saveImage(context, p.getId()+"_"+size, imageBitmap);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Log.i(TAG,"OnPost()");
        if (size == context.getResources().getInteger(R.integer.adapter_contact_size_large)) avatar.setImageBitmap(imageBitmap);
        stopTime = System.currentTimeMillis();
        Log.i(TAG,"Time: "+(stopTime-startTime)/1000);
    }
}

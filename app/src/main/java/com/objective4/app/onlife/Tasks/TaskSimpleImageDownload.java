package com.objective4.app.onlife.Tasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.objective4.app.onlife.Models.ModelPerson;
import com.objective4.app.onlife.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;

import static com.objective4.app.onlife.Controller.StaticMethods.saveImage;

public class TaskSimpleImageDownload extends AsyncTask<ModelPerson,Void,Void> {
    private Context context;
    private ImageView avatar;
    private int size;
    private Bitmap imageBitmap;

    public TaskSimpleImageDownload(Context context, ImageView avatar, int size) {
        this.context = context;
        this.avatar = avatar;
        this.size = size;
    }

    @Override
    protected Void doInBackground(ModelPerson... params) {
        ModelPerson p = params[0];
        String urlStr = p.getPhoto()+"width="+size+"&height="+size;

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
        if (size == context.getResources().getInteger(R.integer.adapter_contact_size_little)) avatar.setImageBitmap(imageBitmap);
    }
}

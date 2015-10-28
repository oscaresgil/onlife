package com.objective4.app.onlife.Tasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.graphics.Palette;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.objective4.app.onlife.Adapters.AdapterBaseElements;
import com.objective4.app.onlife.BlockActivity.ActivityFriendBlock;
import com.objective4.app.onlife.Models.ModelPerson;
import com.objective4.app.onlife.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.ConnectException;

import static com.objective4.app.onlife.Controller.StaticMethods.saveImage;

public class TaskSimpleImageDownload extends AsyncTask<ModelPerson,Void,Bitmap> {
    public String data = "";
    private boolean connectionFailure=false;
    private Context context;
    private WeakReference<ImageView> imageViewReference;
    private int size;
    private Bitmap imageBitmap;
    private RoundCornerProgressBar progressBar;

    public TaskSimpleImageDownload(Context context, ImageView avatar, int size) {
        this.context = context;
        this.size = size;
        imageViewReference = new WeakReference<>(avatar);
    }

    public TaskSimpleImageDownload(Context context, ImageView avatar, int size, RoundCornerProgressBar progressBar) {
        this.context = context;
        this.size = size;
        imageViewReference = new WeakReference<>(avatar);
        this.progressBar = progressBar;
    }

    @Override
    protected Bitmap doInBackground(ModelPerson... params) {
        ModelPerson p = params[0];
        data = p.getId();
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
            return imageBitmap;
        } catch(ConnectException e){
            connectionFailure = true;
        } catch (IOException e) {
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (!connectionFailure && bitmap!=null) {
            if (context instanceof ActivityFriendBlock) {
                if (progressBar!=null){
                    Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                        @Override
                        public void onGenerated(Palette palette) {
                            if (palette.getDarkVibrantSwatch() != null) progressBar.setProgressBackgroundColor(palette.getDarkVibrantSwatch().getRgb());
                        }
                    });
                }
                ImageView imageView = imageViewReference.get();
                Animation myFadeInAnimation = AnimationUtils.loadAnimation(context, R.anim.fadein);
                imageView.startAnimation(myFadeInAnimation);
                imageView.setImageBitmap(bitmap);
            } else {
                if (isCancelled()) {
                    bitmap = null;
                }
                if (imageViewReference != null && bitmap != null) {
                    final ImageView imageView = imageViewReference.get();
                    final TaskSimpleImageDownload bitmapWorkerTask =
                            AdapterBaseElements.AsyncDrawable.getBitmapWorkerTask(imageView);
                    if (this == bitmapWorkerTask && imageView != null) {
                        Animation myFadeInAnimation = AnimationUtils.loadAnimation(context, R.anim.fadein);
                        imageView.startAnimation(myFadeInAnimation);
                        imageView.setImageBitmap(bitmap);
                    }
                }
            }
        }
    }
}

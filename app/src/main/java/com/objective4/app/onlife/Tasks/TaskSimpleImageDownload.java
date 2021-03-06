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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import static com.objective4.app.onlife.Controller.StaticMethods.saveImage;

public class TaskSimpleImageDownload extends AsyncTask<ModelPerson,Void,Bitmap> {
    private Context context;
    private RoundCornerProgressBar progressBar;
    private WeakReference<ImageView> imageViewReference;
    private String data = "";
    private int size;

    public TaskSimpleImageDownload(Context context, ImageView avatar, int size) {
        this.context = context;
        this.size = size;
        imageViewReference = new WeakReference<>(avatar);
    }

    public TaskSimpleImageDownload(Context context, ImageView avatar, int size, RoundCornerProgressBar progressBar) {
        this.context = context;
        this.size = size;
        this.progressBar = progressBar;
        imageViewReference = new WeakReference<>(avatar);
    }

    @Override
    protected Bitmap doInBackground(ModelPerson... params) {
        ModelPerson p = params[0];
        data = p.getId();

        String urlStr = "https://graph.facebook.com/" + p.getId() + "/picture?" + "width=" + size + "&height=" + size;

        try {
            Bitmap imageBitmap = BitmapFactory.decodeStream(new URL(urlStr).openConnection().getInputStream());
            saveImage(context, p.getId() + "_" + size, imageBitmap);
            return imageBitmap;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap!=null) {
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
                    ImageView imageView = imageViewReference.get();
                    TaskSimpleImageDownload bitmapWorkerTask = AdapterBaseElements.AsyncDrawable.getBitmapWorkerTask(imageView);
                    if (this == bitmapWorkerTask) {
                        Animation myFadeInAnimation = AnimationUtils.loadAnimation(context, R.anim.fadein);
                        imageView.startAnimation(myFadeInAnimation);
                        imageView.setImageBitmap(bitmap);
                    }
                }
            }
        }
    }

    public String getData() {
        return data;
    }
}

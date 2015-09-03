package com.example.henzer.socialize.Tasks;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.henzer.socialize.Adapters.AdapterContact;
import com.example.henzer.socialize.Fragments.FragmentContacts;
import com.example.henzer.socialize.Models.ModelPerson;
import com.example.henzer.socialize.R;
import com.kenny.snackbar.SnackBar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.example.henzer.socialize.Controller.StaticMethods.saveImage;

public class TaskImageDownload extends AsyncTask<Void, Void, Void> {
    public final static String TAG = "TaskImageDownload";
    private Context context;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private AdapterContact adapter;
    private MaterialDialog materialDialog;
    private List<ModelPerson> friends;
    private boolean isMaterialDialog;

    public TaskImageDownload(Context context, SwipeRefreshLayout mSwipeRefreshLayout, boolean isMaterialDialog, AdapterContact adapter, List<ModelPerson> friends){
        Log.e(TAG,"OnConstructor");
        this.context = context;
        this.mSwipeRefreshLayout = mSwipeRefreshLayout;
        this.adapter = adapter;
        this.isMaterialDialog = isMaterialDialog;
        this.friends = friends;
    }

    public TaskImageDownload(Context context, List<ModelPerson> friends, boolean isMaterialDialog, AdapterContact adapter) {
        this.context = context;
        this.friends = friends;
        this.isMaterialDialog = isMaterialDialog;
        this.adapter = adapter;
    }

    @Override protected void onPreExecute() {
        if (isMaterialDialog){
            materialDialog = new MaterialDialog.Builder(context)
                    .title(R.string.dialog_please_wait)
                    .content(R.string.getting_images)
                    .progress(true,0)
                    .widgetColorRes(R.color.orange_light)
                    .cancelable(false)
                    .show();
        }
        else{
            mSwipeRefreshLayout.setRefreshing(true);
        }
    }

    protected Void doInBackground(Void ... urls) {
        try {
            Log.e(TAG,"StartDoInBackground");
            for (ModelPerson user: friends){
                String userID = user.getId();
                user.setName(user.getName());
                String urlStr = "https://graph.facebook.com/" + userID + "/picture?width=700&height=700";
                Log.e(TAG,"URL: "+urlStr);
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet(urlStr);
                HttpResponse response;
                try {
                    response = client.execute(request);
                    HttpEntity entity = response.getEntity();
                    BufferedHttpEntity bufferedEntity = new BufferedHttpEntity(entity);
                    InputStream inputStream = bufferedEntity.getContent();
                    saveImage(context, userID, BitmapFactory.decodeStream(inputStream));
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }catch(Exception e){e.printStackTrace();}
        return null;
    }

    @Override protected void onPostExecute(Void aVoid) {
        if (isMaterialDialog){
            if (materialDialog.isShowing()) {
                materialDialog.dismiss();
            }
        }
        else{
            mSwipeRefreshLayout.setRefreshing(false);

            List<ModelPerson> temp = new ArrayList<>();
            temp.addAll(friends);
            friends = new ArrayList<>();
            friends.addAll(temp);
            adapter.notifyDataSetChanged();
            SnackBar.show((Activity)context, R.string.toast_contacts_refreshed);
        }
    }
}

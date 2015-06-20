package com.example.henzer.socialize.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.henzer.socialize.ContactsFragment;
import com.example.henzer.socialize.Models.Person;
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

import static com.example.henzer.socialize.Adapters.StaticMethods.saveImage;

public class DownloadImageTask extends AsyncTask<Boolean, Void, Void> {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Activity context;
    private ContactsAdapter adapter;
    private boolean isMaterialDialog;
    private MaterialDialog materialDialog;
    private List<Person> friends;
    private boolean typeUrl;

    public DownloadImageTask(Context context, SwipeRefreshLayout mSwipeRefreshLayout, boolean isMaterialDialog, ContactsAdapter adapter, List<Person> friends){
        this.context = (Activity) context;
        this.mSwipeRefreshLayout = mSwipeRefreshLayout;
        this.adapter = adapter;
        this.isMaterialDialog = isMaterialDialog;
        this.friends = friends;
    }

    @Override
    protected void onPreExecute() {
        if (isMaterialDialog){
            materialDialog = new MaterialDialog.Builder(context)
                    .title("Loading Friends..")
                    .content("Please wait..")
                    .progress(true,0)
                    .widgetColorRes(R.color.orange_light)
                    .cancelable(false)
                    .show();
        }
        else{
            mSwipeRefreshLayout.setRefreshing(true);
        }
    }

    protected Void doInBackground(Boolean ... urls) {
        try {
            typeUrl = !urls[0];
            for (Person user: friends){
                String userID = user.getId();
                user.setName(user.getName());
                String urlStr;
                if (urls[0]){
                    urlStr = "https://graph.facebook.com/" + userID + "/picture?width=700&height=700";
                }
                else{
                    urlStr = "https://graph.facebook.com/" + userID + "/picture?width=600&height=600";
                }
                Log.i("Actual BackGround Link",user.getName()+": "+urlStr);

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

    @Override
    protected void onPostExecute(Void aVoid) {
        if (isMaterialDialog){
            if (materialDialog.isShowing()) {
                materialDialog.dismiss();
            }
        }
        else{
            mSwipeRefreshLayout.setRefreshing(false);

            List<Person> temp = new ArrayList<>();
            temp.addAll(friends);
            friends = new ArrayList<>();
            friends.addAll(temp);
            adapter.notifyDataSetChanged();
            ContactsFragment.setTypeUrl(typeUrl);
            SnackBar.show(context, R.string.contacts_refreshed);
        }
    }


}

package com.example.henzer.socialize.Tasks;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.widget.GridView;

import com.example.henzer.socialize.Activities.ActivityHome;
import com.example.henzer.socialize.Activities.ActivityMain;
import com.example.henzer.socialize.Adapters.AdapterContact;
import com.example.henzer.socialize.Controller.JSONParser;
import com.example.henzer.socialize.Models.ModelPerson;
import com.example.henzer.socialize.Models.ModelSessionData;
import com.example.henzer.socialize.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kenny.snackbar.SnackBar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.example.henzer.socialize.Controller.StaticMethods.saveImage;

public class TaskRefreshImageDownload extends AsyncTask<String, Void, Void> {
    public final static String TAG = "TaskImageDownload";
    private Context context;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private GridView gridView;
    private ArrayList<ModelPerson> friends;
    private JSONParser jsonParser;
    private int size;

    public TaskRefreshImageDownload(Context context, SwipeRefreshLayout mSwipeRefreshLayout, GridView gridView){
        Log.e(TAG,"OnConstructor");
        this.context = context;
        this.mSwipeRefreshLayout = mSwipeRefreshLayout;
        this.gridView = gridView;
        size = context.getResources().getInteger(R.integer.adapter_contact_size_little);
        jsonParser = new JSONParser();
    }

    @Override protected void onPreExecute() {
        mSwipeRefreshLayout.setRefreshing(true);
    }

    protected Void doInBackground(String ... params) {

        String id = params[0];
        List<NameValuePair> p = new ArrayList<>();
        p.add(new BasicNameValuePair("tag", "getFriends"));
        p.add(new BasicNameValuePair("myId", id));
        JSONObject jsonFriends = null;
        try {
            jsonFriends = jsonParser.makeHttpRequest(ActivityMain.SERVER_URL, "POST", p);
            Gson gson = new Gson();
            friends = gson.fromJson(jsonFriends.getString("friends"), (new TypeToken<ArrayList<ModelPerson>>() {
            }.getType()));
            for (ModelPerson user : friends) {
                String userID = user.getId();
                String urlStr = user.getPhoto() + "width=" + size + "&height=" + size;
                Log.e(TAG, "URL: " + urlStr);
                Log.e(TAG, "URLSize: "+urlStr.length());
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet(urlStr);
                HttpResponse response;
                response = client.execute(request);
                HttpEntity entity = response.getEntity();
                BufferedHttpEntity bufferedEntity = new BufferedHttpEntity(entity);
                InputStream inputStream = bufferedEntity.getContent();

                saveImage(context, userID, BitmapFactory.decodeStream(inputStream));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override protected void onPostExecute(Void aVoid) {
        mSwipeRefreshLayout.setRefreshing(false);

        Collections.sort(friends, new Comparator<ModelPerson>() {
            @Override
            public int compare(ModelPerson modelPerson1, ModelPerson modelPerson2) {
                return modelPerson1.getName().compareTo(modelPerson2.getName());
            }
        });

        Log.i(TAG,"FriendsOnPost: "+friends.toString());

        ModelSessionData.getInstance().setFriends(friends);
        AdapterContact adapter = new AdapterContact(context,R.layout.layout_contact,friends);
        ((ActivityHome)context).setAdapterContact(adapter);
        gridView.setAdapter(adapter);

        SnackBar.show((Activity)context, R.string.toast_contacts_refreshed);
    }
}

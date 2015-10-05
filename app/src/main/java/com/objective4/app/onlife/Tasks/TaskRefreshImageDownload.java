package com.objective4.app.onlife.Tasks;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.ListView;

import com.objective4.app.onlife.Activities.ActivityHome;
import com.objective4.app.onlife.Activities.ActivityMain;
import com.objective4.app.onlife.Adapters.AdapterContact;
import com.objective4.app.onlife.Controller.JSONParser;
import com.objective4.app.onlife.Models.ModelPerson;
import com.objective4.app.onlife.Models.ModelSessionData;
import com.objective4.app.onlife.R;
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

import static com.objective4.app.onlife.Controller.StaticMethods.saveImage;

public class TaskRefreshImageDownload extends AsyncTask<String, Void, Void> {
    private Context context;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ListView listView;
    private ArrayList<ModelPerson> friends;
    private JSONParser jsonParser;
    private int[] size;

    public TaskRefreshImageDownload(Context context, SwipeRefreshLayout mSwipeRefreshLayout, ListView listView){
        this.context = context;
        this.mSwipeRefreshLayout = mSwipeRefreshLayout;
        this.listView = listView;
        size = new int[]{context.getResources().getInteger(R.integer.adapter_contact_size_little),context.getResources().getInteger(R.integer.adapter_contact_size_large)};
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
                for (int i=0; i<context.getResources().getInteger(R.integer.adapter_contact_size_size); i++){
                    String urlStr = user.getPhoto() + "width=" + size[i] + "&height=" + size[i];
                    HttpClient client = new DefaultHttpClient();
                    HttpGet request = new HttpGet(urlStr);
                    HttpResponse response;
                    response = client.execute(request);
                    HttpEntity entity = response.getEntity();
                    BufferedHttpEntity bufferedEntity = new BufferedHttpEntity(entity);
                    InputStream inputStream = bufferedEntity.getContent();

                    saveImage(context, userID+"_"+size[i], BitmapFactory.decodeStream(inputStream));
                }
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

        ModelSessionData.getInstance().setFriends(friends);
        AdapterContact adapter = new AdapterContact(context,R.layout.layout_contact,friends);
        ((ActivityHome)context).setAdapterContact(adapter);
        listView.setAdapter(adapter);

        SnackBar.show((Activity)context, R.string.toast_contacts_refreshed);
    }
}

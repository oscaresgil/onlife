package com.example.henzer.socialize.Tasks;

import android.content.Context;
import android.util.Log;

import com.example.henzer.socialize.Activities.ActivityMain;
import com.example.henzer.socialize.Activities.ActivitySelectContacts;
import com.example.henzer.socialize.Models.ModelPerson;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.example.henzer.socialize.Controller.StaticMethods.deleteAccent;

public class TaskFacebookFriendRequest implements GraphRequest.Callback {
    private Context context;
    private String TAG;
    private List<ModelPerson> friends;

    public TaskFacebookFriendRequest(Context context, String TAG){
        this.context = context;
        this.TAG = TAG;
        friends = new ArrayList<>();
    }

    @Override public void onCompleted(GraphResponse response) {
        try {
            JSONObject objectResponse = response.getJSONObject();
            JSONArray objectData = (JSONArray) objectResponse.get("data");
            String [] ids = new String[objectData.length()];
            Log.i("DATA", objectData.toString());
            for (int i=0; i<objectData.length(); i++){
                JSONObject objectUser = (JSONObject) objectData.get(i);
                String id = (String) objectUser.get("id");
                ids[i] = id;
                String name = (String) objectUser.get("name");

                // http://stackoverflow.com/questions/5841710/get-user-image-from-facebook-graph-api
                // http://stackoverflow.com/questions/23559736/android-skimagedecoderfactory-returned-null-error
                String path = "https://graph.facebook.com/" + id + "/picture?width=900&height=900";
                URL pathURL = new URL(path);

                Log.i("Friend "+i,id+" = "+ deleteAccent(name));
                Log.i("Friend URL "+i,path.toString());

                ModelPerson contact = new ModelPerson(id, null, deleteAccent(name), pathURL.toString(), "A");
                //Hay que eliminarlo luego
                contact.setHomeSelected(true);
                friends.add(contact);
            }

            new TaskImageDownload(context,null,true,null,friends).execute(true);

            Collections.sort(friends, new Comparator<ModelPerson>() {
                @Override
                public int compare(ModelPerson modelPerson1, ModelPerson modelPerson2) {
                    return modelPerson1.getName().compareTo(modelPerson2.getName());
                }
            });


            Log.i("ACTUAL FRIENDS", friends.toString());

            if (TAG.equals("ActivityMain")){
                ActivityMain activityMain = (ActivityMain) context;
                activityMain.setFriends(friends);
                activityMain.GetGCM();
            }
            else if(TAG.equals("ActivitySelectContacts")){
                ActivitySelectContacts activitySelectContacts = (ActivitySelectContacts)context;
                activitySelectContacts.setAllFriends(friends);
                activitySelectContacts.setAdapterAndDecor();
            }

        }catch(Exception e){e.printStackTrace();}
    }
}

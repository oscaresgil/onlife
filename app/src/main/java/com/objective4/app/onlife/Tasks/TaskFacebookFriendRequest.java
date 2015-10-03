package com.objective4.app.onlife.Tasks;

import android.content.Context;

import com.objective4.app.onlife.Activities.ActivityMain;
import com.objective4.app.onlife.Models.ModelPerson;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.objective4.app.onlife.Controller.StaticMethods.deleteAccent;

public class TaskFacebookFriendRequest implements GraphRequest.Callback {
    private Context context;
    private String TAG;
    private List<ModelPerson> friends;
    private ModelPerson user;

    public TaskFacebookFriendRequest(Context context, String TAG, ModelPerson user){
        this.context = context;
        this.TAG = TAG;
        this.user = user;
        friends = new ArrayList<>();
    }

    @Override public void onCompleted(GraphResponse response) {
        try {
            JSONObject objectResponse = response.getJSONObject();
            JSONArray objectData = (JSONArray) objectResponse.get("data");
            ArrayList<String> ids =  new ArrayList<>();
            ids.add(user.getId());
            for (int i=0; i<objectData.length(); i++){
                JSONObject objectUser = (JSONObject) objectData.get(i);
                String id = (String) objectUser.get("id");
                ids.add(id);
                String name = (String) objectUser.get("name");

                // http://stackoverflow.com/questions/5841710/get-user-image-from-facebook-graph-api
                // http://stackoverflow.com/questions/23559736/android-skimagedecoderfactory-returned-null-error
                String path = "https://graph.facebook.com/" + id + "/picture?";
                ModelPerson contact = new ModelPerson(id, null, deleteAccent(name), path, "A");
                friends.add(contact);
            }

            Collections.sort(friends, new Comparator<ModelPerson>() {
                @Override
                public int compare(ModelPerson modelPerson1, ModelPerson modelPerson2) {
                    return modelPerson1.getName().compareTo(modelPerson2.getName());
                }
            });

            if (TAG.equals("ActivityMain")){
                ActivityMain activityMain = (ActivityMain) context;
                TaskSetFriends taskFriends = new TaskSetFriends(context);
                taskFriends.execute(ids);
                friends = new ArrayList<>();
                activityMain.gotoHome();
            }

        }catch(Exception e){e.printStackTrace();}
    }
}

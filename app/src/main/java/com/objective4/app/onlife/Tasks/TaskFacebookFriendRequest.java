package com.objective4.app.onlife.Tasks;

import android.content.Context;

import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.objective4.app.onlife.Models.ModelPerson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TaskFacebookFriendRequest implements GraphRequest.Callback {
    private Context context;
    private ModelPerson user;

    public TaskFacebookFriendRequest(Context context, ModelPerson user){
        this.context = context;
        this.user = user;
    }

    @Override public void onCompleted(GraphResponse response) {
        try {
            JSONObject objectResponse = response.getJSONObject();
            JSONArray objectData = (JSONArray) objectResponse.get("data");
            List<ModelPerson> friends = new ArrayList<>();
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
                ModelPerson contact = new ModelPerson(id, null, name, path, "A");
                friends.add(contact);
            }

            Collections.sort(friends, new Comparator<ModelPerson>() {
                @Override
                public int compare(ModelPerson modelPerson1, ModelPerson modelPerson2) {
                    return modelPerson1.getName().compareTo(modelPerson2.getName());
                }
            });

            new TaskSetFriends(context).execute(ids);

        }catch(Exception e){e.printStackTrace();}
    }
}

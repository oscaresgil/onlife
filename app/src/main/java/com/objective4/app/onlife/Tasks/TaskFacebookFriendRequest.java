package com.objective4.app.onlife.Tasks;

import android.content.Context;

import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.objective4.app.onlife.Models.ModelPerson;

import org.json.JSONArray;
import org.json.JSONObject;

public class TaskFacebookFriendRequest implements GraphRequest.Callback {
    private Context context;
    private ModelPerson user;

    public TaskFacebookFriendRequest(Context context, ModelPerson user){
        this.context = context;
        this.user = user;
    }

    @Override public void onCompleted(GraphResponse response) {
        try {
            JSONArray objectData = (JSONArray) response.getJSONObject().get("data");
            String[] idsArray = new String[objectData.length()+1];
            idsArray[0] = user.getId();

            for (int i=0; i<objectData.length(); i++){
                JSONObject objectUser = (JSONObject) objectData.get(i);
                String id = (String) objectUser.get("id");
                idsArray[i+1] = id;
            }

            new TaskSetFriends(context).execute(idsArray);

        }catch(Exception e){e.printStackTrace();}
    }
}

package com.example.henzer.socialize.Controller;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.henzer.socialize.Models.ModelGroup;
import com.example.henzer.socialize.Models.ModelPerson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class SessionData {
    private SharedPreferences preferences;
    private static SessionData instance;
    public static String TAG = "Session";

    public  static SessionData getInstance(){
        if(instance==null)
            instance = new SessionData(null);
        return instance;
    }

    public SessionData(SharedPreferences preferences){
        this.preferences = preferences;
    }


    public void saveInSession(List<ModelGroup> modelGroups) throws JSONException {
        SharedPreferences.Editor editor = preferences.edit();

        JSONObject mySession = new JSONObject(preferences.getString("session", "{}"));
        Log.e(TAG, mySession.toString());

        JSONArray myGroups = new JSONArray();

        for(ModelGroup modelGroup : modelGroups) {
            JSONObject obj = new JSONObject();
            obj.put("id", modelGroup.getId());
            obj.put("name", modelGroup.getName());
            obj.put("photo", modelGroup.getNameImage());
            obj.put("limit", modelGroup.getLimit());
            obj.put("state", modelGroup.getState());

            JSONArray arr = new JSONArray();
            for (ModelPerson p : modelGroup.getFriendsInGroup()) {
                JSONObject friend = new JSONObject();
                friend.put("id", p.getId());
                friend.put("id_phone", p.getId_phone());
                friend.put("name", p.getName());
                friend.put("photo", p.getPhoto());
                friend.put("state", p.getState());
                friend.put("background", p.getBackground());
                arr.put(friend);
            }
            obj.put("people", arr);
            myGroups.put(obj);
        }
        mySession.put("activity_groups", myGroups);

        Log.e(TAG, mySession.toString());
        editor.putString("session", mySession.toString());
        editor.commit();
    }
}
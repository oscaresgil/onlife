package com.example.henzer.socialize.Controller;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.henzer.socialize.Models.Group;
import com.example.henzer.socialize.Models.Person;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Henzer on 19/05/2015.
 */
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


    public void saveInSession(List<Group> groups) throws JSONException {
        SharedPreferences.Editor editor = preferences.edit();

        JSONObject mySession = new JSONObject(preferences.getString("session", "{}"));
        Log.e(TAG, mySession.toString());

        JSONArray myGroups = new JSONArray();

        for(Group group: groups) {
            JSONObject obj = new JSONObject();
            obj.put("id", group.getId());
            obj.put("name", group.getName());
            obj.put("photo", group.getNameImage());
            obj.put("limit", group.getLimit());
            obj.put("state", group.getState());

            JSONArray arr = new JSONArray();
            for (Person p : group.getFriendsInGroup()) {
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
        mySession.put("groups", myGroups);

        Log.e(TAG, mySession.toString());
        editor.putString("session", mySession.toString());
        editor.commit();
    }
}
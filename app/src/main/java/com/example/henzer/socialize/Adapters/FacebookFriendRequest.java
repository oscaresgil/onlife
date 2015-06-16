package com.example.henzer.socialize.Adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.example.henzer.socialize.Models.Person;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.example.henzer.socialize.Adapters.StaticMethods.eliminarTilde;

public class FacebookFriendRequest implements GraphRequest.Callback {

    private Context context;
    private Person userLogin;
    private List<Person> friends;
    private ContactsAdapter adapter;

    public FacebookFriendRequest(Context context, Person userLogin, List<Person> friends, ContactsAdapter adapter){
        this.context = context;
        this.userLogin = userLogin;
        this.friends = friends;
        this.adapter = adapter;
    }

    @Override
    public void onCompleted(GraphResponse response) {
        try {
            JSONObject objectResponse = response.getJSONObject();
            JSONArray objectData = (JSONArray) objectResponse.get("data");
            String[] ids = new String[objectData.length()];
            Log.i("DATA", objectData.toString());
            for (int i = 0; i < objectData.length(); i++) {
                JSONObject objectUser = (JSONObject) objectData.get(i);
                String id = (String) objectUser.get("id");
                if (!alreadyFriend(id)) {
                    ids[i] = id;
                    String name = (String) objectUser.get("name");

                    // http://stackoverflow.com/questions/5841710/get-user-image-from-facebook-graph-api
                    // http://stackoverflow.com/questions/23559736/android-skimagedecoderfactory-returned-null-error
                    String path = "https://graph.facebook.com/" + id + "/picture?width=900&height=900";
                    URL pathURL = new URL(path);

                    Log.i("Friend " + i, id + " = " + eliminarTilde(name));
                    Log.i("Friend URL " + i, path.toString());

                    Person contact = new Person(id, null, eliminarTilde(name), pathURL.toString(), "A");
                    contact.setDeleted(false);
                    friends.add(contact);
                }
                else{
                    getFriendById(id).setDeleted(false);
                }
            }

            for (int i=0; i<friends.size(); i++){
                Person f = friends.get(i);
                if (f.isDeleted()){
                    friends.remove(i);
                    i--;
                }
            }

            Log.i("Friends Refreshed",friends.toString());

            new DownloadImageTask(context, null, true, null).execute(ids);

            Collections.sort(friends, new Comparator<Person>() {
                @Override
                public int compare(Person person1, Person person2) {
                    return person1.getName().compareTo(person2.getName());
                }
            });
            Log.i("ACTUAL USER", userLogin.toString());
            Log.i("ACTUAL FRIENDS", friends.toString());

            adapter.notifyDataSetChanged();

            //GetGCM();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean alreadyFriend(String friendId){
        for (int i=0;i<friends.size(); i++){
            if (friends.get(i).getId().equals(friendId)){
                return true;
            }
        }
        return false;
    }

    public Person getFriendById(String friendId){
        for (int i=0;i<friends.size(); i++){
            if (friends.get(i).getId().equals(friendId)){
                return friends.get(i);
            }
        }
        return null;
    }

    public List<Person> getFriends(){
        return friends;
    }
}

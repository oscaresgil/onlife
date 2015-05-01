package com.example.henzer.socialize;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Boris on 30/04/2015.
 */
public class SessionData implements Serializable{
    private UserData user;
    private List<UserData> friends;

    public SessionData(UserData user, List<UserData> friends) {
        this.user = user;
        this.friends = friends;
    }

    public UserData getUser() {
        return user;
    }

    public void setUser(UserData user) {
        this.user = user;
    }

    public List<UserData> getFriends() {
        return friends;
    }

    public void setFriends(List<UserData> friends) {
        this.friends = friends;
    }

    @Override
    public String toString() {
        return "SessionData{" +
                "user=" + user +
                ", friends=" + friends +
                '}';
    }
}

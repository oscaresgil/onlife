package com.example.henzer.socialize;

import com.example.henzer.socialize.Models.Person;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Boris on 30/04/2015.
 */
public class SessionData implements Serializable{
    private Person user;
    private List<Person> friends;

    public SessionData(Person user, List<Person> friends) {
        this.user = user;
        this.friends = friends;
    }

    public Person getUser() {
        return user;
    }

    public void setUser(Person user) {
        this.user = user;
    }

    public List<Person> getFriends() {
        return friends;
    }

    public void setFriends(List<Person> friends) {
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

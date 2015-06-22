package com.example.henzer.socialize.Models;

import java.io.Serializable;
import java.util.List;

public class SessionData implements Serializable{
    private Person user;
    private List<Person> friends;
    private List<Group> groups;

    public SessionData(Person user, List<Person> friends, List<Group> groups) {
        this.user = user;
        this.friends = friends;
        this.groups = groups;
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

    public List<Group> getGroups() {
        return groups;
    }

    @Override public String toString() {
        return "SessionData{" +
                "user=" + user +
                ", friends=" + friends +
                '}';
    }
}
package com.example.henzer.socialize.Models;

import java.io.Serializable;
import java.util.List;

public class Group implements Serializable {
    private int id;
    private String name;
    private List<Person> friendsInGroup;
    private String nameImage;
    private int limit;
    private String state;

    public Group(int id, String name, List<Person> friendsInGroup, String nameImage, int limit, String state) {
        this.id = id;
        this.name = name;
        this.friendsInGroup = friendsInGroup;
        this.nameImage = nameImage;
        this.limit = limit;
        this.state = state;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLimit() {
        return limit;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Person> getFriendsInGroup() {
        return friendsInGroup;
    }

    public String getNameImage() {
        return nameImage;
    }

    @Override public String toString() {
        return "Group{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", friendsInGroup=" + friendsInGroup +
                ", nameImage='" + nameImage + '\'' +
                ", limit=" + limit +
                ", state='" + state + '\'' +
                '}';
    }
}
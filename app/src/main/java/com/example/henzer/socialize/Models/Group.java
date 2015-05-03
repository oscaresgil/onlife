package com.example.henzer.socialize.Models;

import java.util.List;

/**
 * Created by Boris on 03/05/2015.
 */
public class Group {
    private String name;
    private List<Person> friendsInGroup;
    private String nameImage;

    public Group(String name, List<Person> friendsInGroup, String nameImage) {
        this.name = name;
        this.friendsInGroup = friendsInGroup;
        this.nameImage = nameImage;
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

    public void setFriendsInGroup(List<Person> friendsInGroup) {
        this.friendsInGroup = friendsInGroup;
    }

    public String getNameImage() {
        return nameImage;
    }

    public void setNameImage(String nameImage) {
        this.nameImage = nameImage;
    }

    @Override
    public String toString() {
        return "Group{" +
                "name='" + name + '\'' +
                ", friendsInGroup=" + friendsInGroup +
                ", nameImage='" + nameImage + '\'' +
                '}';
    }
}

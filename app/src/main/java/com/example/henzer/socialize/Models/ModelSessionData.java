package com.example.henzer.socialize.Models;

import java.io.Serializable;
import java.util.List;

public class ModelSessionData implements Serializable{
    private ModelPerson user;
    private List<ModelPerson> friends;
    private List<ModelGroup> modelGroups;

    public ModelSessionData(ModelPerson user, List<ModelPerson> friends, List<ModelGroup> modelGroups) {
        this.user = user;
        this.friends = friends;
        this.modelGroups = modelGroups;
    }

    public ModelPerson getUser() {
        return user;
    }

    public void setUser(ModelPerson user) {
        this.user = user;
    }

    public List<ModelPerson> getFriends() {
        return friends;
    }

    public void setFriends(List<ModelPerson> friends) {
        this.friends = friends;
    }

    public void setModelGroups(List<ModelGroup> modelGroups) {
        this.modelGroups = modelGroups;
    }

    public List<ModelGroup> getModelGroups() {
        return modelGroups;
    }

    @Override public String toString() {
        return "SessionData{" +
                "user=" + user +
                ", friends=" + friends +
                '}';
    }
}
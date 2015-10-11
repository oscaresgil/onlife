package com.objective4.app.onlife.Models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ModelSessionData implements Serializable{
    private static ModelSessionData instance;
    private ModelPerson user;
    private List<ModelPerson> friends;
    private List<ModelGroup> modelGroups;

    public static void initInstance(ModelPerson user, List<ModelPerson> friends, List<ModelGroup> modelGroups) {
        instance = new ModelSessionData(user,friends,modelGroups);
    }

    public static ModelSessionData getInstance(){
        if (instance==null){
            instance = new ModelSessionData();
        }
        return instance;
    }

    public ModelSessionData() {
    }

    public ModelSessionData(ModelPerson user, List<ModelPerson> friends, List<ModelGroup> modelGroups) {
        this.user = user;
        this.friends = friends;
        this.modelGroups = modelGroups;
    }

    public ModelPerson getUser() {
        return user;
    }

    public List<ModelPerson> getFriends() {
        return friends;
    }

    public void setFriends(List<ModelPerson> friends) {
        this.friends = friends;
    }

    public void clear(){
        ModelPerson modelPerson = new ModelPerson();
        List<ModelPerson> friends = new ArrayList<>();
        List<ModelGroup> groups = new ArrayList<>();
        instance = new ModelSessionData(modelPerson,friends,groups);
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
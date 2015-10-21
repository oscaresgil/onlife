package com.objective4.app.onlife.Models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import static com.objective4.app.onlife.Controller.StaticMethods.setHashToList;

public class ModelSessionData implements Serializable{
    private static ModelSessionData instance;
    private ModelPerson user;
    private HashMap<String,ModelPerson> hashFriends;
    private List<ModelGroup> modelGroups;

    public static void initInstance(ModelPerson user, HashMap<String,ModelPerson> friends, List<ModelGroup> modelGroups) {
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

    public ModelSessionData(ModelPerson user, HashMap<String,ModelPerson> hashFriends, List<ModelGroup> modelGroups) {
        this.user = user;
        this.hashFriends = hashFriends;
        this.modelGroups = modelGroups;
    }

    public ModelPerson getUser() {
        return user;
    }

    public HashMap<String,ModelPerson> getFriends() {
        return hashFriends;
    }

    public void setFriends(HashMap<String,ModelPerson> friends) {
        this.hashFriends = friends;
    }

    public void clear(){
        ModelPerson modelPerson = new ModelPerson();
        HashMap<String,ModelPerson> friends = new HashMap<>();
        List<ModelGroup> groups = new ArrayList<>();
        instance = new ModelSessionData(modelPerson,friends,groups);
    }

    public List<ModelGroup> getModelGroups() {
        return modelGroups;
    }

    @Override public String toString() {
        return "SessionData{" +
                "user=" + user +
                ", friends=" + hashFriends.toString() +
                '}';
    }
}
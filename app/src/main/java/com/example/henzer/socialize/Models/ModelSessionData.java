package com.example.henzer.socialize.Models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

    public boolean addUser(ModelPerson user){
        for (ModelPerson f: friends){
            if (f.getId().equals(user.getId())){
                return false;
            }
        }
        friends.add(user);
        Collections.sort(friends, new Comparator<ModelPerson>() {
            @Override
            public int compare(ModelPerson modelPerson1, ModelPerson modelPerson2) {
                return modelPerson1.getName().compareTo(modelPerson2.getName());
            }
        });
        return true;
    }

    public void removeUser(String id){
        for (int i=0; i<friends.size(); i++){
            if (id.equals(friends.get(i).getId())){
                friends.remove(i);
                break;
            }
        }
        for (int j = 0; j<modelGroups.size(); j++){
            ModelGroup g = modelGroups.get(j);
            for (int i=0; i<g.getFriendsInGroup().size(); i++){
                ModelPerson f = g.getFriendsInGroup().get(i);
                if (f.getId().equals(id)){
                    g.getFriendsInGroup().remove(i);
                    if (g.getFriendsInGroup().isEmpty()){
                        modelGroups.remove(j);
                    }
                    break;
                }
            }
        }
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
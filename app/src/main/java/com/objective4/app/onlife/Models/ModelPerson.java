package com.objective4.app.onlife.Models;

import java.io.Serializable;

public class ModelPerson implements Serializable{
    private String id;
    private String name;
    private String state;

    private boolean selected;
    private boolean refreshImage,refreshImageBig;
    private long lastBlockedTime;

    public ModelPerson() {
    }

    public ModelPerson(String id, String name) {
        this.id = id;
        this.name = name;
        selected = false;
        lastBlockedTime = 0;
        refreshImage = true;
        refreshImageBig = true;
    }

    public ModelPerson(String id, String name, String state) {
        this.id = id;
        this.name = name;
        this.state = state;
    }

    public ModelPerson(String id, String name, boolean selected) {
        this.id = id;
        this.name = name;
        this.selected = selected;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public long getLastBlockedTime() {
        return lastBlockedTime;
    }

    public void setLastBlockedTime(long lastBlockedTime) {
        this.lastBlockedTime = lastBlockedTime;
    }

    public boolean refreshImage() {
        return refreshImage;
    }

    public void setRefreshImage(boolean refreshImage) {
        this.refreshImage = refreshImage;
    }

    public boolean refreshImageBig() {
        return refreshImageBig;
    }

    public void setRefreshImageBig(boolean refreshImageBig) {
        this.refreshImageBig = refreshImageBig;
    }

    @Override
    public String toString() {
        return "ModelPerson{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", state='" + state + '\'' +
                ", selected=" + selected +
                ", refreshImage=" + refreshImage +
                ", refreshImageBig=" + refreshImageBig +
                ", lastBlockedTime=" + lastBlockedTime +
                '}';
    }
}
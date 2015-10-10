package com.objective4.app.onlife.Models;

import java.io.Serializable;

public class ModelPerson implements Serializable{
    private String id;
    private String id_phone;
    private String name;
    private String photo;
    private String state;
    private int background;
    private boolean selected;
    private boolean refreshImage,refreshImageBig;
    private long lastBlockedTime;

    public ModelPerson() {
    }

    public ModelPerson(String id, String id_phone, String name, String photo, String state) {
        this.id = id;
        this.id_phone = id_phone;
        this.name = name;
        this.photo = photo;
        this.state = state;
        selected = false;
        lastBlockedTime = 0;
        refreshImage = true;
        refreshImageBig = true;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId_phone() {
        return id_phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getBackground() {
        return background;
    }

    public void setBackground(int background) {
        this.background = background;
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
                ", id_phone='" + id_phone + '\'' +
                ", name='" + name + '\'' +
                ", photo='" + photo + '\'' +
                ", state='" + state + '\'' +
                ", background=" + background +
                ", selected=" + selected +
                ", refresh=" + refreshImage +
                ", lastBlockedTime=" + lastBlockedTime +
                '}';
    }
}
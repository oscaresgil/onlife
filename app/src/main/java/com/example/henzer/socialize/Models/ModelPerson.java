package com.example.henzer.socialize.Models;

import java.io.Serializable;

public class ModelPerson implements Serializable{
    private String id;
    private String id_phone;
    private String name;
    private String photo;
    private String state;
    private int background;
    private boolean selected;
    private boolean deleted=true;

    public ModelPerson() {
    }

    public ModelPerson(String id, String id_phone, String name, String photo, String state) {
        this.id = id;
        this.id_phone = id_phone;
        this.name = name;
        this.photo = photo;
        this.state = state;
        selected = false;
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

    public void setId_phone(String id_phone) {
        this.id_phone = id_phone;
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

    public void setPhoto(String photo) {
        this.photo = photo;
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

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Override public String toString() {
        return "Person{" +
                "id=" + id +
                ", id_phone='" + id_phone + '\'' +
                ", name='" + name + '\'' +
                ", photo='" + photo + '\'' +
                ", state='" + state + '\'' +
                ", selected='" + selected + '\'' +
                '}';
    }
}
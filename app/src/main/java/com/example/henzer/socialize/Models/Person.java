package com.example.henzer.socialize.Models;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by Henzer on 01/05/2015.
 */
public class Person implements Serializable{
    private String id;
    private String id_phone;
    private String name;
    private String photo;
    private Bitmap icon;
    private String state;
    int background;
    boolean selected;

    public Person() {
    }

    public Person(String id, String id_phone, String name, String photo, Bitmap icon, String state) {
        this.id = id;
        this.id_phone = id_phone;
        this.name = name;
        this.photo = photo;
        this.icon = icon;
        this.state = state;
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

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
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

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", id_phone='" + id_phone + '\'' +
                ", name='" + name + '\'' +
                ", photo='" + photo + '\'' +
                ", icon='" + icon + '\'' +
                ", state='" + state + '\'' +
                '}';
    }
}

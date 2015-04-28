package com.example.henzer.sqlite.model;

/**
 * Created by Henzer on 25/04/2015.
 */
public class People {
    private int id;
    private String name;
    private String photo;

    public People() {
    }

    public People(int id, String name, String photo) {
        this.id = id;
        this.name = name;
        this.photo = photo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
}

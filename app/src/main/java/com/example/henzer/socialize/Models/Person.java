package com.example.henzer.socialize.Models;

/**
 * Created by Henzer on 01/05/2015.
 */
public class Person {
    private String id;
    private String id_phone;
    private String name;
    private String photo;
    private String bitmap;
    private String state;

    public Person() {
    }

    public Person(String id, String id_phone, String name, String photo, String bitmap, String state) {
        this.id = id;
        this.id_phone = id_phone;
        this.name = name;
        this.photo = photo;
        this.bitmap = bitmap;
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

    public String getBitmap() {
        return bitmap;
    }

    public void setBitmap(String bitmap) {
        this.bitmap = bitmap;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", id_phone='" + id_phone + '\'' +
                ", name='" + name + '\'' +
                ", photo='" + photo + '\'' +
                ", bitmap='" + bitmap + '\'' +
                ", state='" + state + '\'' +
                '}';
    }
}

package com.example.henzer.socialize;


import android.graphics.Bitmap;

import java.net.URL;

public class UserData {
    String id="";
    String name="";
    Bitmap icon;
    URL url;
    int background;

    public UserData(String id, String name, URL url){
        this.id = id;
        this.name = name;
        this.url = url;
        background = 2;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public Bitmap getImageAvatar() {
        return icon;
    }

    public int getBackground(){
        return background;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id){
        this.id = id;
    }

    public String toString(){
        if (icon!=null)
            return id+" "+name+" "+icon.toString();
        else
            return id+" "+name +" "+url;
    }
}

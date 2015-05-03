package com.example.henzer.socialize;


import android.graphics.Bitmap;

import java.io.Serializable;
import java.net.URL;

public class UserDataYaNoSeUsa implements Serializable{
    String id="";
    String name="";
    Bitmap icon;
    URL url;
    int background;
    boolean selected;

    public UserDataYaNoSeUsa(String id, String name, URL url){
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

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
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

    public String toString(){
        if (icon!=null)
            return id+" "+name+" "+icon.toString();
        else
            return id+" "+name +" "+url;
    }
}

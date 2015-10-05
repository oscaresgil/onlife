package com.objective4.app.onlife.Models;

public class ModelMessages {
    private String userName;
    private String message="";
    private String gifName="";
    private String color="";

    public ModelMessages(String userName, String message, String gifName, String color) {
        this.userName = userName;
        this.message = message;
        this.gifName = gifName;
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getGifName() {
        return gifName;
    }

    public void setGifName(String gifName) {
        this.gifName = gifName;
    }

    @Override
    public String toString() {
        return "ModelMessages{" +
                "userName='" + userName + '\'' +
                ", message='" + message + '\'' +
                ", gifName='" + gifName + '\'' +
                '}';
    }
}

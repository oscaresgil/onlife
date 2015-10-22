package com.objective4.app.onlife.Models;

public class ModelMessages {
    private String userName;
    private String message="";
    private String gifName="";
    private int color;
    private int colorDark;

    public ModelMessages(String userName, String message, String gifName, int color, int colorDark) {
        this.userName = userName;
        this.message = message;
        this.gifName = gifName;
        this.color = color;
        this.colorDark = colorDark;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
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

    public int getColorDark() {
        return colorDark;
    }

    public void setColorDark(int colorDark) {
        this.colorDark = colorDark;
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

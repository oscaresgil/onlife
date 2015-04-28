package com.example.henzer.sqlite.model;

/**
 * Created by Henzer on 25/04/2015.
 */
public class Group {
    private int id;
    private String name;
    private int limit;

    public Group() {
    }

    public Group(int id, String name, int limit) {
        this.id = id;
        this.name = name;
        this.limit = limit;
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

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}

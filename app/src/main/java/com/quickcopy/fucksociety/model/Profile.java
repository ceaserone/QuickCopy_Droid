package com.quickcopy.fucksociety.model;

public class Profile {
    public String id;
    public String emoji;
    public String name;
    public String[] hints = new String[5];
    public String[] values = new String[5];

    public Profile(String id) {
        this.id = id;
    }
}

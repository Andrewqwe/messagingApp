package com.reconizer.loveteller;

/**
 * Created by Radosław on 2017-08-17.
 */

public class User {
    public String name;
    public String email;
    public String photo;
    public String first_name;
    public String last_name;
    public String gender;
    public String age;
    public String facebook_id;
    public double latitude;
    public double longitude;

    public User() {}

    public User(String name,String email, String photo) {
        this.email = email;
        this.name = name;
        this.photo = photo;
    }

    public User(String first_name,String last_name,String email,String gender,String age, String photo,String facebook_id, double latitude, double longitude) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.gender = gender;
        this.age = age;
        this.photo = photo;
        this.facebook_id = facebook_id;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}


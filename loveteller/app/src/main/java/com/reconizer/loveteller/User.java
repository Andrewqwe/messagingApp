package com.reconizer.loveteller;

import android.net.Uri;

/**
 * Created by Radosław on 2017-08-17.
 */

public class User {
    public String name;
    public String email;
    public String photo;

    public User() {}

    public User(String name,String email, String photo) {
        this.email = email;
        this.name = name;
        this.photo = photo;
    }

}


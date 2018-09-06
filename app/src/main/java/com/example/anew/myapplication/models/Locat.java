package com.example.anew.myapplication.models;
/**
 * Created by New on 28-02-2017.
 */

import com.google.firebase.database.IgnoreExtraProperties;

// [START blog_user_class]
@IgnoreExtraProperties
public class Locat {
    public float lati;
    public float longi;
    public String timestamp;

    public Locat() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Locat(float lati,float longi,String timestamp) {
        this.lati = lati;
        this.longi = longi;
        this.timestamp=timestamp;
    }
}

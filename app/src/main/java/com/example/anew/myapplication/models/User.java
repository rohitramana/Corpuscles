package com.example.anew.myapplication.models;

import com.google.firebase.database.IgnoreExtraProperties;

// [START blog_user_class]
@IgnoreExtraProperties
public class User {

    public String username;
    public String email;
    public String bloodType;
    public int age;
    public String phnum;
    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }
    public User(String username, String email,String bloodType,int age,String phnum) {
        this.username = username;
        this.email = email;
        this.bloodType=bloodType;
        this.age=age;
        this.phnum=phnum;
    }

}
// [END blog_user_class]

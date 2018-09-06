package com.example.anew.myapplication.models;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by New on 15-03-2017.
 */

// [START blog_user_class]
@IgnoreExtraProperties
public class NearBy {

    public String key;
    public Double dist;
    public Double time;
    public NearBy() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public NearBy(String key,Double dist,Double time) {
       this.key=key;
       this.dist=dist;
       this.time=time;
    }

}
// [END blog_user_class]

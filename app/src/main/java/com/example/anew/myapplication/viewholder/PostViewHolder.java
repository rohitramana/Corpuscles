package com.example.anew.myapplication.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.anew.myapplication.R;
import com.example.anew.myapplication.models.NearBy;
import com.example.anew.myapplication.models.User;

import java.text.DecimalFormat;

public class PostViewHolder extends RecyclerView.ViewHolder {

    public TextView titleView;
    public TextView authorView;

    public TextView distanceView;
    public TextView bodyView;
    public TextView timeView;

    protected static final String TAG = "PostViewHolder";
    public PostViewHolder(View itemView) {
        super(itemView);

        titleView = (TextView) itemView.findViewById(R.id.post_title);
        authorView = (TextView) itemView.findViewById(R.id.post_author);
       // starView = (ImageView) itemView.findViewById(R.id.star);
        distanceView = (TextView) itemView.findViewById(R.id.distance);
        bodyView = (TextView) itemView.findViewById(R.id.post_body);
        timeView=(TextView) itemView.findViewById(R.id.time);
    }

    public void bindToPost(User user, NearBy model) {
       //if(tm.containsValue(postKey)){
        DecimalFormat df=new DecimalFormat("#.00");
        titleView.setText(user.bloodType +"   ("+user.phnum+")");

        authorView.setText(user.username);

        distanceView.setText(df.format(model.dist)+" Km");
        timeView.setText(df.format(model.time)+" mins");

    }


   // }
}

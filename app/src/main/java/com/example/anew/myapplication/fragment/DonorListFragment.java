package com.example.anew.myapplication.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.anew.myapplication.MapsActivity;
import com.example.anew.myapplication.R;
import com.example.anew.myapplication.models.Locat;
import com.example.anew.myapplication.models.NearBy;
import com.example.anew.myapplication.models.User;
import com.example.anew.myapplication.viewholder.PostViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public  class DonorListFragment extends Fragment {

    private static final String TAG = "PostListFragment";

    // [START define_database_reference]
    private DatabaseReference mDatabase;
    // [END define_database_reference]

    private FirebaseRecyclerAdapter<NearBy, PostViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;

    private SwipeRefreshLayout mSwipeRefreshLayout;

   // String bloodtyp;
   TextView bloodtyp;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference("user_location_donor");
    DatabaseReference refreq = database.getReference("user_location_requestor");
    DatabaseReference refuser = database.getReference("users");
    final DatabaseReference nearby = database.getReference();

    public DonorListFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_all_posts, container, false);

       // bloodtyp = getArguments().getString("bloodtype");
        bloodtyp=(TextView)getActivity().findViewById(R.id.bloodtyp);
        System.out.println("Donorlistfragment"+bloodtyp.getText());

        mSwipeRefreshLayout=(SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);

        // [START create_database_reference]
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // [END create_database_reference]

        mRecycler = (RecyclerView) rootView.findViewById(R.id.messages_list);
        mRecycler.setHasFixedSize(true);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Set up Layout Manager, reverse layout
        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);
        refreshData();
        //updateData();
    }

    public void updateData() {


        // Set up FirebaseRecyclerAdapter with the Query
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
            }
        }, 15000);
        Query postsQuery = getQuery(mDatabase);

        mAdapter = new FirebaseRecyclerAdapter<NearBy, PostViewHolder>(NearBy.class, R.layout.item_post,
                PostViewHolder.class, postsQuery) {

            @Override
            protected void populateViewHolder(final PostViewHolder viewHolder, final NearBy model, final int position) {
                final DatabaseReference postRef = getRef(position);

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                    }
                }, 5000);



                // Set click listener for the whole post view
                final String postKey = postRef.getKey();


                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(getActivity(), MapsActivity.class);
                        intent.putExtra(MapsActivity.EXTRA_POST_KEY, postKey);
                        ref.child("nearby").child(getUid()).setValue(null);
                        startActivity(intent);
                    }
                });


                refuser.orderByKey().equalTo(postKey).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                        User changedPost = dataSnapshot.getValue(User.class);
                        System.out.println("The updated post title is: " + changedPost.username);

                        viewHolder.bindToPost(changedPost, model);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

                // Bind Post to ViewHolder, setting OnClickListener for the star button
            }
        };
        // mRecycler.getRecycledViewPool().clear();
        //mAdapter.notifyDataSetChanged();
        mRecycler.setAdapter(mAdapter);
    }

    public void refreshData() {
        refreq.orderByKey().equalTo(getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                User changedPost = dataSnapshot.getValue(User.class);
                updateData();
                System.out.println("The updated post title is: " + changedPost.username);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) {
            mAdapter.cleanup();
        }
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    //public abstract Query getQuery(DatabaseReference databaseReference);
    Locat myloc;
    Locat otherloc;
    User buser;
    NearBy list;
    Float latFrom = Float.valueOf(0);
    Float lngFrom;
    Float latTo, lngTo;

    Double dist = 0.0;
    Double time= 0.0;
       public Query getQuery(DatabaseReference databaseReference) {
        // [START my_top_posts_query]
        // My top posts by number of stars
        final String myUserId = getUid();

        final FirebaseDatabase database = FirebaseDatabase.getInstance();


        final DatabaseReference ordbyblood = database.getReference("users");
        Log.i(TAG, "outside1");
// Attach a listener to read the data at our posts reference

        refreq.orderByKey().equalTo(myUserId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                myloc = dataSnapshot.getValue(Locat.class);
                latFrom = myloc.lati;
                lngFrom = myloc.longi;
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        ref.orderByKey().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                otherloc = dataSnapshot.getValue(Locat.class);
                if (!dataSnapshot.getKey().equals(myUserId) ){
                    latTo = otherloc.lati;
                    lngTo = otherloc.longi;
                    dist = getDistanceInfo();
                    Log.i(TAG, "outside2  " + dataSnapshot.getKey()) ;
                    if ( dist < 10 ) {
                        Log.i(TAG, "outside2.2  " + dataSnapshot.getKey()) ;
                        ordbyblood.orderByKey().equalTo(dataSnapshot.getKey()).addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                                buser = dataSnapshot.getValue(User.class);
                                Log.i(TAG, "outside2.5" +buser.username+" "+ buser.bloodType+" "+bloodtyp.getText());
                                if (buser.bloodType.equals(bloodtyp.getText())) {
                                    list = new NearBy(dataSnapshot.getKey(), dist,time);
                                    nearby.child("nearby").child(getUid()).child(dataSnapshot.getKey()).setValue(list);
                                    Log.i(TAG, "outside3");
                                }

                            }

                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
                            }

                            @Override
                            public void onChildRemoved(DataSnapshot dataSnapshot) {
                            }

                            @Override
                            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }


                        });
                    }

                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }


        });
        Log.i(TAG, "outside4");
        Query donors = databaseReference.child("nearby").child(getUid());

        return donors;
        //return donor_list;

    }

    private double getDistanceInfo() {
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here

        }
        StringBuilder stringBuilder = new StringBuilder();

        try {

            // destinationAddress = destinationAddress.replaceAll(" ","%20");
            String url = "http://maps.googleapis.com/maps/api/directions/json?origin=" + latFrom + "," + lngFrom + "&destination=" + latTo + "," + lngTo + "&mode=driving&sensor=false";

            HttpPost httppost = new HttpPost(url);

            HttpClient client = new DefaultHttpClient();
            HttpResponse response;
            stringBuilder = new StringBuilder();


            response = client.execute(httppost);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }

        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject = new JSONObject(stringBuilder.toString());

            JSONArray array = jsonObject.getJSONArray("routes");

            JSONObject routes = array.getJSONObject(0);

            JSONArray legs = routes.getJSONArray("legs");

            JSONObject steps = legs.getJSONObject(0);

            JSONObject timing=steps.getJSONObject("duration");

            JSONObject distance = steps.getJSONObject("distance");



            time=Double.parseDouble(timing.getString("value"));
            time=time/60;
            System.out.println("time"+time);

            dist = Double.parseDouble(distance.getString("value") );
            dist=dist/1000;
            System.out.println("dist"+dist);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return dist;
    }



}

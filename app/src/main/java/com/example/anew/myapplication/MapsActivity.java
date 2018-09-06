package com.example.anew.myapplication;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.TextView;

import com.example.anew.myapplication.Modules.DirectionFinder;
import com.example.anew.myapplication.Modules.DirectionFinderListener;
import com.example.anew.myapplication.Modules.Route;
import com.example.anew.myapplication.models.Locat;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, DirectionFinderListener {
    private static final String TAG = "MapsActivity";
    private GoogleMap mMap;

    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;

    public static final String EXTRA_POST_KEY = "post_key";

    private DatabaseReference mDatabase;
    FirebaseUser user;
    String mPostKey;
    double latFrom,lngFrom,latto,lngto;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        user= FirebaseAuth.getInstance().getCurrentUser();

        mPostKey = getIntent().getStringExtra(EXTRA_POST_KEY);
        Log.i(TAG, mPostKey);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        sendRequest();

    }
    private void sendRequest() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("user_location_donor");
        DatabaseReference refreq = database.getReference("user_location_requestor");
        String myUserId = user.getUid();
        refreq.orderByKey().equalTo(myUserId).addChildEventListener(new ChildEventListener() {
                                                            @Override
                                                            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                                                                Locat myloc = dataSnapshot.getValue(Locat.class);
                                                                latFrom = myloc.lati;
                                                                lngFrom = myloc.longi;
                                                                Log.i(TAG, "" + dataSnapshot.getChildrenCount());
                                                                Log.i(TAG, latFrom + " " + lngFrom);
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


                                                        }

        );
        ref.orderByKey().equalTo(mPostKey).addChildEventListener(new ChildEventListener() {
                                                                     @Override
                                                                     public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                                                                         Locat otherloc = dataSnapshot.getValue(Locat.class);
                                                                         latto = otherloc.lati;
                                                                         lngto = otherloc.longi;
                                                                         Log.i(TAG, latto + " " + lngto);
                                                                         String origin = latFrom+","+lngFrom;
                                                                         String destination = latto+","+lngto;
                                                                         try {
                                                                             new DirectionFinder(MapsActivity.this, origin, destination).execute();
                                                                         } catch (UnsupportedEncodingException e) {
                                                                             e.printStackTrace();
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


                                                                 }

        );



    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng hcmus = new LatLng(latFrom,lngFrom);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hcmus, 18));
        originMarkers.add(mMap.addMarker(new MarkerOptions()
                .title("Your Position")
                .position(hcmus)));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
    }


    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Please wait.",
                "Finding direction..!", true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline:polylinePaths ) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
            ((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text);
            ((TextView) findViewById(R.id.tvDistance)).setText(route.distance.text);

            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue))
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
                    .title(route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(20);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }
    }
}

package com.example.somename;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
//git test text
//    private static final String TAG = "MainActivity"; //FireStore Constant
    FirebaseFirestore db;
    ArrayList<Vehicle> vehicleList = new ArrayList<>();

//    private View.OnTouchListener onTouchListener = new com.example.somename.OnSwipeTouchListener(MainActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //get arraylist
        Bundle extra = getIntent().getBundleExtra("extra");
        if (extra != null) {
            vehicleList = (ArrayList<Vehicle>) extra.getSerializable("vehicleList");
        }
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        //test code for custom view
//        if (vehicleList.isEmpty()) {
//            int[] icon = new int[1];
//            icon[0] = R.drawable.baseline_directions_car_black_18dp;
//            // int testCarInt = findViewById(R.drawable.baseline_directions_car_black_18dp);
//            Vehicle testCar = new Vehicle("testcar", icon[0]);
//
//            int[] icon2 = new int[1];
//            icon2[0] = R.drawable.baseline_directions_car_black_18dp;
//            Vehicle testCarTwo = new Vehicle("testcar 2", icon2[0]);
//
//            int[] icon3 = new int[1];
//            icon3[0] = R.drawable.baseline_directions_car_black_18dp;
//            Vehicle testCarThree = new Vehicle("fuck yeah", icon3[0]);
//
//            vehicleList.add(testCar);
//            vehicleList.add(testCarTwo);
//            vehicleList.add(testCarThree);
//        }
        //end of test code for custom view,


       //set swipe listener on activity main
        ConstraintLayout layout = findViewById(R.id.activityMain);

        layout.setOnTouchListener(new com.example.somename.OnSwipeTouchListener(MainActivity.this) {
            public void onSwipeTop() {
                Toast.makeText(MainActivity.this, "top", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeRight() {
                Intent intent = new Intent(MainActivity.this, VehicleListActivity.class);
                //intent.putExtra("vehicleList", vehicleList);
                Bundle extra = new Bundle();
                extra.putSerializable("vehicleList", vehicleList);
                intent.putExtra("extra", extra);

                startActivity(intent);
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
               // Toast.makeText(MainActivity.this, "right", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeLeft() {
                Toast.makeText(MainActivity.this, "left", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeBottom() {
                Toast.makeText(MainActivity.this, "bottom", Toast.LENGTH_SHORT).show();
            }

        });







    }


    @Override
    public void onMapReady(GoogleMap map) {
        map.addMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
                .title("Marker"));
    }










}

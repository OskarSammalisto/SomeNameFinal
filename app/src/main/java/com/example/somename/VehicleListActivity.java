package com.example.somename;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VehicleListActivity extends AppCompatActivity {

//ss
    //list of vehicle objects
    ArrayList<Vehicle> vehicleList = new ArrayList<>();
    FirebaseFirestore db;
    private static final String TAG = "VehicleListActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_list);

        db = FirebaseFirestore.getInstance();


        //get arraylist
        Bundle extra = getIntent().getBundleExtra("extra");
        vehicleList = (ArrayList<Vehicle>) extra.getSerializable("vehicleList");


        FloatingActionButton fab = findViewById(R.id.fabAddVehicle);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Intent intent = new Intent(VehicleListActivity.this, AddVehicleActivity.class);
                Bundle extra = new Bundle();
                extra.putSerializable("vehicleList", vehicleList);
                intent.putExtra("extra", extra);
                startActivity(intent);


            }
        });

        //test code for custom view

//        int[] icon = new int[1];
//        icon[0] = R.drawable.baseline_directions_car_black_18dp;
//       // int testCarInt = findViewById(R.drawable.baseline_directions_car_black_18dp);
//        Vehicle testCar = new Vehicle("testcar", icon[0]);
//
//        int[] icon2 = new int[1];
//        icon2[0] = R.drawable.baseline_directions_car_black_18dp;
//        Vehicle testCarTwo = new Vehicle("testcar 2", icon2[0]);
//
//        vehicleList.add(testCar);
//        vehicleList.add(testCarTwo);
        //end of test code for custom view,

        ListView vehicleListView = findViewById(R.id.vehicleListView);


        VehicleListAdapter vehicleListAdapter = new VehicleListAdapter(this, vehicleList);

        vehicleListView.setAdapter(vehicleListAdapter);
//

        //Swipe back to main screen,

        ConstraintLayout layout = findViewById(R.id.vehicleListActivity);
        RelativeLayout layout2 = findViewById(R.id.customVehicleListLayout);


        vehicleListView.setOnTouchListener(new com.example.somename.OnSwipeTouchListener(VehicleListActivity.this) {
            public void onSwipeTop() {
                Toast.makeText(VehicleListActivity.this, "top", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeRight() {
                //test send arraylist to firestore
               // arrayListToFirebaseTest(vehicleList);
              Toast.makeText(VehicleListActivity.this, "right", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeLeft() {

                Intent intent = new Intent(VehicleListActivity.this, MainActivity.class);
                Bundle extra = new Bundle();
                extra.putSerializable("vehicleList", vehicleList);
                intent.putExtra("extra", extra);
                startActivity(intent);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);

                //Toast.makeText(VehicleListActivity.this, "left", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeBottom() {
                Toast.makeText(VehicleListActivity.this, "bottom", Toast.LENGTH_SHORT).show();
            }

        });





    }

    public void arrayListToFirebaseTest(ArrayList arrayList){
        Map<String, ArrayList<Vehicle>> map = new HashMap<>();
        map.get(arrayList); //doesn't add data, just creates directory.

        db.collection("users")
                .add(map)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

    }


}

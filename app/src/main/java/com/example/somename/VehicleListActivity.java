package com.example.somename;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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

        //get theme selection from sharedPref and apply to activity.
        SharedPreferences preferences = getSharedPreferences("MyPref", MODE_PRIVATE);
        int theme = preferences.getInt("theme", 1);

        if(theme == 1) {
            setTheme(R.style.AppTheme);
        }

        if(theme == 2) {
            setTheme(R.style.LightAppTheme);
        }

        if(theme == 3) {
            setTheme(R.style.DarkAppTheme);
        }


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_list);

        db = FirebaseFirestore.getInstance();



        //get arraylist
       // Bundle extra = getIntent().getBundleExtra("extra");
        //vehicleList = (ArrayList<Vehicle>) extra.getSerializable("vehicleList");

        vehicleList = this.getIntent().getParcelableArrayListExtra("arrayListPars");


        FloatingActionButton fab = findViewById(R.id.fabAddVehicle);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (vehicleList.size() < 6) {
                    Intent intent = new Intent(VehicleListActivity.this, AddVehicleActivity.class);



                    intent.putParcelableArrayListExtra("arrayListPars", vehicleList);

                    startActivity(intent);

                }

                else {
                    Toast.makeText(VehicleListActivity.this, "You can only store six Vehicles at a time!", Toast.LENGTH_LONG).show();
                }



            }
        });


        ListView vehicleListView = findViewById(R.id.vehicleListView);

        VehicleListAdapter vehicleListAdapter = new VehicleListAdapter(this, vehicleList);

        vehicleListView.setAdapter(vehicleListAdapter);




        //listens to clicks on listView. int position is arrayList index.
        vehicleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(VehicleListActivity.this, "clicked row " +position, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(VehicleListActivity.this, VehicleInfoActivity.class);


                intent.putParcelableArrayListExtra("arrayListPars", vehicleList);

                intent.putExtra("vehicle", position);
                startActivity(intent);


            }
        });


//

        //Swipe back to main screen,

        ConstraintLayout layout = findViewById(R.id.vehicleListActivity);
        RelativeLayout layout2 = findViewById(R.id.customVehicleListLayout);


        vehicleListView.setOnTouchListener(new com.example.somename.OnSwipeTouchListener(VehicleListActivity.this) {
            public void onSwipeTop() {
               // Toast.makeText(VehicleListActivity.this, "top", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeRight() {

                Intent intent = new Intent(VehicleListActivity.this, SharedVehiclesActivity.class);

                intent.putParcelableArrayListExtra("arrayListPars", vehicleList);

                startActivity(intent);
                overridePendingTransition(R.anim.right_in, R.anim.right_out);

             // Toast.makeText(VehicleListActivity.this, "right", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeLeft() {

                Intent intent = new Intent(VehicleListActivity.this, MainActivity.class);
//

                intent.putParcelableArrayListExtra("arrayListPars", vehicleList);

                startActivity(intent);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);


            }
            public void onSwipeBottom() {
               // Toast.makeText(VehicleListActivity.this, "bottom", Toast.LENGTH_SHORT).show();
            }

        });





    }


    @Override
    public void onBackPressed(){

    }

}

package com.example.somename;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SharedVehiclesActivity extends AppCompatActivity {

    ArrayList<Vehicle> sharedVehicles = new ArrayList<>();
    ArrayList<Vehicle> vehicleList = new ArrayList<>();


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
        setContentView(R.layout.activity_shared_vehicles);

        vehicleList = this.getIntent().getParcelableArrayListExtra("arrayListPars");

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String email = user.getEmail();
        Log.d("!!!!", email);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final CollectionReference sharedVehicleRef = db.collection("shared").document(email).collection("sharedVehicles");


        ConstraintLayout layout = findViewById(R.id.sharedVehiclesActivity);
        layout.setOnTouchListener(new com.example.somename.OnSwipeTouchListener(SharedVehiclesActivity.this) {
            public void onSwipeTop() {
                // Toast.makeText(VehicleListActivity.this, "top", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeRight() {

//
            }
            public void onSwipeLeft() {

                Intent intent = new Intent(SharedVehiclesActivity.this, VehicleListActivity.class);
//

                intent.putParcelableArrayListExtra("arrayListPars", vehicleList);

                startActivity(intent);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);


            }
            public void onSwipeBottom() {
                // Toast.makeText(VehicleListActivity.this, "bottom", Toast.LENGTH_SHORT).show();
            }

        });

        sharedVehicleRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        final Vehicle vehicle = documentSnapshot.toObject(Vehicle.class);
                        sharedVehicles.add(vehicle);
                        Log.d("!!!!", sharedVehicles.get(0).getName());
                        setAdapter();
                    }
                }

            }

        });

        ListView listView = findViewById(R.id.sharedVehicleListView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Log.d("!!!!", "pressed" + sharedVehicles.get(position).getName());
                new AlertDialog.Builder(SharedVehiclesActivity.this)
                        .setMessage("Add to your list?")
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                vehicleList.add(sharedVehicles.get(position));
                                CollectionReference vehicleRef = db.collection("users").document(user.getUid()).collection("vehicles");
                                vehicleRef.document(sharedVehicles.get(position).getName()).set(sharedVehicles.get(position));
                            }
                        })
                        .setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sharedVehicleRef.document(sharedVehicles.get(position).getName()).delete();
                                sharedVehicles.remove(position);
                                setAdapter();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });


    }

    public void setAdapter() {
        ListView sharedListView = findViewById(R.id.sharedVehicleListView);

        VehicleListAdapter vehicleListAdapter = new VehicleListAdapter(this, sharedVehicles);

        sharedListView.setAdapter(vehicleListAdapter);


    }
}

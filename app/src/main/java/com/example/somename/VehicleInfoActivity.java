package com.example.somename;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class VehicleInfoActivity extends AppCompatActivity {

    ArrayList<Vehicle> vehicleList = new ArrayList<>();
    private int vehicle;
    private Uri uri;
    private String uriString;
    private FloatingActionButton deleteVehicleFab;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_info);

        db = FirebaseFirestore.getInstance();

        deleteVehicleFab = findViewById(R.id.deleteVehicle);

        //get arrayList
//        Bundle extra = getIntent().getBundleExtra("extra");
//        vehicleList = (ArrayList<Vehicle>) extra.getSerializable("vehicleList");

        vehicleList = this.getIntent().getParcelableArrayListExtra("arrayListPars");

        //get selected vehicle by arrayList position
        vehicle = getIntent().getIntExtra("vehicle", 0);



        TextView vehicleName = findViewById(R.id.vehicleName);
        vehicleName.setText(vehicleList.get(vehicle).getName());

        TextView vehicleDescription = findViewById(R.id.vehicleDescription);
        vehicleDescription.setText(vehicleList.get(vehicle).getDescription());

        ImageView imageView = findViewById(R.id.vehicleImage);



        Bitmap bitmap;
        uriString = vehicleList.get(vehicle).getUri();
        uri = Uri.parse(vehicleList.get(vehicle).getUri());
        if (uri != null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                imageView.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        deleteVehicleFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(VehicleInfoActivity.this)
                        .setMessage("Delete?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

//                                CollectionReference collRef = db.collection(vehicleList.get(vehicle).getVehiclesRef());

                                db.collection("vehicles").document(vehicleList.get(vehicle).getName()).delete();


                                vehicleList.remove(vehicle);
                                Intent intent = new Intent(VehicleInfoActivity.this, VehicleListActivity.class);

//                                Bundle extra = new Bundle();
//                                extra.putSerializable("vehicleList", vehicleList);
//                                intent.putExtra("extra", extra);

                                intent.putParcelableArrayListExtra("arrayListPars", vehicleList);

                                startActivity(intent);


                            }
                        })
                        .setNegativeButton("No", null)
                        .show();





            }
        });


    }
}

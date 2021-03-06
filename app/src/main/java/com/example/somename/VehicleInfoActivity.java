package com.example.somename;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcelable;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class VehicleInfoActivity extends AppCompatActivity {

    ArrayList<Vehicle> vehicleList = new ArrayList<>();
    private int vehicle;
    private Uri uri;
    private String uriString;
    private Button deleteVehicleFab;
    private Button shareButton;
    FirebaseFirestore db;
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //get theme selection from sharedPref and apply to activity.
        SharedPreferences preferences = getSharedPreferences("MyPref", MODE_PRIVATE);
        final int theme = preferences.getInt("theme", 1);

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
        setContentView(R.layout.activity_vehicle_info);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        final CollectionReference vehicleRef = db.collection("users").document(user.getUid()).collection("vehicles");

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

        shareButton = findViewById(R.id.shareButton);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder shareDialog = new AlertDialog.Builder(VehicleInfoActivity.this);

                shareDialog.setTitle(getString(R.string.how_share));

                final EditText shareEmail = new EditText(VehicleInfoActivity.this);
                shareEmail.setHint(getString(R.string.email_dialog));
                shareDialog.setView(shareEmail);

                shareDialog.setPositiveButton(getString(R.string.outside), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Uri uri = Uri.parse(vehicleList.get(vehicle).getUri());
                                try {
                                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                Intent email = new Intent(Intent.ACTION_SEND);
                                email.putExtra(Intent.EXTRA_EMAIL, new String[]{shareEmail.getText().toString()});
                                email.putExtra(Intent.EXTRA_SUBJECT, "Shared Vehicle Position");
                                email.setType("text/plain");
                                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                                StrictMode.setVmPolicy(builder.build());
                                email.putExtra(android.content.Intent.EXTRA_TEXT, "Vehicle: " +vehicleList.get(vehicle).getName() +" is located at: " +"https://www.google.com/maps/search/?api=1&query=" +vehicleList.get(vehicle).getLatitude() +","+vehicleList.get(vehicle).getLongitude());
                                email.putExtra(Intent.EXTRA_STREAM, uri);
                                startActivity(Intent.createChooser(email, "Send Email"));
                    }
                });

                shareDialog.setNeutralButton(getString(R.string.in_app), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String email = shareEmail.getText().toString();
                        String userEmail = user.getEmail();

                        if (email.equals(userEmail)) {
                            Toast.makeText(VehicleInfoActivity.this, R.string.own_email, Toast.LENGTH_SHORT).show();
                        }
                        else if (email.length() != 0) {
                            CollectionReference shareRef = db.collection("shared").document(email).collection("sharedVehicles");
                            shareRef.document(vehicleList.get(vehicle).getName()).set(vehicleList.get(vehicle));
                            Toast.makeText(VehicleInfoActivity.this, getString(R.string.vehicle_shared) +": " +email, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(VehicleInfoActivity.this, R.string.email_empty, Toast.LENGTH_SHORT).show();
                        }


                    }
                });

                shareDialog.setNegativeButton(getString(R.string.cancel), null);

                shareDialog.show();

//                new AlertDialog.Builder(VehicleInfoActivity.this)
//                        .setMessage("How will you share")
//                        .setCancelable(false)
//                        .setPositiveButton("outside", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                Uri uri = Uri.parse(vehicleList.get(vehicle).getUri());
//                                try {
//                                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }
//                                Intent email = new Intent(Intent.ACTION_SEND);
//                                email.putExtra(Intent.EXTRA_EMAIL, "Vehicle" +vehicleList.get(vehicle).getName());
//                                email.putExtra(Intent.EXTRA_SUBJECT, "Shared Vehicle Position");
//                                email.setType("text/plain");
//                                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
//                                StrictMode.setVmPolicy(builder.build());
//                                email.putExtra(android.content.Intent.EXTRA_TEXT, "Vehicle: " +vehicleList.get(vehicle).getName() +" is located at: " +"https://www.google.com/maps/search/?api=1&query=" +vehicleList.get(vehicle).getLatitude() +","+vehicleList.get(vehicle).getLongitude());
//                                email.putExtra(Intent.EXTRA_STREAM, uri);
//                                startActivity(Intent.createChooser(email, "Send Email"));
//
//                            }
//                        })
//                        .setNeutralButton("in app", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                String email = "a";
//                                CollectionReference shareRef = db.collection("shared").document(email).collection("sharedVehicles");
//                                shareRef.document(vehicleList.get(vehicle).getName()).set(vehicleList.get(vehicle));
//
//                            }
//                        })
//                        .setNegativeButton("cancel", null)
//                        .show();


            }
        });



        Bitmap bitmap;
        uriString = vehicleList.get(vehicle).getUri();
        uri = Uri.parse(vehicleList.get(vehicle).getUri());
        if (uri != null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                //imageView.setImageBitmap(bitmap);
                imageView.setImageURI(Uri.parse(vehicleList.get(vehicle).getUri()));

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        deleteVehicleFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                new AlertDialog.Builder(VehicleInfoActivity.this)
                        .setMessage(getString(R.string.delete_vehicle))
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

//                                CollectionReference collRef = db.collection(vehicleList.get(vehicle).getVehiclesRef());

                                vehicleRef.document(vehicleList.get(vehicle).getName()).delete();


                                String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                StorageReference deletePhoto =  storageRef.child("images/" +userUid +"/" +vehicleList.get(vehicle).getName() +".jpg"); //storageRef.child("images/" +vehicleList.get(vehicle).getName() +".jpg");

                                deletePhoto.delete();

                                //delete local image file
                                File deleteFile = new File(Uri.parse(vehicleList.get(vehicle).getUri()).getPath());
                                if (deleteFile.exists()) {
                                     deleteFile.delete();
                                }



                                vehicleList.remove(vehicle);
                                Intent intent = new Intent(VehicleInfoActivity.this, VehicleListActivity.class);

//                                Bundle extra = new Bundle();
//                                extra.putSerializable("vehicleList", vehicleList);
//                                intent.putExtra("extra", extra);

                                intent.putParcelableArrayListExtra("arrayListPars", vehicleList);

                                startActivity(intent);


                            }
                        })
                        .setNegativeButton(R.string.no, null)
                        .show();





            }
        });


    }
}

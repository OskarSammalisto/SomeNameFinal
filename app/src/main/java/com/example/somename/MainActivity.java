package com.example.somename;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.location.Location;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {
    //git test text
//    private static final String TAG = "MainActivity"; //FireStore Constant
    FirebaseFirestore db;
    ArrayList<Vehicle> vehicleList = new ArrayList<>();
    private static final int REQUEST_LOCATION = 1;
    private FusedLocationProviderClient locationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    double currentLat;
    double currentLng;
    GoogleMap googleMap;
    private int moveMapToLocation = 1;
    private static final String TAG = "MainActivity";
    private StorageReference mStorageRef;
    private int currentVehiclePosition = 0;
    private LatLng emptyLatLng = new LatLng(0.0,0.0);
    private double myLat;
    private double myLon;
    private FirebaseAuth mAuth;
    private String currentPhotoPath;



//    private View.OnTouchListener onTouchListener = new com.example.somename.OnSwipeTouchListener(MainActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        //get arraylist
       // Bundle extra = getIntent().getBundleExtra("extra");

//        if (extra != null) {
//            vehicleList = (ArrayList<Vehicle>) extra.getSerializable("vehicleList");
//        }


        ArrayList<Vehicle> tempVehicleList = this.getIntent().getParcelableArrayListExtra("arrayListPars");

        if (tempVehicleList != null) {
            vehicleList = tempVehicleList;

        }
        //vehicleList = this.getIntent().getParcelableArrayListExtra("arrayListPars");


        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final CollectionReference vehicleRef = db.collection("users").document(user.getUid()).collection("vehicles");
        mStorageRef = FirebaseStorage.getInstance().getReference();


        //retrieve from fireStore

        if (vehicleList.isEmpty()) {

            vehicleRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            findViewById(R.id.mainScreen).setVisibility(View.GONE);
                            //recreate objects
                            final Vehicle vehicle = documentSnapshot.toObject(Vehicle.class);
                            vehicleList.add(vehicle);
                            Log.d("!!!", "added vehicles");

                            File vehiclePhoto = new File(Uri.parse(vehicle.getUri()).getPath());
                            if (vehiclePhoto.exists()){
                                Log.d("!!!", "file exists");
                                findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                                findViewById(R.id.mainScreen).setVisibility(View.VISIBLE);

                            } else {
//                                FirebaseStorage storage = FirebaseStorage.getInstance();
//                                StorageReference pictureReference = storage.getReferenceFromUrl(vehicle.getStorageUrl());

                                String userUid = mAuth.getInstance().getCurrentUser().getUid();
                                final StorageReference pictureReference = mStorageRef.child("images/" +userUid +"/" +vehicle.getName() + ".jpg");
                                Log.d("!!!", "got to here");

                                try {
                                    final File localFile = File.createTempFile("images", "jpg");
                                    pictureReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                                            //Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                            //set uri to vehicle
                                            Uri localFileUri = Uri.fromFile(localFile);
                                            String uriString = localFileUri.toString();

                                            vehicle.setUri(uriString);
                                            Log.d("!!!", "download file success" + vehicle.getName());
                                            if (!vehicleList.isEmpty())setVehicleDisplay();
                                            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                                            findViewById(R.id.mainScreen).setVisibility(View.VISIBLE);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });
                                } catch (IOException e) {
                                    Log.d("!!!", "downl not success");
                                }
                            }


                        }



                        if (!vehicleList.isEmpty())setVehicleDisplay();
                        for (Vehicle vehicle :vehicleList){
                            if (vehicle.getLatitude() != 0){
                                googleMap.addMarker(new MarkerOptions().position(new LatLng(vehicle.getLatitude(), vehicle.getLongitude())).title(vehicle.getName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.baseline_directions_car_black_18dp)));
                                Log.d("!!!", "vehicle: " + vehicle.getName() + " Lat: " + vehicle.getLatitude() + " Lon: " + vehicle.getLongitude());

                            }

                        }


                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }




                }
            });



        }




        //location gps

        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("gps_test", "no permission");

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {

            locationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        currentLat = location.getLatitude();
                        currentLng = location.getLongitude();
                    }
                }
            });
        }

        locationRequest = createLocationRequest();
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    LatLng myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    myLat = location.getLatitude();
                    myLon = location.getLongitude();
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(myLatLng).zoom(17).build();
                    if (moveMapToLocation == 1) {
                         googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        moveMapToLocation = 0;
                    }

                    Log.d("!!!! onlocation result", "lat: " + location.getLatitude() + " ,long: " + location.getLongitude());
                }
            }

        };



        //set swipe listener on activity main
        ConstraintLayout layout = findViewById(R.id.activityMain);


        layout.setOnTouchListener(new com.example.somename.OnSwipeTouchListener(MainActivity.this) {
            public void onSwipeTop() {
              //  Toast.makeText(MainActivity.this, "top", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeRight() {
                Intent intent = new Intent(MainActivity.this, VehicleListActivity.class);



                intent.putParcelableArrayListExtra("arrayListPars", vehicleList);

                startActivity(intent);
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                // Toast.makeText(MainActivity.this, "right", Toast.LENGTH_SHORT).show();
            }

            public void onSwipeLeft() {
                Intent intent = new Intent(MainActivity.this, OptionsActivity.class);



                intent.putParcelableArrayListExtra("arrayListPars", vehicleList);

                startActivity(intent);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);

             //   Toast.makeText(MainActivity.this, "left", Toast.LENGTH_SHORT).show();
            }

            public void onSwipeBottom() {
            //    Toast.makeText(MainActivity.this, "bottom", Toast.LENGTH_SHORT).show();
            }

        });

        RelativeLayout mainActivityDisplayVehicle = findViewById(R.id.mainActivityCurrentVehicle);

        mainActivityDisplayVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!vehicleList.isEmpty()){

                    new AlertDialog.Builder(MainActivity.this)
                            .setMessage("Park " +vehicleList.get(currentVehiclePosition).getName() + " here?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    //set lat lon on vehicle
                                    vehicleList.get(currentVehiclePosition).setLatitude(myLat);
                                    vehicleList.get(currentVehiclePosition).setLongitude(myLon);


                                    // clear and update markers
                                    googleMap.clear();

                                    for (Vehicle vehicle :vehicleList){
                                        if (vehicle.getLatitude() != 0){
                                            googleMap.addMarker(new MarkerOptions().position(new LatLng(vehicle.getLatitude(), vehicle.getLongitude())).title(vehicle.getName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.baseline_directions_car_black_18dp)));
                                            Log.d("!!!", "vehicle: " + vehicle.getName() + " Lat: " + vehicle.getLatitude() + " Lon: " + vehicle.getLongitude());

                                        }

                                    }

                                    // googleMap.addMarker(new MarkerOptions().position(new LatLng(vehicleList.get(currentVehiclePosition).getLatitude(), vehicleList.get(currentVehiclePosition).getLongitude())).title(vehicleList.get(currentVehiclePosition).getName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.baseline_directions_car_black_18dp)));
                                    Log.d("!!!!", "Vehicle: "   + vehicleList.get(currentVehiclePosition).getName() +"lat: " + vehicleList.get(currentVehiclePosition).getLatitude() +"lon: " + vehicleList.get(currentVehiclePosition).getLongitude() );

                                    //update vehicle in fireBase
                                    vehicleRef.document(vehicleList.get(currentVehiclePosition).getName()).set(vehicleList.get(currentVehiclePosition));


                                }
                            })
                            .setNegativeButton("No", null)
                            .show();



                }
            }
        });

        FloatingActionButton cycleVehiclesButton = findViewById(R.id.fabCycleVehicles);

        cycleVehiclesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vehicleList != null && !vehicleList.isEmpty()){

                    currentVehiclePosition ++;
                    if (currentVehiclePosition >= vehicleList.size()){
                        currentVehiclePosition = 0;
                    }
                    Log.d("!!!!", "currentVehicle pos" + currentVehiclePosition);
                    setVehicleDisplay();



                    if (vehicleList.get(currentVehiclePosition).getLatitude() != 0.0 && vehicleList.get(currentVehiclePosition).getLongitude() != 0.0){
                        LatLng myLatLng = new LatLng(vehicleList.get(currentVehiclePosition).getLatitude(), vehicleList.get(currentVehiclePosition).getLongitude());
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(myLatLng).zoom(17).build();
                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        moveMapToLocation = 0;
                    } else {
                        Toast.makeText(MainActivity.this, "Vehicle has no saved position!", Toast.LENGTH_SHORT).show();
                    }



                } else {
                    Toast.makeText(MainActivity.this, "You have no saved vehicles.", Toast.LENGTH_SHORT).show();
                }

            }
        });


                                                                                  //fill card with vehicle, too slow when getting from fireStore!!!! doesn't display first time!!!!!!!
        if (vehicleList != null && !vehicleList.isEmpty()) {
            setVehicleDisplay();

        }


    }

    public void setVehicleDisplay(){

        ImageView vehicleCardPic = findViewById(R.id.cardVehicleImage);
        TextView vehicleCardName = findViewById(R.id.cardVehicleName);
        TextView vehicleCardDesc = findViewById(R.id.cardVehicleDescription);

        vehicleCardName.setText(vehicleList.get(currentVehiclePosition).getName());
        vehicleCardDesc.setText(vehicleList.get(currentVehiclePosition).getDescription());

        Bitmap bitmap;

        Uri uri = Uri.parse(vehicleList.get(currentVehiclePosition).getUri());
        if (uri != null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                //vehicleCardPic.setImageBitmap(bitmap);
                vehicleCardPic.setImageURI(Uri.parse(vehicleList.get(currentVehiclePosition).getUri()));

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }




    }


    public void downloadAndSet(ArrayList<Vehicle> vehicleList) throws IOException {
        for (final Vehicle vehicle : vehicleList) {

            StorageReference storageReference = mStorageRef.child("images/" +vehicle.getName() + ".jpg");
            File localFile = File.createTempFile("images", "jpg");
            vehicle.setUri(Uri.fromFile(localFile).toString());
            storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Log.d("!!!", "success");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("!!!", "no success");
                }
            });


        }
    }




    @Override
    public void onMapReady(GoogleMap map) {

        this.googleMap = map;
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
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMyLocationButtonClickListener(this);
        googleMap.setOnMyLocationClickListener(this);



        for (Vehicle vehicle :vehicleList){
                if (vehicle.getLatitude() != 0){
                    googleMap.addMarker(new MarkerOptions().position(new LatLng(vehicle.getLatitude(), vehicle.getLongitude())).title(vehicle.getName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.baseline_directions_car_black_18dp)));
                    Log.d("!!!", "vehicle: " + vehicle.getName() + " Lat: " + vehicle.getLatitude() + " Lon: " + vehicle.getLongitude());

                }

        }

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                intent.putParcelableArrayListExtra("arrayListPars", vehicleList);
                startActivity(intent);
            }
        });




    }

    private File createImageFile() throws IOException {
        //create a unique filename for a picture using date/time

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        currentPhotoPath = image.getAbsolutePath();

        //   sendImageToCloud(image);                            //trying this !!!!!!

        return image;

    }

    @Override
    protected void onResume() {
        super.onResume();

        startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();

        stopLocationUpdates();
    }


    private void startLocationUpdates() {

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null );
        }

    }


    private void stopLocationUpdates() {
        locationProviderClient.removeLocationUpdates(locationCallback);

    }


    LocationRequest createLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(7000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        return locationRequest;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if( requestCode == REQUEST_LOCATION) {
            if( grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();

            } else {
                //permission denied by user
            }



        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }


    @Override
    public void onBackPressed(){

    }




}

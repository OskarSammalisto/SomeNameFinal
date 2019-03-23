package com.example.somename;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.location.Location;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
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


//    private View.OnTouchListener onTouchListener = new com.example.somename.OnSwipeTouchListener(MainActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        FirebaseFirestore db = FirebaseFirestore.getInstance();


        //location gps

        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("gps_test", "no permission");

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {

                locationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null){
                            currentLat = location.getLatitude();
                            currentLng = location.getLongitude();
                        }
                    }
                });
        }

        locationRequest = createLocationRequest();
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult){
                for (Location location : locationResult.getLocations()){
                    LatLng myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(myLatLng).zoom(17).build();
                    if (moveMapToLocation == 1){
                      // googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        moveMapToLocation = 0;
                    }

                    Log.d("gps_test", "lat: " + location.getLatitude() + " ,long: " + location.getLongitude());
                }
            }

        };



        //get arraylist
        Bundle extra = getIntent().getBundleExtra("extra");
        if (extra != null) {
            vehicleList = (ArrayList<Vehicle>) extra.getSerializable("vehicleList");
        }

//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//               // moveMapToLocation = 1;
//                Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });




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




        //fill card with vehicle
        if (vehicleList != null && !vehicleList.isEmpty()) {
            ImageView vehicleCardPic = findViewById(R.id.cardVehicleImage);
            TextView vehicleCardName = findViewById(R.id.cardVehicleName);
            TextView vehicleCardDesc = findViewById(R.id.cardVehicleDescription);

            vehicleCardName.setText(vehicleList.get(0).getName());
            vehicleCardDesc.setText(vehicleList.get(0).getDescription());

            Bitmap bitmap;

            Uri uri = Uri.parse(vehicleList.get(0).getUri());
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                vehicleCardPic.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }




    }


    @Override
    public void onMapReady(GoogleMap map) {

        this.googleMap = map;
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMyLocationButtonClickListener(this);
        googleMap.setOnMyLocationClickListener(this);
//        map.addMarker(new MarkerOptions()
//                .position(new LatLng(0, 0))
//                .title("Marker"));



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







}

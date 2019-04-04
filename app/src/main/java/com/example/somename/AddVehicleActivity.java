package com.example.somename;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddVehicleActivity extends AppCompatActivity {
//s
ArrayList<Vehicle> vehicleList = new ArrayList<>();
    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final String TAG = "AddVehicleActivity";
    private ImageView imageViewNewCarPicture;
    private Bitmap imageBitmap;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private String currentPhotoPath;
    private File photoFile;
    private FirebaseFirestore db;
    private StorageReference mStorageRef;
    private EditText newVehicleName;
    private EditText newVehicleDescription;
    private Switch setLocationSwitch;
    private FloatingActionButton confirmButton;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Double lat;
    private Double lon;
    private Double emptyLat = 0.0;
    private Double emptyLon = 0.0;
    private Double setLat = 0.0;
    private Double setLon = 0.0;
    private FirebaseAuth mAuth;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle);

        db = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();

        //set view and button variables
        newVehicleName = findViewById(R.id.newVehicleName);
        newVehicleDescription = findViewById(R.id.vehicleDescription);
        confirmButton = findViewById(R.id.createVehicleButton);
        setLocationSwitch = findViewById(R.id.addLocationSwitch);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(AddVehicleActivity.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null) {
                    lat = location.getLatitude();
                    lon = location.getLongitude();
                }

            }
        });

        setLocationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    setLat = lat;
                    setLon = lon;
                } else {
                    setLat = emptyLat;
                    setLon = emptyLon;
                }
            }
        });

        vehicleList = this.getIntent().getParcelableArrayListExtra("arrayListPars");

        ImageView imageViewNewCarPictureButton = findViewById(R.id.imageViewNewCarPicture);




        //clicking image opens camera method
        imageViewNewCarPictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dispatchTakePictureIntent();
            }
        });

        //create new vehicle and go back to vehicleListActivity.
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v) {

                boolean duplicateName = false;

                for (Vehicle vehicle : vehicleList) {
                    String temp = vehicle.getName();
                    if (temp.equals(newVehicleName.getText().toString()) ) {
                        duplicateName = true;
                    } else duplicateName = false;

                }



                //check if all fields are filled, and pick is taken
                if (photoFile == null || newVehicleName.getText().toString().length() == 0 || newVehicleDescription.getText().toString().length() == 0 || duplicateName){

                    Toast.makeText(AddVehicleActivity.this, "You must fill in name, description and take a pic", Toast.LENGTH_SHORT).show();
                }

                //make function that checks if vehicle with same name exists


                //creates vehicle and goes back to vehicleListActivity.
                else {
                    Uri uri = Uri.fromFile(photoFile);
                    String uriString = uri.toString();
                   // String uriString = uri.toString();



                    Vehicle vehicle = new Vehicle(newVehicleName.getText().toString(), newVehicleDescription.getText().toString(), uriString, setLat, setLon);
                    vehicleList.add(vehicle);
                    int vehicleListIndex = vehicleList.size() -1;

                    CollectionReference vehicleRef = db.collection("users").document(user.getUid()).collection("vehicles");


                    Intent intent = new Intent(AddVehicleActivity.this, VehicleListActivity.class);



                    intent.putParcelableArrayListExtra("arrayListPars", vehicleList);

                    //send image to fireBase cloud storage
                    sendImageToCloud(photoFile, vehicle);

                    //send object to fireStore
                    // set try catch or the like
                    vehicleRef.document(vehicle.getName()).set(vehicle);


                    startActivity(intent);
                }

            }
        });


    }


    //goes to camera
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            //create file for photo
            photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.d("PHOTO", "Photo to file error");
            }
            if (photoFile != null){

                Uri photoUri = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider", photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);




                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }

        }
    }


    //sets pic as thumbnail
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImageView imageViewNewCarPicture = findViewById(R.id.imageViewNewCarPicture);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {

            Uri uri = Uri.fromFile(photoFile);
            Bitmap bitmap;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                imageViewNewCarPicture.setImageBitmap(bitmap);
//                //send image to firebase cloud storage
//                sendImageToCloud(photoFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }


    //for creating a file for the photo taken
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


    //upload photo to fireBase storageCloud, works.
    private void sendImageToCloud(File image, final Vehicle vehicle) {
        Uri file = Uri.fromFile(image);
     // final StorageReference riversRef = mStorageRef.child("images/" +newVehicleName.getText().toString() + ".jpg");
        String userUid = mAuth.getInstance().getCurrentUser().getUid();
       final StorageReference riversRef = mStorageRef.child("images/" +userUid +"/" +newVehicleName.getText().toString() + ".jpg");
       String ref = riversRef.toString();


        riversRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        final Task<Uri> u = taskSnapshot.getMetadata().getReference().getDownloadUrl();  //this line might need to be something else but works for now.
                        vehicle.setUri(u.toString());
                        Log.d("!!!!", "upload complete" +u.toString());

                        riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Uri downloadUrl = uri;
                                String url = uri.toString();
                                vehicle.setStorageUrl(url);
                                Log.d("!!!", "stor url: " +url);

                                //vehicleList.get(vehicleList.size() -1).setUriReal(downloadUrl);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.d("UPLOAD", "FAILED");
                        // Handle unsuccessful uploads
                        // ...
                    }
                });



    }


    //upload photo to fireBase different way
//    private void imageToCloud(File image){
//        Uri file = Uri.fromFile(image);
//        UploadTask uploadTask = mStorageRef.child("images/" +newVehicleName.getText().toString() + ".jpg").putFile(file);
//
//        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//
//            }
//        })
//
//
//    }



}

package com.example.somename;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddVehicleActivity extends AppCompatActivity {
//ss
    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final String TAG = "AddVehicleActivity";
    private ImageView imageViewNewCarPicture;
    private Bitmap imageBitmap;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private String currentPhotoPath;
    private File photoFile;
    private FirebaseFirestore db;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle);

        db = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        ImageView imageViewNewCarPictureButton = findViewById(R.id.imageViewNewCarPicture);


        //clicking image opens camera method
        imageViewNewCarPictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dispatchTakePictureIntent();
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
                //send image to firebase cloud storage
                sendImageToCloud(photoFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


           // imageViewNewCarPicture.setImageBitmap(bitmap);
//            Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");
//            imageViewNewCarPicture.setImageBitmap(imageBitmap);

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
        return image;

    }


    //does not work, crashes activity. not the way to upload a photo.
    private void sendToFirestore(File photoFile) {

        db.collection("users")
                .add(photoFile)
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

    //upload photo to firebase storageCloud, works. Is currently open without authentication.
    private void sendImageToCloud(File image) {
        Uri file = Uri.fromFile(image);
        StorageReference riversRef = mStorageRef.child("images/rivers2.jpg");

        riversRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        Task<Uri> u = taskSnapshot.getMetadata().getReference().getDownloadUrl();  //this line might need to be something else but works for now.
                        Log.d("UPLOAD", "SUCCESS");
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

}

package com.example.somename;

import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class VehicleInfoActivity extends AppCompatActivity {

    ArrayList<Vehicle> vehicleList = new ArrayList<>();
    private int vehicle;
    private Uri uri;
    private String uriString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_info);

        //get arraylist
        Bundle extra = getIntent().getBundleExtra("extra");
        vehicleList = (ArrayList<Vehicle>) extra.getSerializable("vehicleList");
        vehicle = getIntent().getIntExtra("vehicle", 0);

        TextView textView = findViewById(R.id.testText);
        textView.setText("you selected vehicle nr: " +vehicle);

        ImageView imageView = findViewById(R.id.testImage);



        Bitmap bitmap;
        uriString = vehicleList.get(vehicle).getUri();
        uri = Uri.parse(uriString);
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            imageView.setImageBitmap(bitmap);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}

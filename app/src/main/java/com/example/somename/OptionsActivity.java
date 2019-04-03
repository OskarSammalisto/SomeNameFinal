package com.example.somename;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class OptionsActivity extends AppCompatActivity {

    ArrayList<Vehicle> vehicleList = new ArrayList<>();
    Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        vehicleList = this.getIntent().getParcelableArrayListExtra("arrayListPars");

        RelativeLayout layout = findViewById(R.id.optionsActivity);
        logout = findViewById(R.id.logOut);


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(OptionsActivity.this, LogInActivity.class);
                startActivity(intent);

            }
        });


        layout.setOnTouchListener(new com.example.somename.OnSwipeTouchListener(OptionsActivity.this) {
            public void onSwipeTop() {
                //  Toast.makeText(MainActivity.this, "top", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeRight() {
                Intent intent = new Intent(OptionsActivity.this, MainActivity.class);



                intent.putParcelableArrayListExtra("arrayListPars", vehicleList);

                startActivity(intent);
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                 //Toast.makeText(MainActivity.this, "right", Toast.LENGTH_SHORT).show();
            }

            public void onSwipeLeft() {
//                Intent intent = new Intent(MainActivity.this, OptionsActivity.class);
//
//
//
//                intent.putParcelableArrayListExtra("arrayListPars", vehicleList);
//
//                startActivity(intent);
//                overridePendingTransition(R.anim.left_in, R.anim.left_out);

                //   Toast.makeText(MainActivity.this, "left", Toast.LENGTH_SHORT).show();
            }

            public void onSwipeBottom() {
                //    Toast.makeText(MainActivity.this, "bottom", Toast.LENGTH_SHORT).show();
            }

        });
    }


    @Override
    public void onBackPressed(){

    }
}

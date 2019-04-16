package com.example.somename;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class OptionsActivity extends AppCompatActivity {

    ArrayList<Vehicle> vehicleList = new ArrayList<>();
    Button logout;
    Button setThemeButton;
    RadioGroup themeRadioButtons;
    private int theme;
    RadioButton radioTheme1;
    RadioButton radioTheme2;
    RadioButton radioTheme3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //get theme selection from sharedPref and apply to activity.
        SharedPreferences preferences = getSharedPreferences("MyPref", MODE_PRIVATE);
        theme = preferences.getInt("theme", 1);

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


        radioTheme1 = findViewById(R.id.theme1);
        radioTheme2 = findViewById(R.id.theme2);
        radioTheme3 = findViewById(R.id.theme3);


        if (theme == 1) {
            radioTheme1.setChecked(true);
        }
        if (theme == 2) {
            radioTheme2.setChecked(true);
        }
        if (theme == 3) {
            radioTheme3.setChecked(true);
        }
        //Save theme preference to shared preferences
        themeRadioButtons = findViewById(R.id.themeRadioGroup);
        setThemeButton = findViewById(R.id.confirmThemeButton);

        setThemeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = themeRadioButtons.getCheckedRadioButtonId();

                switch (id) {
                    case R.id.theme1:
                        theme = 1;
                        break;
                    case R.id.theme2:
                        theme = 2;
                        break;
                    case R.id.theme3:
                        theme = 3;
                        break;
                    default:
                        theme = 1;
                        break;

                }

                SharedPreferences.Editor editor = getSharedPreferences("MyPref", MODE_PRIVATE).edit();
                editor.putInt("theme", theme);
                editor.apply();
                recreate();



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

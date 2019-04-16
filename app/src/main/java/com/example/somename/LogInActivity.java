package com.example.somename;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogInActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView emailView;
    private TextView passwordView;
    private Button logInButton;
    private Button signUpButton;
    private static final int REQUEST_LOCATION = 1;
    private static final int REQUEST_CAMERA = 1;

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
        setContentView(R.layout.activity_log_in);

        mAuth = FirebaseAuth.getInstance();

        emailView = findViewById(R.id.email);
        passwordView = findViewById(R.id.password);
        logInButton = findViewById(R.id.logIn);
        signUpButton = findViewById(R.id.signUp);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("gps_test", "no permission");

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, REQUEST_CAMERA);
        }


        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null) {
            intentMainActivity();
        }

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = emailView.getText().toString();
                String password = passwordView.getText().toString();
                if (email.length() != 0 && password.length() != 0){
                    signIn(email, password);
                } else {
                    Toast.makeText(LogInActivity.this, "You must enter an email and password.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailView.getText().toString();
                String password = passwordView.getText().toString();

                if (email.length() != 0 && password.length() != 0){
                    createAccount(email, password);
                } else {
                    Toast.makeText(LogInActivity.this, "You must enter an email and password.", Toast.LENGTH_SHORT).show();
                }


            }
        });


    }

    private void intentMainActivity() {
        Intent intent = new Intent(LogInActivity.this, MainActivity.class);
        startActivity(intent);

    }

    private void createAccount (final String email, final String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    signIn(email, password);
                } else {
                    Log.d("!!!!", "failed creating user" , task.getException());
                    Toast.makeText(LogInActivity.this,"User creation failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    intentMainActivity();
                } else {
                    Toast.makeText(LogInActivity.this, "Sign In failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onBackPressed(){

    }






//    public void confirmSignIn (View view) {
//        String email = emailView.getText().toString();
//        String password = passwordView.getText().toString();
//
//        signIn(email, password);
//
//    }
//
//    public void confirmSignUp (View view) {
//        String email = emailView.getText().toString();
//        String password = passwordView.getText().toString();
//
//        createAccount(email, password);
//    }


}

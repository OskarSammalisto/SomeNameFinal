package com.example.somename;

import android.content.Intent;
import android.support.annotation.NonNull;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        mAuth = FirebaseAuth.getInstance();

        emailView = findViewById(R.id.email);
        passwordView = findViewById(R.id.password);
        logInButton = findViewById(R.id.logIn);
        signUpButton = findViewById(R.id.signUp);


        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null) {
            intentMainActivity();
        }

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailView.getText().toString();
                String password = passwordView.getText().toString();

                signIn(email, password);
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailView.getText().toString();
                String password = passwordView.getText().toString();

                createAccount(email, password);
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

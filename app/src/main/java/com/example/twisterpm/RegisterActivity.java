package com.example.twisterpm;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    EditText mEmail, mPassword;
    Button mRegisterButton;
    TextView mGoToLoginTextView;
    FirebaseAuth fAuth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mEmail = findViewById(R.id.editTextTextEmailAddress);
        mPassword = findViewById(R.id.editTextTextPassword);
        mRegisterButton = findViewById(R.id.registerButton);
        mGoToLoginTextView = findViewById(R.id.goToLoginTextView);

        fAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);

    }
}
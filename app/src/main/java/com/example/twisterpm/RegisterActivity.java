package com.example.twisterpm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    EditText mEmail, mPassword, mPasswordConfirmation;
    Button mRegisterButton;
    TextView mGoToLoginTextView;
    FirebaseAuth fAuth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Log.d("KIMON", "Register Activity: onCreate");
        mEmail = findViewById(R.id.registerEmail);
        mPassword = findViewById(R.id.registerPassword);
        mPasswordConfirmation = findViewById(R.id.registerPasswordConfirmation);
        mRegisterButton = findViewById(R.id.registerButton);
        mGoToLoginTextView = findViewById(R.id.goToLoginTextView);

        fAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);

        if (fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), AllMessagesActivity.class));
            finish();
        }

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                String passwordConfirmation = mPasswordConfirmation.getText().toString().trim();

                if (email.isEmpty()) {
                    mEmail.setError("Email is required");
                    return;
                }

                if (password.isEmpty()) {
                    mPassword.setError("Password is required");
                    return;
                }

                if (passwordConfirmation.isEmpty()) {
                    mPassword.setError("Password confirmation is required");
                    return;
                }

                if (password.length() < 6) {
                    mPassword.setError("Password must contain 6 characters or more");
                    return;
                }

                if (!password.equals(passwordConfirmation)) {
                    mPasswordConfirmation.setError("Passwords do not match");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                // Register the user in firebase
                fAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(RegisterActivity.this, "Account created", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(getApplicationContext(), AllMessagesActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this, "Registration failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });

        mGoToLoginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            }
        });
    }
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }
}


package com.example.twisterpm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsActivity extends AppCompatActivity {

    EditText newPassword, newPasswordConfirmation, newEmail;
    Button updatePasswordButton;
    Button updateEmailButton;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        newPassword = findViewById(R.id.newPasswordEditText);
        newPasswordConfirmation = findViewById(R.id.newPasswordConfirmationEditText);
        updatePasswordButton = findViewById(R.id.updatePasswordButton);
        newEmail = findViewById(R.id.newEmailEditText);
        updateEmailButton = findViewById(R.id.updateEmailButton);
        user = FirebaseAuth.getInstance().getCurrentUser();

        updatePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newPassword.getText().toString().trim().isEmpty()) {
                    newPassword.setError("Required field");
                    return;
                }

                if (newPassword.length() < 6) {
                    newPassword.setError("Password must contain 6 characters or more");
                    return;
                }

                if (newPasswordConfirmation.getText().toString().trim().isEmpty()) {
                    newPasswordConfirmation.setError("Required field");
                    return;
                }

                if (newPasswordConfirmation.length() < 6) {
                    newPasswordConfirmation.setError("Password must contain 6 characters or more");
                    return;
                }

                if (!newPassword.getText().toString().trim().equals(newPasswordConfirmation.getText().toString().trim())) {
                    newPasswordConfirmation.setError("Passwords do not match");
                    return;
                }

                user.updatePassword(newPassword.getText().toString().trim()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(SettingsActivity.this, "Password changed", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SettingsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });


        updateEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (newEmail.getText().toString().trim().isEmpty()) {
                    newEmail.setError("Required field");
                    return;
                }

                if(newEmail.getText().toString().trim().equals(user.getEmail())){
                    newEmail.setError("Please select a different e-mail");
                    return;
                }

                user.updateEmail(newEmail.getText().toString().trim()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(SettingsActivity.this, "E-mail changed", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SettingsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });





    }
}
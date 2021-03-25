package com.example.twisterpm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        Log.d("KIMON", "Settings Activity: onCreateOptionsMenu");
        //getMenuInflater().inflate(R.menu.menu_bottom, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Log.d("KIMON", "Settings Activity: onOptionsItemSelected");

        switch (id) {
            case R.id.action_settings:
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                break;
            case R.id.action_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                finish();
                break;
            case R.id.action_allMessages:
                startActivity(new Intent(getApplicationContext(), AllMessagesActivity.class));
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Settings");
        setSupportActionBar(toolbar);
        Log.d("KIMON", "Settings Activity: onCreate");
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
                        startActivity(new Intent(getApplicationContext(), AllMessagesActivity.class));
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
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }
}
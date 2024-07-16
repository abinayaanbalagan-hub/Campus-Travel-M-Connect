package com.example.bustrack;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Change_password extends AppCompatActivity {
    private EditText newPasswordEditText, confirmNewPasswordEditText;
    private Button changePasswordButton;
    private DatabaseReference mDatabase;
    private String currentUserRollNo, currentUserMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);

        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        confirmNewPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        changePasswordButton = findViewById(R.id.changePasswordButton);

        // Initialize Firebase Database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Get the roll number, department, and mode from the intent
        currentUserRollNo = getIntent().getStringExtra("Username");
        currentUserMode = getIntent().getStringExtra("lMode");

        if (currentUserRollNo == null || currentUserRollNo.isEmpty()) {
            Toast.makeText(this, "Data not found", Toast.LENGTH_SHORT).show();
            return;
        }

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });
    }

    private void changePassword() {
        String newPassword = newPasswordEditText.getText().toString().trim();
        String confirmNewPassword = confirmNewPasswordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmNewPassword)) {
            Toast.makeText(Change_password.this, "Please fill out both fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmNewPassword)) {
            Toast.makeText(Change_password.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Determine the correct database path based on the user mode
        String rolePath;
        if ("Student".equals(currentUserMode)) {
            rolePath = "Student";
        } else {
            rolePath = "Driver";
        }

        // Update the password in the database
        mDatabase.child(rolePath).child(currentUserRollNo).child("password").setValue(newPassword)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mDatabase.child(rolePath).child(currentUserRollNo).child("firstLogin").setValue(false)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(Change_password.this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                                        // Redirect or close activity
                                        Intent intent = new Intent(Change_password.this, LoginActivity.class);
                                        startActivity(intent);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Change_password.this, "Failed to update firstLogin status", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Change_password.this, "Failed to change password", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

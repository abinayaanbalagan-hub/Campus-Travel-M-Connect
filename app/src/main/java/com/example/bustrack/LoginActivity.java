package com.example.bustrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class LoginActivity extends AppCompatActivity {

    EditText editTextUsername;
    EditText editTextPassword;
    Button buttonLogin;
    RadioButton Radio_Driver, Radio_student;
    RadioGroup Radio_loginmode;
    private RadioButton selectedUserTypeRadioButton;

    // Firebase Authentication

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_login);

        // Initialize Firebase Authentication
        mDatabase = FirebaseDatabase.getInstance ().getReference ();

        // Initialize views
        editTextUsername = findViewById (R.id.editTextUsername);
        editTextPassword = findViewById (R.id.editTextPassword);
        buttonLogin = findViewById (R.id.buttonLogin);
        Radio_Driver = findViewById (R.id.radioButtonDriver);
        Radio_student = findViewById (R.id.radioButtonstudent);
        Radio_loginmode = findViewById (R.id.radioGrouploginmode);

        // Set click listener for Login button
        buttonLogin.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                login ();
            }
        });
    }

    private void login() {
        String username = editTextUsername.getText ().toString ().trim ();
        String password = editTextPassword.getText ().toString ().trim ();
        int selectedUserTypeId = Radio_loginmode.getCheckedRadioButtonId ();
        if (selectedUserTypeId == -1) {
            if(username.equals ("Admin")&&password.equals ("Admin123")){
                Intent intent=new Intent (LoginActivity.this,AdminActivity.class);
                startActivity (intent);
            }else {
                Toast.makeText (LoginActivity.this, "Please select user type", Toast.LENGTH_SHORT).show ();
                return;
            }
        }
        selectedUserTypeRadioButton = findViewById (selectedUserTypeId);
        final String userType = selectedUserTypeRadioButton.getText ().toString ();
        // Check if username and password are empty
        if (username.isEmpty () || password.isEmpty ()) {
            Toast.makeText (this, "Please enter username and password", Toast.LENGTH_SHORT).show ();
            return;
        }

        // Authenticate user with Firebase
        mDatabase.child (userType).child (username).addListenerForSingleValueEvent (new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists ()) {

                    String storedPassword = dataSnapshot.child ("password").getValue (String.class);
                    if (storedPassword != null && storedPassword.equals (password)) {
                        if (userType.equals ("Driver")) {
                            Boolean firstlogin=dataSnapshot.child ("firstLogin").getValue (Boolean.class);
                            if(firstlogin.equals (true)){
                                Intent intent=new Intent(LoginActivity.this,Change_password.class);
                                intent.putExtra ("Username",username);
                                intent.putExtra ("lMode","Driver");
                                startActivity (intent);
                            }else{
                            Intent intent = new Intent (LoginActivity.this, DriverActivity.class);
                            startActivity (intent);
                            }
                            finish ();
                        } else {
                            String rollno=dataSnapshot.child ("Roll No").getValue ().toString ();
                            String department= dataSnapshot.child ("Department").getValue ().toString ();
                            // Redirect to HomeActivity
                            Intent intent = new Intent (LoginActivity.this, Student_Home.class);
                            intent.putExtra ("Roll No",rollno);
                            intent.putExtra ("Department",department);

                            startActivity (intent);
                            finish ();
                        }
                    } else {
                        Toast.makeText (LoginActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show ();
                    }
                } else {
                    Toast.makeText (LoginActivity.this, "User not found", Toast.LENGTH_SHORT).show ();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText (LoginActivity.this, "Database error", Toast.LENGTH_SHORT).show ();
            }
        });

    }

}
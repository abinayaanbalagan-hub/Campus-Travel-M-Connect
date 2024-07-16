package com.example.bustrack;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bustrack.AddDriver;
import com.example.bustrack.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class DriverReportActivity extends AppCompatActivity {

    private EditText driverNumberEditText;
    private Button submitButton, printButton;
    private LinearLayout driverDetailsLayout;
    private TextView driverNameTextView, licenseNumberTextView, ageTextView, contactNumberTextView, emailTextView;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_report);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Driver");

        // Initialize UI elements
        driverNumberEditText = findViewById(R.id.driver_number_edit_text);
        submitButton = findViewById(R.id.submit_button);
        printButton = findViewById(R.id.print_button);
        driverDetailsLayout = findViewById(R.id.driver_details_layout);
        driverNameTextView = findViewById(R.id.driver_name_text_view);
        licenseNumberTextView = findViewById(R.id.license_number_text_view);
        ageTextView = findViewById(R.id.age_text_view);
        contactNumberTextView = findViewById(R.id.contact_number_text_view);
        emailTextView = findViewById(R.id.email_text_view);

        // Button click listeners
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String driverNumber = driverNumberEditText.getText().toString().trim();
                if (!driverNumber.isEmpty()) {
                    fetchDriverDetails(driverNumber);
                } else {
                    Toast.makeText(DriverReportActivity.this, "Please enter a driver number", Toast.LENGTH_SHORT).show();
                }
            }
        });

        printButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printToPDF();
            }
        });
    }

    private void fetchDriverDetails(String driverNumber) {
        // Query to fetch driver details based on driver number
        Query query = databaseReference.orderByChild("contactNumber").equalTo(driverNumber);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        AddDriver driver = snapshot.getValue(AddDriver.class);
                        if (driver != null) {
                            displayDriverDetails(driver);
                        }
                    }
                } else {
                    Toast.makeText(DriverReportActivity.this, "Driver not found", Toast.LENGTH_SHORT).show();
                    hideDriverDetails();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("DriverReportActivity", "Error fetching driver details: " + databaseError.getMessage());
                Toast.makeText(DriverReportActivity.this, "Error fetching driver details", Toast.LENGTH_SHORT).show();
                hideDriverDetails();
            }
        });
    }

    private void displayDriverDetails(AddDriver driver) {
        driverNameTextView.setText("Driver Name: " + driver.getName());
        licenseNumberTextView.setText("License Number: " + driver.getLicenseNumber());
        ageTextView.setText("Age: " + driver.getDob());
        contactNumberTextView.setText("Contact Number: " + driver.getContactNumber());
        emailTextView.setText("Email: " + driver.getEmail());

        driverDetailsLayout.setVisibility(View.VISIBLE);
    }

    private void hideDriverDetails() {
        driverDetailsLayout.setVisibility(View.GONE);
    }

    private void printToPDF() {
        // Code to generate and save PDF from displayed data
        // Replace with your logic to create PDF
        Toast.makeText(this, "Printing to PDF...", Toast.LENGTH_SHORT).show();
    }
}

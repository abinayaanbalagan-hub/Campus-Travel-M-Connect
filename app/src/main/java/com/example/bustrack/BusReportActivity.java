package com.example.bustrack;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BusReportActivity extends AppCompatActivity {

    private EditText editTextBusNo;
    private Button buttonSubmit, buttonPrint;
    private LinearLayout linearDataContainer;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_report);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("busDetails");

        // Initialize views
        editTextBusNo = findViewById(R.id.editTextBusNo);
        buttonSubmit = findViewById(R.id.buttonSubmit);
        buttonPrint = findViewById(R.id.buttonPrint);
        linearDataContainer = findViewById(R.id.linearDataContainer);

        // Submit button click listener
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String busNo = editTextBusNo.getText().toString().trim();
                if (!busNo.isEmpty()) {
                    fetchBusDetails(busNo);
                } else {
                    Toast.makeText(BusReportActivity.this, "Please enter a Bus Number", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Print button click listener (for future implementation)
        buttonPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement PDF printing functionality here
                Toast.makeText(BusReportActivity.this, "Printing functionality will be implemented", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchBusDetails(String busNo) {
        DatabaseReference busRef = databaseReference.child(busNo);
        busRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Clear previous data
                    linearDataContainer.removeAllViews();

                    // Retrieve bus details and display
                    String startingPoint = snapshot.child("StartingPoint").getValue(String.class);
                    String endingPoint = snapshot.child("Ending Point").getValue(String.class);
                    String departureTime = snapshot.child("DepartureTime").getValue(String.class);
                    String arrivalTime = snapshot.child("ArrivalTime").getValue(String.class);
                    int totalSeats = snapshot.child("totalSeats").getValue(Integer.class);

                    // Display data in LinearLayout
                    displayData(startingPoint, endingPoint, departureTime, arrivalTime, totalSeats);
                } else {
                    Toast.makeText(BusReportActivity.this, "Bus details not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BusReportActivity.this, "Failed to fetch bus details: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayData(String startingPoint, String endingPoint, String departureTime, String arrivalTime, int totalSeats) {
        TextView textViewStartingPoint = createTextView("Starting Point: " + startingPoint);
        TextView textViewEndingPoint = createTextView("Ending Point: " + endingPoint);
        TextView textViewDepartureTime = createTextView("Departure Time: " + departureTime);
        TextView textViewArrivalTime = createTextView("Arrival Time: " + arrivalTime);
        TextView textViewTotalSeats = createTextView("Total Seats: " + totalSeats);

        linearDataContainer.addView(textViewStartingPoint);
        linearDataContainer.addView(textViewEndingPoint);
        linearDataContainer.addView(textViewDepartureTime);
        linearDataContainer.addView(textViewArrivalTime);
        linearDataContainer.addView(textViewTotalSeats);
    }

    private TextView createTextView(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextSize(16);
        textView.setPadding(16, 8, 16, 8);
        return textView;
    }
}

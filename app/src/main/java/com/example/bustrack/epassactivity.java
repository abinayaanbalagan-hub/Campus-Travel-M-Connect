package com.example.bustrack;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class epassactivity extends AppCompatActivity {

    private ImageView studentImage;
    private TextView collegeNameTextView;
    private TextView epassDetailsTextView;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private String busNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_epass);

        // Initialize views
        studentImage = findViewById(R.id.studentImage);
        collegeNameTextView = findViewById(R.id.collegeName);
        epassDetailsTextView = findViewById(R.id.epassDetails);

        // Get roll number and bus number from intent
        String rollNo = getIntent().getStringExtra("Roll No").trim();
        String department = getIntent().getStringExtra("Department");

        database = FirebaseDatabase.getInstance();

        DatabaseReference busNoReference = database.getReference().child("Add_Student").child(department).child(rollNo);
        busNoReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    busNo = snapshot.child("Bus No").getValue(String.class);
                    Toast.makeText(epassactivity.this, busNo, Toast.LENGTH_SHORT).show();

                    // Now that we have the busNo, fetch the student data
                    fetchStudentData(busNo, rollNo);
                } else {
                    Toast.makeText(epassactivity.this, "No bus number found for this student", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(epassactivity.this, "Failed to fetch bus number", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchStudentData(String busNo, String rollNo) {
        reference = database.getReference().child("Student Details").child(busNo).child(rollNo.toUpperCase());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("Name").getValue(String.class);
                    String boardingPlace = snapshot.child("Boarding Place").getValue(String.class);
                    String course = snapshot.child("Course").getValue(String.class);
                    String semester = snapshot.child("Semester").getValue(String.class);
                    String batch = snapshot.child("Batch").getValue(String.class);
                    String gender = snapshot.child("Gender").getValue(String.class);
                    String mobileNo = snapshot.child("Mobile No").getValue(String.class);
                    String fees = snapshot.child("Fees").getValue(String.class);
                    String validDate = snapshot.child("Valid Till").getValue(String.class);
                    String validfrom=snapshot.child ("Valid From").getValue(String.class);
                    String imageUrl = snapshot.child("imageUrl").getValue(String.class);

                    if ("Paid".equals(fees)) {
                        // Create e-pass details
                        String epassDetails = "Name: " + name + "\n" +
                                "Roll No: " + rollNo + "\n" +
                                "Bus No: " + busNo + "\n" +
                                "Boarding Place: " + boardingPlace + "\n" +
                                "Course: " + course + "\n" +
                                "Semester: " + semester + "\n" +
                                "Batch: " + batch + "\n" +
                                "Gender: " + gender + "\n" +
                                "Mobile No: " + mobileNo + "\n" +
                                "Fees: " + fees + "\n" +
                                "Valid From: "+validfrom+
                                "Valid Till: " + validDate;

                        // Set e-pass details to the TextView
                        epassDetailsTextView.setText(epassDetails);

                        // Debugging information


                        // Load image into ImageView using Glide
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            Glide.with(epassactivity.this)
                                    .load(imageUrl)
                                    .into(studentImage);
                        }
                        else {
                            Toast.makeText(epassactivity.this, "Image URL is not found or empty", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        epassDetailsTextView.setText("Your payment is not received yet. After a successful payment, your e-pass will be generated.");
                    }

                } else {
                    Toast.makeText(epassactivity.this, "No data found for this student", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(epassactivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
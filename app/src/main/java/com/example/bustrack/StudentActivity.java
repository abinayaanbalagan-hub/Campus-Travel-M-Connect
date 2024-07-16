package com.example.bustrack;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StudentActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "StudentActivity";
    private GoogleMap mMap;
    private DatabaseReference databaseReference;
    private Marker busMarker;
    private String busNo;
    private TextView statusTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student);

        statusTextView = findViewById(R.id.statusTextView);
        updateStatus("Initializing...");

        String rollNo = getIntent().getStringExtra("Roll No");
        String department = getIntent().getStringExtra("Department");

        if (rollNo == null || department == null) {
            updateStatus("Error: Roll No or Department is null");
            return;
        }

        Log.d(TAG, "Fetching data for Roll No: " + rollNo + ", Department: " + department);
        updateStatus("Fetching student data...");

        DatabaseReference studentRef = FirebaseDatabase.getInstance().getReference()
                .child("Add_Student").child(department).child(rollNo);

        studentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "Student data snapshot: " + snapshot.toString());
                if (snapshot.exists()) {
                    busNo = snapshot.child("Bus No").getValue(String.class);
                    if (busNo != null && !busNo.isEmpty()) {
                        Log.d(TAG, "Bus No found: " + busNo);
                        updateStatus("Bus No found: " + busNo);
                        setupBusLocationListener();
                    } else {
                        Log.e(TAG, "Bus No is null or empty");
                        updateStatus("Error: Bus number not found");
                    }
                } else {
                    Log.e(TAG, "Student data not found for Roll No: " + rollNo + ", Department: " + department);
                    updateStatus("Error: Student data not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Database error: " + error.getMessage());
                updateStatus("Error: " + error.getMessage());
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            updateStatus("Error: Map fragment not found");
        }
    }

    private void setupBusLocationListener() {
        databaseReference = FirebaseDatabase.getInstance().getReference("busDetails")
                .child(busNo).child("Current Location");
        listenForBusLocationUpdates();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        updateStatus("Map is ready");
    }

    private void listenForBusLocationUpdates() {
        if (databaseReference != null) {
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d(TAG, "Bus location data snapshot: " + dataSnapshot.toString());
                    if (dataSnapshot.exists()) {
                        Double latitude = dataSnapshot.child("latitude").getValue(Double.class);
                        Double longitude = dataSnapshot.child("longitude").getValue(Double.class);
                        if (latitude != null && longitude != null) {
                            LatLng busLocation = new LatLng(latitude, longitude);
                            updateBusLocationOnMap(busLocation);
                            updateStatus("Bus location updated");
                        } else {
                            Log.e(TAG, "Invalid location data: lat=" + latitude + ", lon=" + longitude);
                            updateStatus("Error: Invalid location data");
                        }

                        String delay = dataSnapshot.child("delay").getValue(String.class);
                        updateDelayInfo(delay);
                    } else {
                        Log.e(TAG, "No location data available for bus: " + busNo);
                        updateStatus("No location data available");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "Failed to get location data: " + databaseError.getMessage());
                    updateStatus("Error: Failed to get location data");
                }
            });
        } else {
            Log.e(TAG, "databaseReference is null");
            updateStatus("Error: Database reference is null");
        }
    }

    private void updateBusLocationOnMap(LatLng busLocation) {
        if (mMap != null) {
            if (busMarker == null) {
                busMarker = mMap.addMarker(new MarkerOptions().position(busLocation).title("Bus Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(busLocation, 15));
            } else {
                busMarker.setPosition(busLocation);
            }
            mMap.animateCamera(CameraUpdateFactory.newLatLng(busLocation));
        } else {
            Log.e(TAG, "Google Map is not initialized");
            updateStatus("Error: Google Map is not initialized");
        }
    }

    private void updateDelayInfo(String delay) {
        TextView delayTxt = findViewById(R.id.Delay);
        if (delayTxt != null) {
            if (delay != null && !delay.isEmpty()) {
                delayTxt.setText(delay);
                updateStatus("Delay info: " + delay);
            } else {
                delayTxt.setText("No delay information available");
                updateStatus("No delay information available");
            }
        } else {
            Log.e(TAG, "Delay TextView not found");
            updateStatus("Error: Delay TextView not found");
        }
    }

    private void updateStatus(String message) {
        Log.d(TAG, "Status: " + message);
        runOnUiThread(() -> {
            if (statusTextView != null) {
                statusTextView.setText(message);
            } else {
                Log.e(TAG, "statusTextView is null");
            }
            Toast.makeText(StudentActivity.this, message, Toast.LENGTH_SHORT).show();
        });
    }
}
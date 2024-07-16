package com.example.bustrack;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DriverActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private DatabaseReference databaseReference;
    private Button btnStartLocation, btnStopLocation;
    private boolean trackingLocation = false;
    private List<String> spinnerValues;
    private String selectedBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver);

        btnStartLocation = findViewById(R.id.btnStartLocation);
        btnStopLocation = findViewById(R.id.btnStopLocation);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        databaseReference = FirebaseDatabase.getInstance().getReference("busDetails");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btnStartLocation.setOnClickListener(v -> confirmDrivingBus());
        btnStopLocation.setOnClickListener(v -> stopLocationUpdates());
    }

    private void confirmDrivingBus() {
        spinnerValues = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("busDetails");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String value = dataSnapshot.getKey();
                    spinnerValues.add(value);
                }
                showConfirmDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DriverActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showConfirmDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_confirm, null);

        Spinner spinner = dialogView.findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerValues);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setTitle("Select Bus")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedBus = spinner.getSelectedItem().toString();
                        startLocationUpdates(selectedBus);
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    private void startLocationUpdates(String selectedBus) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        trackingLocation = true;
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                updateLocationOnMap(location);
                updateLocationInDatabase(location, selectedBus);
            } else {
                Toast.makeText(DriverActivity.this, "Failed to get location, try again later", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(DriverActivity.this, "Failed to get location: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void stopLocationUpdates() {
        trackingLocation = false;
        Toast.makeText(this, "Location Tracking Stopped", Toast.LENGTH_SHORT).show();
    }

    private void updateLocationOnMap(Location location) {
        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(currentLocation).title("Current Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
    }

    private void updateLocationInDatabase(Location location, String selectedBus) {
        Map<String, Object> locationData = new HashMap<>();
        locationData.put("latitude", location.getLatitude());
        locationData.put("longitude", location.getLongitude());

        databaseReference.child(selectedBus).child("Current Location").updateChildren(locationData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(DriverActivity.this, "Location Updated", Toast.LENGTH_SHORT).show();
                checkForDelays(selectedBus, location);
            } else {
                Toast.makeText(DriverActivity.this, "Location Update Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkForDelays(String selectedBus, Location currentLocation) {
        DatabaseReference stopsRef = databaseReference.child(selectedBus).child("stops");
        stopsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot stopSnapshot : snapshot.getChildren()) {
                    String stopName = stopSnapshot.getKey();
                    String arrivalTime = stopSnapshot.child("arrivalTime").getValue(String.class);

                    if (arrivalTime != null) {
                        calculateDelay(stopName, arrivalTime);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DriverActivity.this, "Failed to fetch stops data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void calculateDelay(String stopName, String arrivalTime) {
        try {
            long currentTimeMinutes = getCurrentTimeInMinutes();
            long arrivalTimeMinutes = parseTimeToMinutes(arrivalTime);
            long minutesDifference = currentTimeMinutes - arrivalTimeMinutes;

            String delayMessage;
            if (minutesDifference < 0) {
                delayMessage = stopName + " " + Math.abs(minutesDifference) + " minutes early";
            } else {
                delayMessage = stopName + " " + minutesDifference + " minutes late";
            }

            DatabaseReference delayRef = FirebaseDatabase.getInstance().getReference("busDetails")
                    .child(selectedBus).child("Current Location").child("delay");
            delayRef.setValue(delayMessage);

            Toast.makeText(DriverActivity.this, delayMessage, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(DriverActivity.this, "Error calculating delay: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private long getCurrentTimeInMinutes() {
        long currentTimeMillis = System.currentTimeMillis();
        return TimeUnit.MILLISECONDS.toMinutes(currentTimeMillis) % (24 * 60);
    }

    private long parseTimeToMinutes(String time) {
        String[] parts = time.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        return hours * 60 + minutes;
    }
}

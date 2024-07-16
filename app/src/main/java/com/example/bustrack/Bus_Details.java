package com.example.bustrack;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Bus_Details extends AppCompatActivity {

    private static final int PLACE_PICKER_REQUEST_1 = 1;
    private static final int PLACE_PICKER_REQUEST_2 = 2;
    private static final int MAP_PICKER_REQUEST = 100;

    private EditText  editTextStartingPoint, editTextEndingPoint, editTextDepartureTime, editTextArrivalTime,totalSeats;
    private EditText editTextStop1Latitude, editTextStop1Longitude, editTextStop2Latitude, editTextStop2Longitude,Fees1,Fees2,stopping1name,stopping2name,Arrivaltime1,Arrivaltime2;
    private ImageButton mapIcon1, mapIcon2;
    private Button buttonSubmit, buttonAddStop;
    private LinearLayout stoppingPointsContainer, Stopping;
    private DatabaseReference databaseReference;
    private int stopCount = 2;
    private Map<Integer, EditText> latitudeFields = new HashMap<>();
    private Map<Integer, EditText> longitudeFields = new HashMap<>();
    private Map<Integer, EditText> stoppingNameFields = new HashMap<>();
    private Map<Integer, EditText> feesFields = new HashMap<>();
    private Map<Integer, EditText> Arrivaltimefields = new HashMap<>();
    private Spinner Busno;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.busdetails);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("busDetails");

        // Initialize views
        editTextStartingPoint = findViewById(R.id.editTextStartingPoint);
        editTextEndingPoint = findViewById(R.id.editTextEndingPoint);
        editTextDepartureTime = findViewById(R.id.editTextDepartureTime);
        editTextArrivalTime = findViewById(R.id.editTextArrivalTime);
        editTextStop1Latitude = findViewById(R.id.editTextStop1Latitude);
        editTextStop1Longitude = findViewById(R.id.editTextStop1Longitude);
        editTextStop2Latitude = findViewById(R.id.editTextStop2Latitude);
        editTextStop2Longitude = findViewById(R.id.editTextStop2Longitude);
        mapIcon1 = findViewById(R.id.mapIcon1);
        mapIcon2 = findViewById(R.id.mapIcon2);
        buttonSubmit = findViewById(R.id.buttonSubmit);
        buttonAddStop = findViewById(R.id.buttonAddStop);
        stoppingPointsContainer = findViewById(R.id.stoppingPointsContainer);
        Stopping = findViewById(R.id.stopping);
        Fees1=findViewById (R.id.Fees1);
        Fees2=findViewById (R.id.Fees2);
        stopping1name=findViewById (R.id.stopping_name_1);
        stopping2name=findViewById (R.id.stopping_name_2);
        Busno=findViewById (R.id.BusNo);
        Arrivaltime1=findViewById (R.id.Arrivaltime1);
        Arrivaltime2=findViewById (R.id.Arrivaltime2);
        totalSeats=findViewById (R.id.totalSeats);

        // Set up TimePicker for departure and arrival times
        editTextDepartureTime.setOnClickListener(v -> showTimePicker(editTextDepartureTime));
        editTextArrivalTime.setOnClickListener(v -> showTimePicker(editTextArrivalTime));

        // Map icon click listeners
        mapIcon1.setOnClickListener(v -> pickLocation(PLACE_PICKER_REQUEST_1));
        mapIcon2.setOnClickListener(v -> pickLocation(PLACE_PICKER_REQUEST_2));


        List<String> Bus = new ArrayList<> ();
        Bus.add("01");
        Bus.add("02");
        Bus.add("03");
        Bus.add("04");
        Bus.add("05");
        Bus.add("06");
        Bus.add("07");
        Bus.add("08");
        Bus.add("09");
        Bus.add("10");
        Bus.add("11");
        Bus.add("12");
        Bus.add("13");
        Bus.add("14");
        Bus.add("15");
        // Add other bus numbers
        ArrayAdapter<String> select_Bus = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Bus);
        select_Bus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Busno.setAdapter(select_Bus);


        // Submit button click listener
        buttonSubmit.setOnClickListener(v -> {
            if (validateInputs()) {
                submitBusDetails();

            }
        });

        // Button to dynamically add stopping points
        buttonAddStop.setOnClickListener(v -> addStop());
    }

    private void pickLocation(int requestCode) {
        Intent intent = new Intent(this, MappickerActivity.class);
        startActivityForResult(intent, requestCode);
    }

    private void addStop() {
        LinearLayout outerLayout = new LinearLayout(this);
        outerLayout.setOrientation(LinearLayout.VERTICAL);
        outerLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        LinearLayout firstLineLayout = new LinearLayout(this);
        firstLineLayout.setOrientation(LinearLayout.HORIZONTAL);
        firstLineLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView textViewStopping = new TextView(this);
        textViewStopping.setText("Stopping Point " + (stopCount + 1));
        textViewStopping.setTextSize(16);
        textViewStopping.setTypeface(Typeface.DEFAULT_BOLD);
        textViewStopping.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));

        ImageButton mapPicker = new ImageButton(this);
        mapPicker.setImageDrawable(getDrawable(R.drawable.ic_map));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 40, 0);
        mapPicker.setLayoutParams(layoutParams);

        firstLineLayout.addView(textViewStopping);
        firstLineLayout.addView(mapPicker);

        EditText stoppingName = new EditText(this);
        stoppingName.setHint("Stopping Name");
        stoppingName.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        LinearLayout latLngLayout = new LinearLayout(this);
        latLngLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        EditText stopLatitude = new EditText(this);
        stopLatitude.setHint("Latitude");
        stopLatitude.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        stopLatitude.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));

        EditText stopLongitude = new EditText(this);
        stopLongitude.setHint("Longitude");
        stopLongitude.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        stopLongitude.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));

        EditText fees=new EditText (this);
        fees.setHint ("Enter Fees");
        fees.setInputType (InputType.TYPE_CLASS_NUMBER);
        fees.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        EditText arrivaltime=new EditText (this);
        arrivaltime.setHint ("Enter Arrival Time");
        arrivaltime.setInputType (InputType.TYPE_DATETIME_VARIATION_TIME);
        arrivaltime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker(arrivaltime);
            }
        });
        arrivaltime.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        latLngLayout.addView(stopLatitude);
        latLngLayout.addView(stopLongitude);

        outerLayout.addView(firstLineLayout);
        outerLayout.addView(stoppingName);
        outerLayout.addView(latLngLayout);
        outerLayout.addView(fees);
        outerLayout.addView(arrivaltime);

        stoppingPointsContainer.addView(outerLayout);

        latitudeFields.put(stopCount, stopLatitude);
        longitudeFields.put(stopCount, stopLongitude);
        stoppingNameFields.put(stopCount, stoppingName);
        feesFields.put(stopCount, fees);
        Arrivaltimefields.put(stopCount, arrivaltime);

        final int stopIndex = stopCount;
        mapPicker.setOnClickListener(v -> pickLocation(MAP_PICKER_REQUEST + stopIndex));

        stopCount++;
    }

    private void showTimePicker(final EditText timeEditText) {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minuteOfHour) -> {
                    String time = String.format("%02d:%02d", hourOfDay, minuteOfHour);
                    timeEditText.setText(time);
                }, hour, minute, true);
        timePickerDialog.show();
    }

    private boolean validateInputs() {

        if (TextUtils.isEmpty(editTextStartingPoint.getText().toString())) {
            editTextStartingPoint.setError("Starting point is required");
            return false;
        }
        if (TextUtils.isEmpty(editTextEndingPoint.getText().toString())) {
            editTextEndingPoint.setError("Ending point is required");
            return false;
        }
        if (TextUtils.isEmpty(editTextDepartureTime.getText().toString())) {
            editTextDepartureTime.setError("Departure time is required");
            return false;
        }
        if (TextUtils.isEmpty(editTextArrivalTime.getText().toString())) {
            editTextArrivalTime.setError("Arrival time is required");
            return false;
        }
        if (TextUtils.isEmpty(totalSeats.getText().toString())) {
            editTextArrivalTime.setError("Seat Count is required");
            return false;
        }
        if (TextUtils.isEmpty(editTextStop1Latitude.getText().toString())) {
            editTextStop1Latitude.setError("Latitude is required");
            return false;
        }
        if (TextUtils.isEmpty(editTextStop2Latitude.getText().toString())) {
            editTextStop2Latitude.setError("Latitude is required");
            return false;
        }
        if (TextUtils.isEmpty(editTextStop1Longitude.getText().toString())) {
            editTextStop1Longitude.setError("Longitude is required");
            return false;
        }
        if (TextUtils.isEmpty(editTextStop2Longitude.getText().toString())) {
            editTextStop2Longitude.setError("Longitude is required");
            return false;
        }
        if (TextUtils.isEmpty(stopping2name.getText().toString())) {
            stopping2name.setError("stopping1name is required");
            return false;
        }
        if (TextUtils.isEmpty(stopping2name.getText().toString())) {
            stopping2name.setError("stopping2name is required");
            return false;
        }
        if (TextUtils.isEmpty(Fees1.getText().toString())) {
            Fees1.setError("Fees is required");
            return false;
        }
        if (TextUtils.isEmpty(Fees2.getText().toString())) {
            Fees2.setError("Fees is required");
            return false;
        }
        if (TextUtils.isEmpty(Arrivaltime1.getText().toString())) {
            Arrivaltime1.setError("Arrivaltime is required");
            return false;
        }
        if (TextUtils.isEmpty(Arrivaltime2.getText().toString())) {
            Arrivaltime2.setError("Arrivaltime is required");
            return false;
        }
        // Add validation for stopping points if needed
        return true;
    }

    private void submitBusDetails() {
        // Retrieve bus details from input fields

        String busNo = Busno.getSelectedItem ().toString();
        String startingPoint = editTextStartingPoint.getText().toString();
        String endingPoint = editTextEndingPoint.getText().toString();
        String departureTime = editTextDepartureTime.getText().toString();
        String arrivalTime = editTextArrivalTime.getText().toString();
        Toast.makeText (this, "validated", Toast.LENGTH_SHORT).show ();
        String Stopping1Latitude=editTextStop1Latitude.getText ().toString ();
        String Stopping1longitude=editTextStop1Longitude.getText ().toString ();
        String Stopping2Latitude=editTextStop2Latitude.getText ().toString ();
        String Stopping2Longitude=editTextStop2Longitude.getText ().toString ();
        String StoppingName1=stopping1name.getText ().toString ();
        String StoppingName2=stopping2name.getText ().toString ();
        int fees1=Integer.parseInt(Fees1.getText().toString());
        int fees2=Integer.parseInt(Fees2.getText().toString());
        String arrival1=Arrivaltime1.getText ().toString ();
        String arrival2=Arrivaltime2.getText ().toString ();
        int TotalSeats=Integer.parseInt(totalSeats.getText ().toString ());


        DatabaseReference busdetails = databaseReference.child(busNo);

        // Save bus details to the database
        busdetails.child("ArrivalTime").setValue(arrivalTime);
        busdetails.child("DepartureTime").setValue(departureTime);
        busdetails.child("StartingPoint").setValue(startingPoint);
        busdetails.child("Ending Point").setValue(endingPoint);
        busdetails.child("totalSeats").setValue(TotalSeats);



        DatabaseReference stopping = databaseReference.child(busNo).child("Stopping Points").child (StoppingName1);
        stopping.child("name").setValue (StoppingName1);
        stopping.child ("latitude").setValue (Stopping1Latitude);
        stopping.child ("longitude").setValue (Stopping1longitude);
        stopping.child ("fees").setValue (fees1);
        stopping.child ("Arrival Time").setValue (arrival1);


        DatabaseReference stopping2 = databaseReference.child(busNo).child("Stopping Points").child (StoppingName2);
        stopping2.child("name").setValue (StoppingName2);
        stopping2.child ("latitude").setValue (Stopping2Latitude);
        stopping2.child ("longitude").setValue (Stopping2Longitude);
        stopping2.child ("fees").setValue (fees2);
        stopping2.child ("Arrival Time").setValue (arrival2);

        databaseReference.child(busNo).child("Seat Count").setValue (0);

        DatabaseReference routemap=databaseReference.child (busNo).child ("Routemap");
        routemap.child ("StoppingName 1").setValue (StoppingName1);
        routemap.child ("StoppingName 2").setValue (StoppingName2);

        // Save stopping points
        for (int i = 0; i < stopCount; i++) {
            EditText latField = latitudeFields.get(i);
            EditText lngField = longitudeFields.get(i);
            EditText nameField = stoppingNameFields.get(i);
            EditText FeesField=feesFields.get(i);
            EditText arrivalTimefields=Arrivaltimefields.get(i);

            if (latField != null && lngField != null && nameField != null) {
                String latitude = latField.getText().toString();
                String longitude = lngField.getText().toString();
                String stoppingName = nameField.getText().toString();
                int fees=Integer.parseInt (FeesField.getText ().toString ());
                String Arrival=arrivalTimefields.getText ().toString ();



                DatabaseReference stopRef = databaseReference.child(busNo).child("Stopping Points").child (stoppingName);
                // Set latitude, longitude, and name values for the stopping point
                stopRef.child("name").setValue(stoppingName);
                stopRef.child("latitude").setValue(latitude);
                stopRef.child("longitude").setValue(longitude);
                stopRef.child("fees").setValue(fees);
                stopRef.child("Arrival Time").setValue(Arrival);

                routemap.child("stoppingName "+(i+1)).setValue(stoppingName);
            }
        }
        Toast.makeText(this, "Bus details submitted successfully", Toast.LENGTH_SHORT).show();
        clearFields();
    }

    private void clearFields() {
        Busno.setSelection (0);
        editTextStartingPoint.setText("");
        editTextEndingPoint.setText("");
        editTextDepartureTime.setText("");
        editTextArrivalTime.setText("");
        editTextStop1Latitude.setText("");
        editTextStop1Longitude.setText("");
        editTextStop2Latitude.setText("");
        editTextStop2Longitude.setText("");
        stoppingPointsContainer.removeAllViews();
        latitudeFields.clear();
        longitudeFields.clear();
        stoppingNameFields.clear();
        stopCount = 2;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            double latitude = data.getDoubleExtra("latitude", 0);
            double longitude = data.getDoubleExtra("longitude", 0);

            if (requestCode == PLACE_PICKER_REQUEST_1) {
                editTextStop1Latitude.setText(String.valueOf(latitude));
                editTextStop1Longitude.setText(String.valueOf(longitude));
            } else if (requestCode == PLACE_PICKER_REQUEST_2) {
                editTextStop2Latitude.setText(String.valueOf(latitude));
                editTextStop2Longitude.setText(String.valueOf(longitude));
            } else if (requestCode >= MAP_PICKER_REQUEST) {
                int stopIndex = requestCode - MAP_PICKER_REQUEST;
                EditText latitudeField = latitudeFields.get(stopIndex);
                EditText longitudeField = longitudeFields.get(stopIndex);
                if (latitudeField != null && longitudeField != null) {
                    latitudeField.setText(String.valueOf(latitude));
                    longitudeField.setText(String.valueOf(longitude));
                }
            }
        }
    }
}
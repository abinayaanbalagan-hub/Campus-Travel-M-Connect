package com.example.bustrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


import androidx.appcompat.app.AppCompatActivity;

public class ReportActivity extends AppCompatActivity {
    Button DriverReport, BusReport, StudentReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        DriverReport = findViewById(R.id.driver_report);
        BusReport = findViewById(R.id.bus_report);
        StudentReport = findViewById(R.id.student_report);

        DriverReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add the intent for Driver Report here
                Intent intent = new Intent(ReportActivity.this, DriverReportActivity.class);
                startActivity(intent);
            }
        });

        BusReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add the intent for Bus Report here
                Intent intent = new Intent(ReportActivity.this, BusReportActivity.class);
                startActivity(intent);
            }
        });

        StudentReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add the intent for Student Report here
                Intent intent = new Intent(ReportActivity.this, StudentReportActivity.class);
                startActivity(intent);
            }
        });
    }
}

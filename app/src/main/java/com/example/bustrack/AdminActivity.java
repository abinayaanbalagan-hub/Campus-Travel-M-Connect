package com.example.bustrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AdminActivity extends AppCompatActivity {
    Button BusDetails, DriverDetails, StudentDetails, Payment, Report;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin);
        BusDetails = findViewById(R.id.busdetails);
        StudentDetails = findViewById(R.id.studentdetails);
        DriverDetails = findViewById(R.id.driverdetails);
        Payment = findViewById(R.id.payment);
        Report = findViewById(R.id.report);

        BusDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, Bus_Details.class);
                startActivity(intent);
            }
        });

        StudentDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, Student_Main.class);
                startActivity(intent);
            }
        });

        DriverDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, AddDriverActivity.class);
                startActivity(intent);
            }
        });

        Payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, AddPaymentActivity.class);
                startActivity(intent);
            }
        });

        Report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, ReportActivity.class);
                startActivity(intent);
            }
        });
    }
}

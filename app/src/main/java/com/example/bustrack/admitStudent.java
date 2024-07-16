package com.example.bustrack;


import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class admitStudent extends AppCompatActivity {
    private EditText Name, Rollno, Mailid,mobileno;
    private TextView Password;
    private FirebaseDatabase Database;
    private DatabaseReference reference;
    private Button add_student;

    private Spinner Department, Degree, BatchStart, BatchEnd;
    private String department_selected, degree_selected, name, rollno, mailid,mobileNo;
    private int BatchStart_selected, BatchEnd_selected;
    private Boolean firstLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admit_student);

        Department = findViewById(R.id.department);
        Degree = findViewById(R.id.Degree);
        BatchStart = findViewById(R.id.Batch_Start);
        BatchEnd = findViewById(R.id.Batch_End);
        add_student = findViewById(R.id.Add_student);
        Name = findViewById(R.id.Name);
        Rollno = findViewById(R.id.Rollno);
        Mailid = findViewById(R.id.Mailid);
        mobileno=findViewById (R.id.MobileNo);

        Database = FirebaseDatabase.getInstance();

        List<String> degree = new ArrayList<>();
        degree.add("MCA");
        degree.add("MBA");
        degree.add("BE");
        degree.add("BTech");
        degree.add("ME");
        degree.add("MTech");

        List<String> department = new ArrayList<>();
        department.add("MCA");
        department.add("MBA");
        department.add("Civil");
        department.add("Mechanical");
        department.add("EEE");
        department.add("ECE");
        department.add("IT");
        department.add("CSE");
        department.add("ICE");

        ArrayAdapter<String> select_degree = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, degree);
        select_degree.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Degree.setAdapter(select_degree);

        ArrayAdapter<String> select_department = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, department);
        select_department.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Department.setAdapter(select_department);

        populateYearSpinners();


        add_student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addStudentToDatabase();
            }
        });
    }

    private void addStudentToDatabase() {
        // Capture the values from the UI
        name = Name.getText().toString();
        rollno = Rollno.getText().toString();
        mailid = Mailid.getText().toString();
        mobileNo = mobileno.getText().toString();
        department_selected = Department.getSelectedItem().toString();
        degree_selected = Degree.getSelectedItem().toString();
        BatchStart_selected = Integer.parseInt(BatchStart.getSelectedItem().toString());
        BatchEnd_selected = Integer.parseInt(BatchEnd.getSelectedItem().toString());
        firstLogin = true;

        reference = Database.getReference("Add_Student");

        if (validateFields()) {
            checkDatabaseAndAddStudent();
        }
    }

    private boolean validateFields() {
        boolean isValid = true;
        if (name.isEmpty() || rollno.isEmpty() || mailid.isEmpty() || mobileNo.isEmpty() || department_selected.isEmpty() || degree_selected.isEmpty()) {
            Toast.makeText(admitStudent.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(mailid).matches()) {
            Toast.makeText(admitStudent.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        if (!isValidDegree(degree_selected, department_selected)) {
            isValid = false;
            Toast.makeText(this, "Please enter a valid degree and department", Toast.LENGTH_SHORT).show();
        }
        if (!isValidDepartment(degree_selected, department_selected)) {
            isValid = false;
            Toast.makeText(this, "Please enter a valid degree and department", Toast.LENGTH_SHORT).show();
        }
        if (!isValidBatch(degree_selected, BatchStart_selected, BatchEnd_selected)) {
            isValid = false;
            Toast.makeText(this, "Invalid batch duration for selected degree", Toast.LENGTH_SHORT).show();
        }
        return isValid;
    }

    private boolean isValidDegree(String degree, String department) {
        switch (degree) {
            case "MCA":
                return department.equals("MCA");
            case "MBA":
                return department.equals("MBA");
            case "BTech":
            case "MTech":
                return department.equals("IT");
            default:
                return true;
        }
    }

    private boolean isValidDepartment(String degree, String department) {
        switch (department) {
            case "MCA":
                return degree.equals("MCA");
            case "MBA":
                return degree.equals("MBA");
            case "Civil":
            case "Mechanical":
            case "CSE":
            case "ECE":
            case "EEE":
            case "ICE":
                return degree.equals("BE") || degree.equals("ME");
            case "IT":
                return degree.equals("BTech") || degree.equals("MTech");
            default:
                return false;
        }
    }

    private boolean isValidBatch(String degree, int batchStart, int batchEnd) {
        if (batchStart == batchEnd) {
            return false;
        }

        int batchDifference = batchEnd - batchStart;

        switch (degree) {
            case "MCA":
            case "MBA":
            case "ME":
            case "MTech":
                return batchDifference == 2;
            case "BE":
            case "BTech":
                return batchDifference == 4;
            default:
                return true;
        }
    }

    private void populateYearSpinners() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        List<Integer> years = new ArrayList<>();
        for (int i = currentYear - 5; i <= currentYear + 5; i++) {
            years.add(i);
        }

        ArrayAdapter<Integer> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        BatchStart.setAdapter(yearAdapter);
        BatchEnd.setAdapter(yearAdapter);
    }

    private void checkDatabaseAndAddStudent() {

        reference.child(department_selected).child(rollno).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(admitStudent.this, "Roll No is already found", Toast.LENGTH_SHORT).show();
                } else {
                    storeIntoDatabase();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(admitStudent.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void storeIntoDatabase() {
        Helperclass helperclass = new Helperclass(name, rollno, mailid, mobileNo, department_selected, degree_selected, BatchStart_selected, BatchEnd_selected, firstLogin);
        reference.child(department_selected).child(rollno).setValue(helperclass);
        Toast.makeText(admitStudent.this, "Successfully added", Toast.LENGTH_SHORT).show();

        Name.setText("");
        Rollno.setText("");
        Mailid.setText("");
        Password.setText("");
    }

   }

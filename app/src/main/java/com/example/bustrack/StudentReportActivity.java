package com.example.bustrack;

import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.pdf.PrintedPdfDocument;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class StudentReportActivity extends AppCompatActivity {

    private EditText etDepartment;
    private Button btnSubmit, btnPrint;
    private LinearLayout llStudentDetails;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_report);

        etDepartment = findViewById(R.id.etDepartment);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnPrint = findViewById(R.id.btnPrint);
        llStudentDetails = findViewById(R.id.llStudentDetails);

        databaseReference = FirebaseDatabase.getInstance().getReference("Add_Student");

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String department = etDepartment.getText().toString().trim();
                if (!department.isEmpty()) {
                    fetchStudentDetails(department);
                } else {
                    Toast.makeText(StudentReportActivity.this, "Please enter a department", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPdf();
            }
        });
    }

    private void fetchStudentDetails(String department) {
        Log.d("StudentReportActivity", "Fetching details for department: " + department);

        databaseReference.child(department).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.d("StudentReportActivity", "Department found: " + department);
                    llStudentDetails.removeAllViews();

                    for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
                        String rollNumber = studentSnapshot.getKey();
                        String busNo = studentSnapshot.child("Bus No").getValue(String.class);
                        String batchEnd = studentSnapshot.child("batchEndSelected").getValue(String.class);
                        String batchStart = studentSnapshot.child("batchStartSelected").getValue(String.class);
                        String degree = studentSnapshot.child("degreeSelected").getValue(String.class);

                        addDetailToLayout("Roll Number: " + rollNumber);
                        addDetailToLayout("Bus No: " + busNo);
                        addDetailToLayout("Batch Start: " + batchStart);
                        addDetailToLayout("Batch End: " + batchEnd);
                        addDetailToLayout("Degree: " + degree);

                        llStudentDetails.setVisibility(View.VISIBLE);
                        btnPrint.setVisibility(View.VISIBLE);
                    }
                } else {
                    Log.d("StudentReportActivity", "No students found in department: " + department);
                    Toast.makeText(StudentReportActivity.this, "No students found in this department", Toast.LENGTH_SHORT).show();
                    llStudentDetails.setVisibility(View.GONE);
                    btnPrint.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("StudentReportActivity", "Database error: " + databaseError.getMessage());
                Toast.makeText(StudentReportActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addDetailToLayout(String detail) {
        TextView textView = new TextView(this);
        textView.setText(detail);
        textView.setPadding(0, 8, 0, 8);
        llStudentDetails.addView(textView);
    }

    private void createPdf() {
        PdfDocument document = new PrintedPdfDocument(this, new PrintAttributes.Builder().build());
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(300, 600, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        llStudentDetails.draw(page.getCanvas());

        document.finishPage(page);

        File pdfFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "student_report.pdf");

        try {
            document.writeTo(new FileOutputStream(pdfFile));
            Toast.makeText(this, "PDF saved to " + pdfFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Log.e("StudentReportActivity", "Error writing PDF", e);
            Toast.makeText(this, "Failed to save PDF", Toast.LENGTH_SHORT).show();
        }

        document.close();
    }
}

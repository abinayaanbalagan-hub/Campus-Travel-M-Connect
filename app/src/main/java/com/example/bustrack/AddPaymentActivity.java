package com.example.bustrack;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddPaymentActivity extends AppCompatActivity {

    private EditText Roll_NoEditText, BatchEditText, DepartmentEditText, feesEditText, BillNoEditText;
    private Button submitButton;
    private FirebaseDatabase database;
    private DatabaseReference paymentReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_payment);
        Roll_NoEditText = findViewById(R.id.RollNoEditText);
        BatchEditText = findViewById(R.id.BatchEditText);
        DepartmentEditText = findViewById(R.id.DepartmentEditText);
        feesEditText = findViewById(R.id.feesEditText);
        BillNoEditText = findViewById(R.id.BillNoEditText);
        submitButton = findViewById(R.id.submitButton);

        database = FirebaseDatabase.getInstance();
        paymentReference = database.getReference().child("Payment");

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPayment();
            }
        });
    }

    private void addPayment() {
        String roll_No = Roll_NoEditText.getText().toString().trim();
        String batch = BatchEditText.getText().toString().trim();
        String department = DepartmentEditText.getText().toString().trim();
        String feesAmount = feesEditText.getText().toString().trim();
        String billNo = BillNoEditText.getText().toString().trim();

        if (TextUtils.isEmpty(roll_No) || TextUtils.isEmpty(batch) || TextUtils.isEmpty(department) || TextUtils.isEmpty(feesAmount) || TextUtils.isEmpty(billNo)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference payment = paymentReference.child(billNo);
        payment.child("Roll No").setValue(roll_No);
        payment.child("Batch").setValue(batch);
        payment.child("Department").setValue(department);
        payment.child("BillNo").setValue(billNo);
        payment.child("Fees").setValue(Long.parseLong(feesAmount));

        Toast.makeText(AddPaymentActivity.this, "Payment details added successfully", Toast.LENGTH_SHORT).show();
        clearFields();
    }

    private void clearFields() {
        Roll_NoEditText.setText("");
        BatchEditText.setText("");
        DepartmentEditText.setText("");
        feesEditText.setText("");
        BillNoEditText.setText("");
    }
}

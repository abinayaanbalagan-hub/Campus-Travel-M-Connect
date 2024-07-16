package com.example.bustrack;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import java.util.Properties;
import java.util.Random;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class RegisterStudentActivity extends AppCompatActivity {

    private EditText editTextName, Rollno, editTextUsername, editTextPassword;
    private Button verifyotp;
    private Spinner Department, Degree, BatchStart, BatchEnd;
    private LinearLayout OTPcontainer;
    private EditText otpInput;
    private Button submitOtp;

    private String name, rollno, username, password, department_selected, degree_selected;
    private int batchstart, batchend;
    private String verificationCode;
    private String toEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_student);

        // Initialize views
        editTextName = findViewById(R.id.Name);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        verifyotp = findViewById(R.id.verifyotp);
        Rollno = findViewById(R.id.Rollno);
        Department = findViewById(R.id.department);
        Degree = findViewById(R.id.Degree);
        BatchStart = findViewById(R.id.Batch_Start);
        BatchEnd = findViewById(R.id.Batch_End);
        OTPcontainer = findViewById(R.id.Otpcontainer);

        // Set up spinners
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

        // Button click listener for verifying OTP
        verifyotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFields()) {
                    // Generate OTP and send email
                    sendOTPByEmail();
                }
            }
        });
    }

    // Method to validate input fields
    private boolean validateFields() {
        name = editTextName.getText().toString().trim();
        rollno = Rollno.getText().toString();
        username = editTextUsername.getText().toString().trim();
        password = editTextPassword.getText().toString().trim();
        department_selected = Department.getSelectedItem().toString();
        degree_selected = Degree.getSelectedItem().toString();
        batchstart = Integer.parseInt(BatchStart.getSelectedItem().toString());
        batchend = Integer.parseInt(BatchEnd.getSelectedItem().toString());

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(rollno) || TextUtils.isEmpty(username)
                || TextUtils.isEmpty(password) || TextUtils.isEmpty(department_selected)
                || TextUtils.isEmpty(degree_selected)) {
            Toast.makeText(RegisterStudentActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!isValidDegree(degree_selected, department_selected)) {
            Toast.makeText(this, "Please enter a valid degree and department", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!isValidDepartment(degree_selected, department_selected)) {
            Toast.makeText(this, "Please enter a valid degree and department", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!isValidBatch(degree_selected, batchstart, batchend)) {
            Toast.makeText(this, "Invalid batch duration for selected degree", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    // Method to check valid degree
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

    // Method to check valid department
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

    // Method to check valid batch
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

    // Method to populate year spinners
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

    // Method to send OTP via email
    private void sendOTPByEmail() {
        DatabaseReference mailReference = FirebaseDatabase.getInstance().getReference().child("Add_Student").child(department_selected).child(rollno);
        mailReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    toEmail = snapshot.child("mailid").getValue(String.class);
                    if (toEmail != null && !toEmail.isEmpty()) {
                        // Generate OTP
                        verificationCode = generateOTP();
                        // Send email with OTP
                        new SendEmailTask().execute();
                    } else {
                        Toast.makeText(RegisterStudentActivity.this, "Email address not found for this student", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RegisterStudentActivity.this, "Failed to fetch email address", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to generate OTP
    private String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // Generate a 6-digit OTP
        return String.valueOf(otp);
    }

    // AsyncTask to send email with OTP
    private class SendEmailTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                // JavaMail properties
                Properties props = new Properties();
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.host", "smtp.gmail.com");
                props.put("mail.smtp.port", "587");

                // Sender's email credentials
                final String senderEmail = "abinaya1622@gmail.com"; // Replace with your email address
                final String appPassword = "bzxo tkwv hzfs mmgi"; // Replace with your generated app password

                // Session to authenticate the sender
                Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(senderEmail, appPassword);
                    }
                });

                // Create a MimeMessage object
                Message message = new MimeMessage(session);

                // Set the sender's email address
                message.setFrom(new InternetAddress(senderEmail));

                // Set the recipient's email address
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));

                // Set the email subject
                message.setSubject("OTP Verification");

                // Set the email content
                String emailContent = "Your OTP for verification is: " + verificationCode;
                message.setText(emailContent);

                // Send the email
                Transport.send(message);

                // Return true if email sending was successful
                return true;

            } catch (MessagingException e) {
                e.printStackTrace();
                // Return false if there was an error sending the email
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            if (success) {
                Toast.makeText(RegisterStudentActivity.this, "OTP sent successfully", Toast.LENGTH_SHORT).show();
                // Display OTP verification UI
                createOtpView();
            } else {
                Toast.makeText(RegisterStudentActivity.this, "Failed to send OTP", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Method to create OTP verification UI
    private void createOtpView() {
        // Clear existing views if any
        OTPcontainer.removeAllViews();

        // Create UI elements for OTP verification
        TextView verifyOtp = new TextView(this);
        verifyOtp.setText("Verify OTP");
        verifyOtp.setTextSize(16);

        EditText otpInput = new EditText(this);
        otpInput.setHint("Enter OTP");
        otpInput.setTextSize(16);

        Button submitOtp = new Button(this);
        submitOtp.setText("Submit OTP");
        submitOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp = otpInput.getText().toString();
                if (!TextUtils.isEmpty(otp)) {
                    // Verify OTP
                    verifyOtp(otp);
                } else {
                    Toast.makeText(RegisterStudentActivity.this, "Enter OTP", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Add views to OTPcontainer LinearLayout
        OTPcontainer.addView(verifyOtp);
        OTPcontainer.addView(otpInput);
        OTPcontainer.addView(submitOtp);
    }

    // Method to verify OTP
    private void verifyOtp(String otp) {
        if (otp.equals(verificationCode)) {
            Toast.makeText(RegisterStudentActivity.this, "OTP verified successfully", Toast.LENGTH_SHORT).show();

            DatabaseReference studentReference=FirebaseDatabase.getInstance ().getReference ().child ("Student").child(username);
            studentReference.child ("Username").setValue (username);
            studentReference.child ("password").setValue (password);
            studentReference.child ("Department").setValue (department_selected);
            studentReference.child ("Roll No").setValue (rollno);
            Intent intent=new Intent (RegisterStudentActivity.this,LoginActivity.class);
            startActivity (intent);

        } else {
            Toast.makeText(RegisterStudentActivity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
        }
    }
}
package com.example.bustrack;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class AddDriverActivity extends AppCompatActivity {

    private EditText driverName, licenseNumber, age, contactNumber, email;
    private TextView password;
    private Button generatePasswordButton, addDriverButton;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_driver);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Driver");

        // Initialize UI elements
        driverName = findViewById(R.id.driverName);
        licenseNumber = findViewById(R.id.licenseNumber);
        age = findViewById(R.id.DoB);
        contactNumber = findViewById(R.id.contactNumber);
        email = findViewById(R.id.email);
        password=findViewById (R.id.password);
        generatePasswordButton = findViewById(R.id.generatePasswordButton);
        addDriverButton = findViewById(R.id.addDriverButton);

        // Button click listeners
        generatePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String generatedPassword = generatePassword();
                password.setText(generatedPassword);
            }
        });

        addDriverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDriver();
            }
        });
    }

    // Method to generate a random password
    private String generatePassword() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%&*";
        StringBuilder password = new StringBuilder();
        int length = 8;
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * characters.length());
            password.append(characters.charAt(index));
        }
        return password.toString();
    }

    // Method to add driver details to Firebase Realtime Database
    private void addDriver() {
        String name = driverName.getText().toString().trim();
        String license = licenseNumber.getText().toString().trim();
        String driverAge = age.getText().toString().trim();
        String contact = contactNumber.getText().toString().trim();
        String emailAddress = email.getText().toString().trim();
        String driverPassword = password.getText().toString().trim();

        // Validate input fields
        if (name.isEmpty() || license.isEmpty() || driverAge.isEmpty() ||
                contact.isEmpty() || emailAddress.isEmpty() || driverPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a new Driver object
        AddDriver driver = new AddDriver(name, license, driverAge, contact, emailAddress,driverPassword);

        // Push the driver details to Firebase Database

        if (contact != null) {
            databaseReference.child(contact).setValue(driver);
            databaseReference.child(contact).child ("firstLogin").setValue (true);
            Toast.makeText(this, "Driver added successfully", Toast.LENGTH_SHORT).show();

            // Send email with driver's account details
            sendEmail(emailAddress, name, driverPassword);
        } else {
            Toast.makeText(this, "Failed to add driver", Toast.LENGTH_SHORT).show();
        }

        // Clear input fields
        clearFields();
    }

    // Method to send email with driver's account details
    private void sendEmail(String emailAddress, String name, String driverPassword) {
        SendEmailTask task = new SendEmailTask();
        task.execute(emailAddress, name, driverPassword);
    }

    // AsyncTask to send email
    private class SendEmailTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String emailAddress = params[0];
            String name = params[1];
            String driverPassword = params[2];

            // Sender's email credentials (Update with your email and app password)
            final String senderEmail = "abinaya1622@gmail.com"; // Replace with your email address
            final String appPassword = "bzxo tkwv hzfs mmgi"; // Replace with your generated app password

            // Mail server properties
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            // Session to authenticate the sender
            Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(senderEmail, appPassword);
                }
            });

            try {
                // Create a MimeMessage object
                Message message = new MimeMessage(session);

                // Set sender email address
                message.setFrom(new InternetAddress(senderEmail));

                // Set recipient email address
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailAddress));

                // Set email subject
                message.setSubject("Driver Account Details");

                // Set email content
                String emailContent = "Dear " + name + ",\n\n"
                        + "Your account has been successfully created. Below are your login credentials:\n\n"
                        + "Username: " + name + "\n"
                        + "Password: " + driverPassword + "\n\n"
                        + "Please keep this information confidential.\n\n"
                        + "Regards,\n"
                        + "Your Company";

                // Set the email body
                message.setText(emailContent);

                // Send the email
                Transport.send(message);
                return true;
            } catch (MessagingException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(AddDriverActivity.this, "Email sent successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(AddDriverActivity.this, "Failed to send email", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Method to clear input fields
    private void clearFields() {
        driverName.setText("");
        licenseNumber.setText("");
        age.setText("");
        contactNumber.setText("");
        email.setText("");
        password.setText("");
    }
}

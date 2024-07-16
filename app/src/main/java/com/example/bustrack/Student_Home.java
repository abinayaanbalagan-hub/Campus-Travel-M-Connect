package com.example.bustrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Student_Home extends AppCompatActivity {
    ImageButton location, epass, applypass;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_home);

        epass = findViewById(R.id.imageButton3);
        location = findViewById(R.id.imageButton2);
        applypass = findViewById(R.id.imageButton1);

        String rollno = getIntent().getStringExtra("Roll No");
        String department = getIntent().getStringExtra("Department");





        epass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Student_Home.this, epassactivity.class);
                intent.putExtra("Roll No",rollno);
                intent.putExtra("Department",department);
                startActivity(intent);
            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Student_Home.this, StudentActivity.class);
                intent.putExtra("Roll No",rollno);
                intent.putExtra("Department",department);
                startActivity(intent);
            }
        });

        applypass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Student_Home.this, Add_Student.class);
                intent.putExtra ("Roll No",rollno);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            // Handle logout click
            Intent logoutIntent = new Intent(this, LoginActivity.class);
            startActivity(logoutIntent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
package com.example.bustrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Student_Main extends AppCompatActivity {
    Button Add_Student,Update_Student,View_Student,Delete_Student;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.student_main);

        Add_Student=findViewById (R.id.Add_student);
        Update_Student=findViewById (R.id.Update_student);
        View_Student=findViewById (R.id.View_student);
        Delete_Student=findViewById (R.id.Delete_student);

        Add_Student.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent (Student_Main.this,admitStudent.class);
                startActivity (intent);
            }
        });

    }
}

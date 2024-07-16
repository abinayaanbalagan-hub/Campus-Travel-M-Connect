package com.example.bustrack;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Add_Student extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText Name, Roll_No, MobileNo, fees,BillNo;
    private Spinner Busno, course, batchstart, batchend, semester, Boarding_Place,Payment_Mode;
    private RadioGroup gender;
    private RadioButton male, female, others;
    private Button add_Student, uploadImageButton;
    private ImageView imagePreview;
    private FirebaseDatabase database;
    private DatabaseReference reference, busdetailsreference;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private Uri imageUri;
    String RollNo, name, boardingplace;
    Integer FeesAmount;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_student);

        // Initialize views
        initializeViews();

        // Initialize Firebase references
        initializeFirebase();

        // Initialize spinners
        initializeSpinners();

        // Set listeners
        setListeners();
    }

    private void initializeViews() {
        Name = findViewById(R.id.editTextName);
        Roll_No=findViewById (R.id.editTextRollNo);
        MobileNo = findViewById(R.id.MobileNo);
        Busno = findViewById(R.id.BusNo);
        course = findViewById(R.id.Course);
        batchstart = findViewById(R.id.BatchStart);
        batchend = findViewById(R.id.BatchEnd);
        semester = findViewById(R.id.Semester);
        fees = findViewById(R.id.Fees);
        gender = findViewById(R.id.Gender);
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        others = findViewById(R.id.others);
        add_Student = findViewById(R.id.Add_student);
        uploadImageButton = findViewById(R.id.uploadImageButton);
        imagePreview = findViewById(R.id.imagePreview);
        Boarding_Place = findViewById(R.id.Boarding);
        Payment_Mode=findViewById (R.id.Payment);
        linearLayout=findViewById (R.id.paymentcontainer);
        BillNo=findViewById (R.id.BillNo);

        RollNo=getIntent ().getStringExtra ("Roll No").trim();
        Roll_No.setText (RollNo);
        Roll_No.setEnabled (false);
    }

    private void initializeFirebase() {
        database = FirebaseDatabase.getInstance();
        reference = database.getReference().child("Student Details");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
    }

    private void initializeSpinners() {
        // Initialize course spinner
        List<String> Course = new ArrayList<>();
        Course.add("MCA");
        Course.add("MBA");
        Course.add("BE CSE");
        Course.add("BE MECHANICAL");
        Course.add("BE CIVIL");
        Course.add("BE ECE");
        Course.add("BE EEE");
        Course.add("BTECH IT");
        Course.add("MTECH IT");
        Course.add("ME CSE");
        Course.add("ME MECHANICAL");
        Course.add("ME CIVIL");
        Course.add("ME ECE");
        Course.add("ME EEE");


        ArrayAdapter<String> select_course = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Course);
        select_course.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        course.setAdapter(select_course);

        // Initialize semester spinner
        List<String> Semester = new ArrayList<>();
        Semester.add("Semester 1");
        Semester.add("Semester 2");
        Semester.add("Semester 3");
        Semester.add("Semester 4");
        Semester.add("Semester 5");
        Semester.add("Semester 6");
        Semester.add("Semester 7");
        Semester.add("Semester 8");

        ArrayAdapter<String> select_Semester = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Semester);
        select_Semester.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        semester.setAdapter(select_Semester);
        List<String> payment = new ArrayList<>();
        payment.add("Direct");
        payment.add("Card");
        payment.add("UPI");

        ArrayAdapter<String> select_payment = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, payment);
        select_payment.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Payment_Mode.setAdapter(select_payment);

        // Initialize bus spinner
        List<String> Bus = new ArrayList<>();
        for (int i = 1; i <= 15; i++) {
            Bus.add(String.format("%02d", i));
        }
        ArrayAdapter<String> select_Bus = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Bus);
        select_Bus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Busno.setAdapter(select_Bus);

        // Populate year spinners
        populateYearSpinners();
    }

    private void setListeners() {
        Busno.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedBusNo = Busno.getSelectedItem().toString();
                fetchBoardingPlaces(selectedBusNo);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        Boarding_Place.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String boarding = Boarding_Place.getSelectedItem().toString();
                fetchFees(Busno.getSelectedItem().toString(), boarding);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
        Payment_Mode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String payment = Payment_Mode.getSelectedItem().toString();
                if(payment.equals ("Direct")){
                    linearLayout.setVisibility (View.VISIBLE);
                    add_Student.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String billno=BillNo.getText ().toString ();
                            DatabaseReference paymentcheck=FirebaseDatabase.getInstance ().getReference ().child ("Payment").child (billno);
                            paymentcheck.addListenerForSingleValueEvent (new ValueEventListener () {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists ()){

                                        String rollno=snapshot.child ("Roll No").getValue (String.class);
                                        String Feesamount=snapshot.child ("Fees").getValue (Integer.class).toString ();


                                        if(rollno.equals (RollNo)&&Feesamount.equals (fees.getText ().toString ())){
                                            Toast.makeText (Add_Student.this, "Bill verified successfully", Toast.LENGTH_SHORT).show ();
                                            addstudentdata();
                                        }else{
                                            Toast.makeText (Add_Student.this, "Wrong Bill Number", Toast.LENGTH_SHORT).show ();
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {


                                }
                            });


                        }
                    });
                }else{
                    linearLayout.setVisibility (view.GONE);
                    add_Student.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            addstudentdata ();
                        }
                    });
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });



        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
    }

    private void populateYearSpinners() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        List<Integer> years = new ArrayList<>();
        for (int i = currentYear - 5; i <= currentYear + 5; i++) {
            years.add(i);
        }
        ArrayAdapter<Integer> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        batchstart.setAdapter(yearAdapter);
        batchend.setAdapter(yearAdapter);
    }

    private void fetchBoardingPlaces(String busNo) {
        busdetailsreference = database.getReference().child("busDetails").child(busNo).child("Stopping Points");
        busdetailsreference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> boardingPlaces = new ArrayList<>();
                for (DataSnapshot placeSnapshot : snapshot.getChildren()) {
                    String place = placeSnapshot.getKey();
                    boardingPlaces.add(place);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(Add_Student.this, android.R.layout.simple_spinner_item, boardingPlaces);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                Boarding_Place.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Add_Student.this, "Failed to fetch boarding places: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchFees(String selectedBusNo, String boarding) {
        busdetailsreference = database.getReference().child("busDetails").child(selectedBusNo).child("Stopping Points").child(boarding);
        busdetailsreference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    FeesAmount = snapshot.child("fees").getValue(Integer.class);
                    fees.setText(String.valueOf(FeesAmount));
                    fees.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Add_Student.this, "Failed to fetch fees: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addstudentdata() {

        String selectedBus = Busno.getSelectedItem().toString();

        // Basic validation
        if (RollNo.isEmpty() || selectedBus.isEmpty()) {
            Toast.makeText(this, "Roll number and bus number cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        reference.child(selectedBus).child(RollNo).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    Toast.makeText(Add_Student.this, "Student data is already found", Toast.LENGTH_SHORT).show();
                } else {
                    storeintodatabase();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Add_Student.this, "Failed to check data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void storeintodatabase() {
        boardingplace = Boarding_Place.getSelectedItem().toString().trim();
        name = Name.getText().toString().trim();
        String mobileNo = MobileNo.getText().toString().trim();
        String selectedBus = Busno.getSelectedItem().toString().trim();
        // Basic validation
        if (boardingplace.isEmpty() || name.isEmpty() || RollNo.isEmpty() || mobileNo.isEmpty()) {
            Toast.makeText(this, "Please fill all the required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Handle image upload if URI is not null
        if (imageUri != null) {
            uploadImageToFirebaseStorage(selectedBus, RollNo, new OnImageUploadListener() {
                @Override
                public void onImageUploadSuccess(String imageUrl) {
                    checkBusSeatAvailability(selectedBus,imageUrl);

                }

                @Override
                public void onImageUploadFailure(String errorMessage) {
                    Toast.makeText(Add_Student.this, "Image upload failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText (this, "Please select a image and then apply", Toast.LENGTH_SHORT).show ();
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imagePreview.setImageURI(imageUri);
        }
    }

    private void uploadImageToFirebaseStorage(String busNo, String rollNo, OnImageUploadListener listener) {
        if (imageUri != null) {
            StorageReference fileReference = storageReference.child("student_images/" + busNo + "/" + rollNo + ".jpg");
            fileReference.putFile(imageUri)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                fileReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.isSuccessful()) {
                                            Uri downloadUri = task.getResult();
                                            listener.onImageUploadSuccess(downloadUri.toString());
                                        } else {
                                            listener.onImageUploadFailure(task.getException().getMessage());
                                        }
                                    }
                                });
                            } else {
                                listener.onImageUploadFailure(task.getException().getMessage());
                            }
                        }
                    });
        } else {
            listener.onImageUploadFailure("No image selected");
        }
    }

    private void checkBusSeatAvailability(String busNo, String imageUrl) {
        DatabaseReference busReference = database.getReference().child("busDetails").child(busNo);

        busReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    Integer seatCount = snapshot.child("Seat Count").getValue(Integer.class);
                    Integer totalSeats = snapshot.child("totalSeats").getValue(Integer.class);

                    if (seatCount == null || totalSeats == null) {
                        Toast.makeText(Add_Student.this, "Data is missing", Toast.LENGTH_SHORT).show();
                        return;
                    }



                    if (totalSeats != seatCount) {
                        addtoDatabase(imageUrl);
                    } else {
                        Toast.makeText(Add_Student.this, "Sorry the Bus Seats are already filled", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Add_Student.this, "Sorry", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void addtoDatabase(String imageUrl) {


        boardingplace = Boarding_Place.getSelectedItem().toString().trim();
        name = Name.getText().toString().trim();
        String mobileNo = MobileNo.getText().toString().trim();

        Calendar currentDate = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String validFrom = formatter.format(currentDate.getTime());
        currentDate.add(Calendar.MONTH, 5);
        String validTo = formatter.format(currentDate.getTime());

        // Get selected gender
        int selectedGenderId = gender.getCheckedRadioButtonId();
        RadioButton selectedGenderRadioButton = findViewById(selectedGenderId);
        String genderText = selectedGenderRadioButton.getText().toString();

        // Get selected items from spinners
        String selectedCourse = course.getSelectedItem().toString().trim();
        String selectedSemester = semester.getSelectedItem().toString().trim();
        String selectedBatchStart = batchstart.getSelectedItem().toString().trim();
        String selectedBatchEnd = batchend.getSelectedItem().toString().trim();
        String selectedBus = Busno.getSelectedItem().toString().trim();

        DatabaseReference passreference=FirebaseDatabase.getInstance ().getReference ().child ("Student Details").child (selectedBus).child (RollNo);
        passreference.child ("Roll No").setValue (RollNo);
        passreference.child ("Name").setValue (name);
        passreference.child ("Bus No").setValue (selectedBus);
        passreference.child ("Course").setValue (selectedCourse);
        passreference.child ("Boarding Place").setValue (boardingplace);
        passreference.child ("Batch").setValue (selectedBatchStart+"-"+selectedBatchEnd);
        passreference.child ("Semester").setValue (selectedSemester);
        passreference.child ("Gender").setValue (genderText);
        passreference.child ("Fees").setValue (FeesAmount);
        passreference.child ("imageUrl").setValue (imageUrl);
        passreference.child ("Valid From").setValue (validFrom);
        passreference.child ("Valid Till").setValue (validTo);
        passreference.child ("Mobile No").setValue (mobileNo);
        passreference.child ("Fees").setValue ("Paid");

        DatabaseReference addbusreference=FirebaseDatabase.getInstance ().getReference ().child ("Add_Student").child (selectedCourse).child (RollNo);
        addbusreference.child ("Bus No").setValue (selectedBus);

        Toast.makeText (this, "Your pass has been successfully applied,you will get your epass in the home page", Toast.LENGTH_SHORT).show ();

    }

    // Listener interface for image upload result
    interface OnImageUploadListener {
        void onImageUploadSuccess(String imageUrl);
        void onImageUploadFailure(String errorMessage);
    }
}
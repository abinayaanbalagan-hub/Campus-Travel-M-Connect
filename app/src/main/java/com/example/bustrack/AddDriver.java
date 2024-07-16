package com.example.bustrack;

public class AddDriver {
    private String name;
    private String licenseNumber;
    private String Dob;
    private String contactNumber;
    private String email;
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public AddDriver() {
        // Default constructor required for calls to DataSnapshot.getValue(Driver.class)
    }

    public AddDriver(String name, String licenseNumber, String Dob, String contactNumber, String email,String driverPassword) {
        this.name = name;
        this.licenseNumber = licenseNumber;
        this.Dob = Dob;
        this.contactNumber = contactNumber;
        this.email = email;
        this.password = driverPassword;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }


    public String getDob() {
        return Dob;
    }

    public void setDob(String dob) {
        Dob = dob;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
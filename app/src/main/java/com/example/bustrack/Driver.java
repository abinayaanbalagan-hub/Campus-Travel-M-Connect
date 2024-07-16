package com.example.bustrack;

public class Driver {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBusNo() {
        return busNo;
    }

    public void setBusNo(String busNo) {
        this.busNo = busNo;
    }

    public String getLicenseNo() {
        return licenseNo;
    }

    public void setLicenseNo(String licenseNo) {
        this.licenseNo = licenseNo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Driver(String name, String busNo, String licenseNo, String username, String password) {
        this.name = name;
        this.busNo = busNo;
        this.licenseNo = licenseNo;
        this.username = username;
        this.password = password;
    }

    private String name, busNo, licenseNo, username, password;
}

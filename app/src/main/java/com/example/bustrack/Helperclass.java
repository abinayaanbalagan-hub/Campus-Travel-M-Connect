package com.example.bustrack;

public class Helperclass {
    private String name;
    private String rollno;
    private String mailid;
    private String mobileNo;
    private String departmentSelected;
    private String degreeSelected;
    private int batchStartSelected;
    private int batchEndSelected;
    private Boolean firstLogin;

    // Constructor
    public Helperclass(String name, String rollno, String mailid, String mobileNo,
                       String departmentSelected, String degreeSelected,
                       int batchStartSelected, int batchEndSelected, Boolean firstLogin) {
        this.name = name;
        this.rollno = rollno;
        this.mailid = mailid;
        this.mobileNo = mobileNo;
        this.departmentSelected = departmentSelected;
        this.degreeSelected = degreeSelected;
        this.batchStartSelected = batchStartSelected;
        this.batchEndSelected = batchEndSelected;
        this.firstLogin = firstLogin;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRollno() {
        return rollno;
    }

    public void setRollno(String rollno) {
        this.rollno = rollno;
    }

    public String getMailid() {
        return mailid;
    }

    public void setMailid(String mailid) {
        this.mailid = mailid;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getDepartmentSelected() {
        return departmentSelected;
    }

    public void setDepartmentSelected(String departmentSelected) {
        this.departmentSelected = departmentSelected;
    }

    public String getDegreeSelected() {
        return degreeSelected;
    }

    public void setDegreeSelected(String degreeSelected) {
        this.degreeSelected = degreeSelected;
    }

    public int getBatchStartSelected() {
        return batchStartSelected;
    }

    public void setBatchStartSelected(int batchStartSelected) {
        this.batchStartSelected = batchStartSelected;
    }

    public int getBatchEndSelected() {
        return batchEndSelected;
    }

    public void setBatchEndSelected(int batchEndSelected) {
        this.batchEndSelected = batchEndSelected;
    }

    public Boolean getFirstLogin() {
        return firstLogin;
    }

    public void setFirstLogin(Boolean firstLogin) {
        this.firstLogin = firstLogin;
    }
}

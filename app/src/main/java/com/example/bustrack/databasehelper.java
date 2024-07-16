package com.example.bustrack;

public class databasehelper {
    String selectedBus;
    String boardingplace;
    String validDate;
    String mobileNo;
    int fees;
    String validFrom;

    public String getValidTo() {
        return validTo;
    }

    public void setValidTo(String validTo) {
        this.validTo = validTo;
    }

    String validTo;

    public String getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(String validFrom) {
        this.validFrom = validFrom;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    String imageUrl;

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public int getFees() {
        return fees;
    }

    public void setFees(int fees) {
        this.fees = fees;
    }

    public String getSelectedBus() {
        return selectedBus;
    }

    public void setSelectedBus(String selectedBus) {
        this.selectedBus = selectedBus;
    }

    public String getBoardingplace() {
        return boardingplace;
    }

    public void setBoardingplace(String boardingplace) {
        this.boardingplace = boardingplace;
    }

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

    public String getPassno() {
        return passno;
    }

    public void setPassno(String passno) {
        this.passno = passno;
    }

    public String getSelectedCourse() {
        return selectedCourse;
    }

    public void setSelectedCourse(String selectedCourse) {
        this.selectedCourse = selectedCourse;
    }

    public String getSelectedSemester() {
        return selectedSemester;
    }

    public void setSelectedSemester(String selectedSemester) {
        this.selectedSemester = selectedSemester;
    }

    public String getBatch() {
        return Batch;
    }

    public void setBatch(String batch) {
        Batch = batch;
    }

    public String getGenderText() {
        return genderText;
    }

    public void setGenderText(String genderText) {
        this.genderText = genderText;
    }

    public String getValidDate() {
        return validDate;
    }

    public void setValidDate(String validDate) {
        this.validDate = validDate;
    }

    public databasehelper(String selectedBus, String boardingplace, String name, String rollno, String selectedCourse, String selectedSemester, String batch, String genderText, String mobileNo, int fees, String imageUrl, String validFrom, String validTo) {
        this.selectedBus = selectedBus;
        this.boardingplace = boardingplace;
        this.name = name;
        this.rollno = rollno;
        this.selectedCourse = selectedCourse;
        this.selectedSemester = selectedSemester;
        Batch = batch;
        this.genderText = genderText;
        this.mobileNo=mobileNo;
        this.fees=fees;
        this.imageUrl=imageUrl;
        this.validFrom=validFrom;
        this.validTo=validTo;

    }

    String name;
    String rollno;
    String passno;
    String selectedCourse;
    String selectedSemester;
    String Batch;
    String genderText;
}

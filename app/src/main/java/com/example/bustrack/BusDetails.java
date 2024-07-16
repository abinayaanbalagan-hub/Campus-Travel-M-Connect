package com.example.bustrack;
public class BusDetails {
    private String busNo;
    private String startingPoint;
    private String endingPoint;

    public String getTotalSeats() {
        return TotalSeats;
    }

    public void setTotalSeats(String totalSeats) {
        TotalSeats = totalSeats;
    }

    private String departureTime;
    private String arrivalTime;
    private  String TotalSeats;

    // Default constructor required for calls to DataSnapshot.getValue(BusDetails.class)
    public BusDetails() {
    }

    public BusDetails(String busNo, String startingPoint, String endingPoint, String departureTime, String arrivalTime, String TotalSeats) {
        this.busNo = busNo;
        this.startingPoint = startingPoint;
        this.endingPoint = endingPoint;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.TotalSeats=TotalSeats;
    }

    public String getBusNo() {
        return busNo;
    }

    public void setBusNo(String busNo) {
        this.busNo = busNo;
    }

    public String getStartingPoint() {
        return startingPoint;
    }

    public void setStartingPoint(String startingPoint) {
        this.startingPoint = startingPoint;
    }

    public String getEndingPoint() {
        return endingPoint;
    }

    public void setEndingPoint(String endingPoint) {
        this.endingPoint = endingPoint;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }
}

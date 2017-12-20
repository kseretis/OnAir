package com.example.onair;

public class Flights {

    private String departs_at, arrives_at, origin_airport, destination_airport, marketing_airline, operating_airline, flight_number, aircraft,
                    travel_class, booking_code, total_price, total_fare, tax,  depart_time, depart_day;
    private int  seats_remaining;
    private boolean refundable, change_penalties;

    public Flights(String departs_at, String arrives_at, String origin_airport, String destination_airport, String marketing_airline,
                   String operating_airline, String flight_number, String aircraft, String travel_class, String booking_code,
                   int seats_remaining, String total_price, String total_fare, String tax, boolean refundable, boolean change_penalties) {
        this.departs_at = departs_at;
        this.arrives_at = arrives_at;
        this.origin_airport = origin_airport;
        this.destination_airport = destination_airport;
        this.marketing_airline = marketing_airline;
        this.operating_airline = operating_airline;
        this.flight_number = flight_number;
        this.aircraft = aircraft;
        this.travel_class = travel_class;
        this.booking_code = booking_code;
        this.seats_remaining = seats_remaining;
        this.total_price = total_price;
        this.total_fare = total_fare;
        this.tax = tax;
        this.refundable = refundable;
        this.change_penalties = change_penalties;
    }

    //empty constructor
    public Flights (){
    }

    //setters
    public void setDeparts_at(String departs_at) {
        this.departs_at = departs_at;
    }

    public void setArrives_at(String arrives_at) {
        this.arrives_at = arrives_at;
    }

    public void setOrigin_airport(String origin_airport) {
        this.origin_airport = origin_airport;
    }

    public void setDestination_airport(String destination_airport) {
        this.destination_airport = destination_airport;
    }

    public void setMarketing_airline(String marketing_airline) {
        this.marketing_airline = marketing_airline;
    }

    public void setOperating_airline(String operating_airline) {
        this.operating_airline = operating_airline;
    }

    public void setFlight_number(String flight_number) {
        this.flight_number = flight_number;
    }

    public void setAircraft(String aircraft) {
        this.aircraft = aircraft;
    }

    public void setTravel_class(String travel_class) {
        this.travel_class = travel_class;
    }

    public void setBooking_code(String booking_code) {
        this.booking_code = booking_code;
    }

    public void setSeats_remaining(int seats_remaining) {
        this.seats_remaining = seats_remaining;
    }

    public void setTotal_price(String total_price) {
        this.total_price = total_price;
    }

    public void setTotal_fare(String total_fare) {
        this.total_fare = total_fare;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public void setRefundable(boolean refundable) {
        this.refundable = refundable;
    }

    public void setChange_penalties(boolean change_penalties) {
        this.change_penalties = change_penalties;
    }

    public void setDepart_day(String depart_day) {
        this.depart_day = depart_day;
    }

    public void setDepart_time(String depart_time) {
        this.depart_time = depart_time;
    }

    // getters
    public String getDeparts_at() {
        return departs_at;
    }

    public String getArrives_at() {
        return arrives_at;
    }

    public String getOrigin_airport() {
        return origin_airport;
    }

    public String getDestination_airport() {
        return destination_airport;
    }

    public String getMarketing_airline() {
        return marketing_airline;
    }

    public String getOperating_airline() {
        return operating_airline;
    }

    public String getFlight_number() {
        return flight_number;
    }

    public String getAircraft() {
        return aircraft;
    }

    public String getTravel_class() {
        return travel_class;
    }

    public String getBooking_code() {
        return booking_code;
    }

    public int getSeats_remaining() {
        return seats_remaining;
    }

    public String getTotal_price() {
        return total_price;
    }

    public String getTotal_fare() {
        return total_fare;
    }

    public String getTax() {
        return tax;
    }

    public boolean isRefundable() {
        return refundable;
    }

    public boolean isChange_penalties() {
        return change_penalties;
    }

    public String getDepart_time() {
        return depart_time;
    }

    public String getDepart_day() {
        return depart_day;
    }
}

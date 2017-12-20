package com.example.onair;

import java.util.ArrayList;

public class Itineraries {

    private ArrayList<Flights> flights_count;
    private String total_price, total_fare, tax;
    private boolean refundable, change_penalties;

    public Itineraries(){
    }

    public Itineraries(ArrayList<Flights> flights_count, String total_price, String total_fare, String tax,
                       boolean refundable, boolean change_penalties) {
        this.flights_count = flights_count;
        this.total_price = total_price;
        this.total_fare = total_fare;
        this.tax = tax;
        this.refundable = refundable;
        this.change_penalties = change_penalties;
    }

    public void setFlights_count(ArrayList<Flights> flights_count) {
        this.flights_count = flights_count;
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

    public ArrayList<Flights> getFlights_count() {
        return flights_count;
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
}

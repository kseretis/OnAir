package com.example.onair;

import java.util.ArrayList;

public class Outbound {
    private ArrayList<Flights> flights_for_a_itinerary;
    private String total_price, total_fare, tax;
    private boolean refundable, change_penalties;

    public Outbound (){
    }

    public ArrayList<Flights> getFlights_for_a_itinerary() {
        return flights_for_a_itinerary;
    }

    public void setFlights_for_a_itinerary(ArrayList<Flights> flights_for_a_itinerary) {
        this.flights_for_a_itinerary = flights_for_a_itinerary;
    }

    public String getTotal_price() {
        return total_price;
    }

    public void setTotal_price(String total_price) {
        this.total_price = total_price;
    }

    public String getTotal_fare() {
        return total_fare;
    }

    public void setTotal_fare(String total_fare) {
        this.total_fare = total_fare;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public boolean isRefundable() {
        return refundable;
    }

    public void setRefundable(boolean refundable) {
        this.refundable = refundable;
    }

    public boolean isChange_penalties() {
        return change_penalties;
    }

    public void setChange_penalties(boolean change_penalties) {
        this.change_penalties = change_penalties;
    }
}

package com.example.onair;

import java.io.Serializable;
import java.util.ArrayList;

public class Itinerary implements Serializable{
    private ArrayList<Flight> outbound_list = new ArrayList<>();
    private ArrayList<Flight> inbound_list = new ArrayList<>();
    private String total_price, total_fare, tax;
    private Boolean refundable, change_penalties;

    //empty constructor
    public Itinerary() {
    }

    //custom methods
    public void Outbound_list_adder(Flight fl){
        outbound_list.add(fl);
    }

    public void Inbound_list_adder(Flight fl){
        inbound_list.add(fl);
    }

    //getters
    public ArrayList<Flight> getOutbound_list() {
        return outbound_list;
    }

    public ArrayList<Flight> getInbound_list() {
        return inbound_list;
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

    public Boolean getRefundable() {
        return refundable;
    }

    public Boolean getChange_penalties() {
        return change_penalties;
    }

    //setters
    public void setTotal_price(String total_price) {
        this.total_price = total_price;
    }

    public void setTotal_fare(String total_fare) {
        this.total_fare = total_fare;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public void setRefundable(Boolean refundable) {
        this.refundable = refundable;
    }

    public void setChange_penalties(Boolean change_penalties) {
        this.change_penalties = change_penalties;
    }
}

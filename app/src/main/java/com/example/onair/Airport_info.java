package com.example.onair;

import java.io.Serializable;

public class Airport_info implements Serializable {
    private String code, airport_name, city, country;

    public Airport_info(String code) {
        this.code = code;
    }

    public Airport_info(){
    }

    public void airport_info(String code){
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAirport_name() {
        return airport_name;
    }

    public void setAirport_name(String airport_name) {
        this.airport_name = airport_name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
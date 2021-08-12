package com.operialcompany.Democliente.Models;

import java.io.Serializable;

/**
 * Created by qboxus on 1/4/2018.
 */

public class AddressListModel implements Serializable {

    String street;
    String apartment;
    String city;
    String state;
    String address_id;
    String delivery_fee;
    String instruction;

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getApartment() {
        return apartment;
    }

    public void setApartment(String apartment) {
        this.apartment = apartment;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }


    public String getAddress_id() {
        return address_id;
    }

    public void setAddress_id(String address_id) {
        this.address_id = address_id;
    }


    public String getDelivery_fee() {
        return delivery_fee;
    }

    public void setDelivery_fee(String delivery_fee) {
        this.delivery_fee = delivery_fee;
    }


    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }
}

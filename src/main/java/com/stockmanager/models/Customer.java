package com.stockmanager.models;

public class Customer {
    private String id;
    private String name;
    private CustomerType customerType;
    private String phone;
    private String address;

    public Customer() {
    }

    public Customer(String id, String name, CustomerType customerType, String phone, String address) {
        this.id = id;
        this.name = name;
        this.customerType = customerType;
        this.phone = phone;
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CustomerType getCustomerType() {
        return customerType;
    }

    public void setCustomerType(CustomerType customerType) {
        this.customerType = customerType;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}

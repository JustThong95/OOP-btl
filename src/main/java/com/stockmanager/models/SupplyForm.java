package com.stockmanager.models;

import java.sql.Timestamp;

public class SupplyForm {
    private int id;
    private String supplierName;
    private String productName;
    private int quantity;
    private double totalPrice;
    private Timestamp createdDate;

    public SupplyForm() {
    }

    public SupplyForm(int id, String supplierName, String productName, int quantity, double totalPrice, Timestamp createdDate) {
        this.id = id;
        this.supplierName = supplierName;
        this.productName = productName;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.createdDate = createdDate;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public Timestamp getCreatedDate() { return createdDate; }
    public void setCreatedDate(Timestamp createdDate) { this.createdDate = createdDate; }
}

package com.stockmanager.models;

public class Product {
    private int id;
    private String name;
    private int categoryId;
    private double price;
    private int stockQuantity;

    public Product() {}

    public Product(int id, String name, int categoryId, double price, int stockQuantity) {
        this.id = id;
        this.name = name;
        this.categoryId = categoryId;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }

    @Override
    public String toString() {
        return "Product[ID=" + id + ", Name=" + name + ", CategoryID=" + categoryId + 
               ", Price=$" + price + ", Stock=" + stockQuantity + "]";
    }
}

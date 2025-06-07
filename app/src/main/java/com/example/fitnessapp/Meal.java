package com.example.fitnessapp;

public class Meal {
    private final int id;
    private final String foodId;
    private final int quantity;
    private final String date;

    public Meal(int id, String foodId, int quantity, String date) {
        this.id = id;
        this.foodId = foodId;
        this.quantity = quantity;
        this.date = date;
    }

    public int getId() { return id; }
    public String getFoodId() { return foodId; }
    public int getQuantity() { return quantity; }
    public String getDate() { return date; }
}
package com.example.fitnessapp;

public class Meal {
    private final String label;
    private final double protein;
    private final int quantity;

    public Meal(String label, double protein, int quantity) {
        this.label = label;
        this.protein = protein;
        this.quantity = quantity;
    }

    public String getLabel() { return label; }
    public double getProtein() { return protein; }
    public int getQuantity() { return quantity; }
}

package com.example.fitnessapp;

public class Meal {
    private final int    id;
    private final String label;
    private final double protein;
    private final int    quantity;

    public Meal(int id, String label, double protein, int quantity) {
        this.id       = id;
        this.label    = label;
        this.protein  = protein;
        this.quantity = quantity;
    }

    public int    getId()       { return id; }
    public String getLabel()    { return label; }
    public double getProtein()  { return protein; }
    public int    getQuantity() { return quantity; }
}

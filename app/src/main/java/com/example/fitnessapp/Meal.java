package com.example.fitnessapp;
/**
 * Reprezentuje jednu položku jídla v deníku.
 * Uchovává ID, název, makroživiny na 100 g, počet porcí a datum.
 */
public class Meal {
    private int     id;
    private String  label;
    private double  proteinPer100g;
    private double  carbsPer100g;
    private double  fatPer100g;
    private int     portions;
    private final String date;

    // Konstruktor pro načtení z DB
    public Meal(int id, String label, double proteinPer100g, double carbsPer100g, double fatPer100g, int portions, String date) {
        this.id = id;
        this.label = label;
        this.proteinPer100g = proteinPer100g;
        this.carbsPer100g = carbsPer100g;
        this.fatPer100g = fatPer100g;
        this.portions = portions;
        this.date = date;  // Přiřazení data
    }

    // Gettery pro všechna pole, včetně data
    public String getDate() {
        return date;
    }

    // Gettery a settery
    public int getId() {
        return id;
    }
    public String getLabel() {
        return label;
    }
    public double getProteinPer100g() {
        return proteinPer100g;
    }
    public double getCarbsPer100g() {
        return carbsPer100g;
    }
    public double getFatPer100g() {
        return fatPer100g;
    }
    public int getPortions() {
        return portions;
    }

    public void setId(int id) {
        this.id = id;
    }
    public void setLabel(String label) {
        this.label = label;
    }
    public void setProteinPer100g(double proteinPer100g) {
        this.proteinPer100g = proteinPer100g;
    }
    public void setCarbsPer100g(double carbsPer100g) {
        this.carbsPer100g = carbsPer100g;
    }
    public void setFatPer100g(double fatPer100g) {
        this.fatPer100g = fatPer100g;
    }
    public void setPortions(int portions) {
        this.portions = portions;
    }
}
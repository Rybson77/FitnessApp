package com.example.fitnessapp.api.model;

import com.google.gson.annotations.SerializedName;
/**
 * Reprezentuje makrozhiviny potraviny na 100 g.
 */
public class Nutriments {
    /**
     * Proteiny v gramech na 100 g potraviny.
     */
    @SerializedName("proteins_100g")
    private double proteinsPer100g;

    /**
     * Sacharidy v gramech na 100 g potraviny.
     */
    @SerializedName("carbohydrates_100g")
    private double carbsPer100g;
    /**
     * Tuky v gramech na 100 g potraviny.
     */
    @SerializedName("fat_100g")
    private double fatPer100g;
    //Getry pro Addmeal vraci ziviny
    public double getProteinsPer100g() { return proteinsPer100g; }
    public double getCarbsPer100g()    { return carbsPer100g; }
    public double getFatPer100g()      { return fatPer100g; }
}


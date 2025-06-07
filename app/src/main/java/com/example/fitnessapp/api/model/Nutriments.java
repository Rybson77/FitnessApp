package com.example.fitnessapp.api.model;

import com.google.gson.annotations.SerializedName;

public class Nutriments {
    @SerializedName("proteins_100g")
    private double proteinsPer100g;

    @SerializedName("carbohydrates_100g")
    private double carbsPer100g;

    @SerializedName("fat_100g")
    private double fatPer100g;

    public double getProteinsPer100g() { return proteinsPer100g; }
    public double getCarbsPer100g()    { return carbsPer100g; }
    public double getFatPer100g()      { return fatPer100g; }
}


package com.example.fitnessapp.api.model;

import com.google.gson.annotations.SerializedName;

public class FoodItem {
    @SerializedName("foodId")
    private String foodId;

    @SerializedName("label")
    private String label;

    @SerializedName("nutrients")
    private Nutriments nutrients;

    public String getFoodId() { return foodId; }
    public String getLabel() { return label; }
    public Nutriments getNutrients() { return nutrients; }
}

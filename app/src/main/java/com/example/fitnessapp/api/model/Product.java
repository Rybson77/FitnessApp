package com.example.fitnessapp.api.model;

import com.google.gson.annotations.SerializedName;

public class Product {
    @SerializedName("product_name_cs")
    private String nameCs;            // český název, pokud je k dispozici

    @SerializedName("product_name")
    private String name;              // fallback anglický

    @SerializedName("nutriments")
    private Nutriments nutriments;

    public String getDisplayName() {
        return (nameCs != null && !nameCs.isEmpty()) ? nameCs : name;
    }
    public Nutriments getNutriments() { return nutriments; }
}

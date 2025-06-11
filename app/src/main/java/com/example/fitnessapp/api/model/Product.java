package com.example.fitnessapp.api.model;

import com.google.gson.annotations.SerializedName;
/**
 * Trida reprezentuje detail produktu z OpenFoodFacts API.
 * Obsahuje cesky nazev, fallback anglicky nazev a objekt Nutriments.
 */
public class Product {
    @SerializedName("product_name_cs")
    private String nameCs;            // český název, pokud je k dispozici

    @SerializedName("product_name")
    private String name;              // fallback anglický
    /**
     * Objekt s informacemi o makrozhivinach produktu.
     */
    @SerializedName("nutriments")
    private Nutriments nutriments;

    /**
     * Vraci zobrazeny nazev produktu.
     * Pokud je cesky nazev dostupny, pouzije jej, jinak vraci anglicky nazev.
     * @return zobrazeny nazev produktu
     */
    public String getDisplayName() {
        return (nameCs != null && !nameCs.isEmpty()) ? nameCs : name;
    }
    public Nutriments getNutriments() { return nutriments; }
}

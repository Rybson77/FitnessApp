package com.example.fitnessapp.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * Trida reprezentuje jednu polozku jidla z OpenFoodFacts API.
 * Vsechny serializovane hodnoty prichazeji z JSONu.
 */
public class FoodItem {
    /**
     * Unikatni ID potraviny v API
     */
    @SerializedName("foodId")
    private String foodId;

    /**
     * Nazev potraviny
     */
    @SerializedName("label")
    private String label;
    /**
     * Objekt obsahujici makrozhiviny (proteiny, sacharidy, tuky)
     */
    @SerializedName("nutrients")
    private Nutriments nutrients;

    //Zatim nepouzite pouze k debug
    public String getFoodId() { return foodId; }
    public String getLabel() { return label; }
    public Nutriments getNutrients() { return nutrients; }
}

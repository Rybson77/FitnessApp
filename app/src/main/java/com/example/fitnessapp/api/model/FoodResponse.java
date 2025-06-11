package com.example.fitnessapp.api.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;
/**
 * Trida reprezentuje odpoved API pro vyhledavani potravin.
 * Obsahuje list hintu, z nichz kazdy drzi podrobnosti o potravine.
 */
public class FoodResponse {
    /**
     * Seznam Hint objektu, ktery obsahuje vlastni FoodItem.
     */
    @SerializedName("hints")
    private List<Hint> hints;
    /**
     * Vraci seznam hintu z odpovedi.
     * @return list Hint objektu
     */
    public List<Hint> getHints() { return hints; }

    /**
     * Vnitrni trida reprezentujici jednotlive hinty v odpovedi.
     * Kazdy Hint obsazuje FoodItem s detaily o potravine.
     */
    public static class Hint {
        /**
         * Objekt FoodItem, ktery obsahuje id, nazev a makrozhiviny.
         */
        @SerializedName("food")
        private FoodItem food;
        public FoodItem getFood() { return food; }
    }
}

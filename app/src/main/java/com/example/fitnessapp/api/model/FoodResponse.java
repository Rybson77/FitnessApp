package com.example.fitnessapp.api.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class FoodResponse {
    @SerializedName("hints")
    private List<Hint> hints;

    public List<Hint> getHints() { return hints; }

    public static class Hint {
        @SerializedName("food")
        private FoodItem food;
        public FoodItem getFood() { return food; }
    }
}

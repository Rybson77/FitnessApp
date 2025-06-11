package com.example.fitnessapp.api.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Reprezentuje odpoved API pri vyhledavani produktu.
 * Obsahuje seznam objektu Product.
 */
public class SearchResponse {
    /**
     * Seznam nalezenych produktu.
     */
    @SerializedName("products")
    private List<Product> products;
    /**
     * Vraci seznam produktu z odpovedi.
     * @return list produktu
     */
    public List<Product> getProducts() { return products; }
}
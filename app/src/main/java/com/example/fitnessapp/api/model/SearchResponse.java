package com.example.fitnessapp.api.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class SearchResponse {
    @SerializedName("products")
    private List<Product> products;
    public List<Product> getProducts() { return products; }
}
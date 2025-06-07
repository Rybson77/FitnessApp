package com.example.fitnessapp.api;

import com.example.fitnessapp.api.model.SearchResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("cgi/search.pl")
    Call<SearchResponse> searchProducts(
            @Query("search_terms") String term,
            @Query("search_simple") int simple,    // vzdy 1
            @Query("json") int json               // vzdy 1
    );
}
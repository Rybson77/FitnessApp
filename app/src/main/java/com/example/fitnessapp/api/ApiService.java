package com.example.fitnessapp.api;

import com.example.fitnessapp.api.model.SearchResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
/**
 * Rozhrani pro volani OpenFoodFacts API pres Retrofit.
 * Definuje endpoint pro vyhledavani produktu podle nazvu.
 */
public interface ApiService {

    /**
     * Provede GET dotaz na endpoint /cgi/search.pl s parametry pro vyhledavani.
     *
     * @param term    hledany vyraz pro produkty
     * @param simple  zda pouzit jednoduche vyhledavani (vzdy 1)
     * @param json    zda vratit odpoved ve formatu JSON (vzdy 1)
     * @return        Retrofit Call, ktery vraci objekt SearchResponse
     */
    @GET("cgi/search.pl")
    Call<SearchResponse> searchProducts(
            @Query("search_terms") String term,
            @Query("search_simple") int simple,    // vzdy 1
            @Query("json") int json               // vzdy 1
    );
}
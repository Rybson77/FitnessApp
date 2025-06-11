package com.example.fitnessapp.api;



import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
/**
 * ApiClient je „singleton“ trida, ktera spravuje instanci Retrofit klienta
 * pro volani OpenFoodFacts API.
 *
 * Pouzitim jedine sdilene instance Retrofit setrime pamet a zbytecne
 * nevytvarime více HTTP klientu.
 */

public class ApiClient {
    /**
     * Základní URL adresy Open Food Facts API.
     * Všechny volani Retrofit budou tato URL pouzivat jako vachozi.
     */
    private static final String BASE_URL = "https://world.openfoodfacts.org/";
    /**
     * Vrací instanci Retrofit klienta.
     * Pokud ještě neexistuje, vytvoří ji pomocí BASE_URL a Gson konvertoru.
     *
     * @return Sdílená instance Retrofit
     */
    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    // Přidá konvertor JSON->Java objekty (Gson)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
package com.example.fitnessapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.fitnessapp.api.ApiClient;
import com.example.fitnessapp.api.ApiService;
import com.example.fitnessapp.api.model.SearchResponse;
import com.example.fitnessapp.api.model.Product;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddMealActivity extends AppCompatActivity {
    private Spinner spinnerFoods;
    private EditText etQuantityGrams;
    private DatabaseHelper db;
    private List<Product> products = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meal);

        spinnerFoods    = findViewById(R.id.spinnerFoods);
        etQuantityGrams = findViewById(R.id.etQuantity);
        Button btnSave  = findViewById(R.id.btnSaveMeal);
        db = new DatabaseHelper(this);

        // Uděláme hledání "vejce" – můžeš předělat na vlastní vyhledávací pole
        fetchProductsFromOff("vejce");

        btnSave.setOnClickListener(v -> {
            int pos = spinnerFoods.getSelectedItemPosition();
            Product chosen = products.get(pos);
            int grams = Integer.parseInt(etQuantityGrams.getText().toString());
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(new Date());

            // spočítáme proteiny na zadané gramy
            double prot100g = chosen.getNutriments().getProteinsPer100g();
            double totalProtein = prot100g * grams / 100.0;

            // Uložíme label a množství proteinů
            db.addMeal(chosen.getDisplayName(), totalProtein, 1, date);
            finish();
        });
    }

    private void fetchProductsFromOff(String query) {
        ApiService service = ApiClient.getClient().create(ApiService.class);
        service.searchProducts(query, 1, 1)
                .enqueue(new Callback<SearchResponse>() {
                    @Override
                    public void onResponse(Call<SearchResponse> call, Response<SearchResponse> resp) {
                        if (!resp.isSuccessful() || resp.body()==null) {
                            Toast.makeText(AddMealActivity.this,
                                    "Chyba načtení z OFF", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        products.clear();
                        products.addAll(resp.body().getProducts());
                        updateSpinner();
                    }
                    @Override
                    public void onFailure(Call<SearchResponse> call, Throwable t) {
                        Toast.makeText(AddMealActivity.this,
                                "OFF API selhalo: "+t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void updateSpinner() {
        List<String> labels = new ArrayList<>();
        for (Product p : products) {
            labels.add(p.getDisplayName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                labels
        );
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );
        spinnerFoods.setAdapter(adapter);
    }
}

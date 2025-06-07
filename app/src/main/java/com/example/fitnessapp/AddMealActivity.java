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
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddMealActivity extends AppCompatActivity {
    private EditText etSearchTerm, etQuantity;
    private Button   btnSearch, btnSaveMeal;
    private Spinner  spinnerFoods;
    private DatabaseHelper db;
    private List<Product> products = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meal);

        etSearchTerm = findViewById(R.id.etSearchTerm);
        btnSearch    = findViewById(R.id.btnSearch);
        spinnerFoods = findViewById(R.id.spinnerFoods);
        etQuantity   = findViewById(R.id.etQuantity);
        btnSaveMeal  = findViewById(R.id.btnSaveMeal);
        db           = new DatabaseHelper(this);

        btnSearch.setOnClickListener(v -> {
            String term = etSearchTerm.getText().toString().trim();
            if (term.isEmpty()) {
                Toast.makeText(this, "Zadej hledaný výraz", Toast.LENGTH_SHORT).show();
            } else {
                fetchProductsFromOff(term);
            }
        });

        btnSaveMeal.setOnClickListener(v -> {
            // 1) Získej index vybrané položky
            int pos = spinnerFoods.getSelectedItemPosition();
            if (pos < 0 || pos >= products.size()) {
                Toast.makeText(this, "Vyber položku ze seznamu", Toast.LENGTH_SHORT).show();
                return;
            }

            // 2) Podle indexu si vyber Product
            Product chosen = products.get(pos);

            // 3) Načti počet 100g porcí, které uživatel zadal
            String s = etQuantity.getText().toString().trim();
            if (s.isEmpty()) {
                Toast.makeText(this, "Zadej počet porcí", Toast.LENGTH_SHORT).show();
                return;
            }
            int portions = Integer.parseInt(s);

            // 4) Spočítej celkový protein
            double proteinPer100g = chosen.getNutriments().getProteinsPer100g();
            double totalProtein   = proteinPer100g * portions;

            // 5) Ulož do DB: ukládáme protein na 100g a počet porcí
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(new Date());
            db.addMeal(chosen.getDisplayName(), proteinPer100g, portions, date);

            // 6) Ukonči Activity a vrať se do MainActivity
            finish();
        });
    }

    private void fetchProductsFromOff(String query) {
        ApiService svc = ApiClient.getClient().create(ApiService.class);
        svc.searchProducts(query, 1, 1).enqueue(new Callback<SearchResponse>() {
            @Override public void onResponse(Call<SearchResponse> call, Response<SearchResponse> resp) {
                if (!resp.isSuccessful() || resp.body() == null) {
                    Toast.makeText(AddMealActivity.this,
                            "Chyba načtení dat", Toast.LENGTH_SHORT).show();
                    return;
                }
                // odfiltrovat jen položky, kde jsou dostupné proteiny >0
                List<Product> all = resp.body().getProducts();
                products = new ArrayList<>();
                for (Product p : all) {
                    if (p.getNutriments() != null
                            && p.getNutriments().getProteinsPer100g() > 0) {
                        products.add(p);
                    }
                }
                if (products.isEmpty()) {
                    Toast.makeText(AddMealActivity.this,
                            "Žádné položky s nenulovým obsahem bílkovin.\n"
                                    + "Zkus např. anglicky \"chicken breast\"",
                            Toast.LENGTH_LONG).show();
                }
                updateSpinner();
            }
            @Override public void onFailure(Call<SearchResponse> call, Throwable t) {
                Toast.makeText(AddMealActivity.this,
                        "OFF API selhalo: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateSpinner() {
        List<String> labels = new ArrayList<>();
        for (Product p : products) {
            double prot = p.getNutriments().getProteinsPer100g();
            labels.add(p.getDisplayName() + " (" + prot + " g/100 g)");
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                labels
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFoods.setAdapter(adapter);
    }
}

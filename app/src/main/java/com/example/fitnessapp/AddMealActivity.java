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
            int pos = spinnerFoods.getSelectedItemPosition();
            if (pos<0 || pos>=products.size()) {
                Toast.makeText(this, "Vyber položku", Toast.LENGTH_SHORT).show();
                return;
            }
            Product chosen = products.get(pos);

            String s = etQuantity.getText().toString().trim();
            if (s.isEmpty()) {
                Toast.makeText(this, "Zadej počet porcí", Toast.LENGTH_SHORT).show();
                return;
            }
            int portions = Integer.parseInt(s);

            double prot = chosen.getNutriments().getProteinsPer100g();
            double carbs= chosen.getNutriments().getCarbsPer100g();
            double fat  = chosen.getNutriments().getFatPer100g();

            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(new Date());

            db.addMeal(
                    chosen.getDisplayName(),
                    prot,
                    carbs,
                    fat,
                    portions,
                    date
            );
            finish();
        });
    }

    private void fetchProductsFromOff(String query) {
        ApiService svc = ApiClient.getClient().create(ApiService.class);
        svc.searchProducts(query, 1, 1).enqueue(new Callback<SearchResponse>() {
            @Override public void onResponse(Call<SearchResponse> call, Response<SearchResponse> resp) {
                if (!resp.isSuccessful() || resp.body()==null) {
                    Toast.makeText(AddMealActivity.this,
                            "Chyba OFF API", Toast.LENGTH_SHORT).show();
                    return;
                }
                // filtr: protein>0, carbo>0, fat>0
                products = resp.body().getProducts().stream()
                        .filter(p ->
                                p.getNutriments()!=null
                                        && p.getNutriments().getProteinsPer100g()>0
                                        && p.getNutriments().getCarbsPer100g()>0
                                        && p.getNutriments().getFatPer100g()>0
                        ).collect(Collectors.toList());
                if (products.isEmpty()) {
                    Toast.makeText(AddMealActivity.this,
                            "Žádné položky s nenulovými makronutrienty",
                            Toast.LENGTH_LONG).show();
                }
                updateSpinner();
            }
            @Override public void onFailure(Call<SearchResponse> call, Throwable t) {
                Toast.makeText(AddMealActivity.this,
                        "OFF selhalo: "+t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateSpinner() {
        List<String> labels = new ArrayList<>();
        for (Product p : products) {
            labels.add(String.format(
                    "%s (P:%.0f C:%.0f F:%.0f/100g)",
                    p.getDisplayName(),
                    p.getNutriments().getProteinsPer100g(),
                    p.getNutriments().getCarbsPer100g(),
                    p.getNutriments().getFatPer100g()
            ));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, labels
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFoods.setAdapter(adapter);
    }
}

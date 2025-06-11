package com.example.fitnessapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fitnessapp.api.ApiClient;
import com.example.fitnessapp.api.ApiService;
import com.example.fitnessapp.api.model.Product;
import com.example.fitnessapp.api.model.SearchResponse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
/**
 * Aktivita pro vyhledání potraviny přes Open Food Facts,
 * výběr porcí a přiřazení k datu.
 */
public class AddMealActivity extends AppCompatActivity {

    private EditText etSearchTerm, etQuantity;
    private Button btnSearch, btnSaveMeal;
    private Spinner spinnerFoods;
    private DatabaseHelper db;
    private List<Product> products = new ArrayList<>(); //Seznam vysledku z API

    private DatePicker datePicker;

    // Přijímáme datum z MainActivity
    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meal);

        // Získání data z MainActivity
        selectedDate = getIntent().getStringExtra("selectedDate");

        etSearchTerm = findViewById(R.id.etSearchTerm);
        btnSearch = findViewById(R.id.btnSearch);
        spinnerFoods = findViewById(R.id.spinnerFoods);
        etQuantity = findViewById(R.id.etQuantity);
        btnSaveMeal = findViewById(R.id.btnSaveMeal);
        datePicker = findViewById(R.id.datePicker);

        db = new DatabaseHelper(this);// Připojení k databázi

        // Listener na tlačítko pro vyhledání položek
        btnSearch.setOnClickListener(v -> {
            String term = etSearchTerm.getText().toString().trim();
            if (term.isEmpty()) {
                Toast.makeText(this, "Zadej hledaný výraz", Toast.LENGTH_SHORT).show();
            } else {
                fetchProductsFromOff(term);
            }
        });

        //Toast.makeText(this, selectedDate, Toast.LENGTH_SHORT).show();

        // Listener na tlačítko pro uložení jídla
        btnSaveMeal.setOnClickListener(v -> {
            int pos = spinnerFoods.getSelectedItemPosition();
            if (pos < 0 || pos >= products.size()) {
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
            // Vypočítáme makroživiny dle porcí
            double prot = chosen.getNutriments().getProteinsPer100g();
            double carbs = chosen.getNutriments().getCarbsPer100g();
            double fat = chosen.getNutriments().getFatPer100g();

            getSelectedDate();

            // Uložíme do DB: název, makra, porce, a původní selectedDate
            db.addMeal(
                    chosen.getDisplayName(),
                    prot,
                    carbs,
                    fat,
                    portions,
                    selectedDate  // Uložíme jídlo k aktuálně vybranému datu
            );
            Intent returnIntent = new Intent();
            returnIntent.putExtra("selectedDate", selectedDate);  // Předáme vybrané datum
            setResult(RESULT_OK, returnIntent);
            finish(); // Vrátíme se zpět do MainActivity
        });
    }
    /**
     * Vyhledá produkty v OFF API, filtruje je podle nenulových makroživin
     * a zobrazí v Spinneru.
     */
    private void fetchProductsFromOff(String query) {
        ApiService svc = ApiClient.getClient().create(ApiService.class);
        svc.searchProducts(query, 1, 1).enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> resp) {
                if (!resp.isSuccessful() || resp.body() == null) {
                    Toast.makeText(AddMealActivity.this, "Chyba OFF API", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Filtrujeme položky, které mají nenulová makra
                products = resp.body().getProducts().stream()
                        .filter(p -> p.getNutriments() != null
                                && p.getNutriments().getProteinsPer100g() > 0
                                && p.getNutriments().getCarbsPer100g() > 0
                                && p.getNutriments().getFatPer100g() > 0)
                        .collect(Collectors.toList());

                if (products.isEmpty()) {
                    Toast.makeText(AddMealActivity.this, "Žádné položky s nenulovými makronutrienty", Toast.LENGTH_LONG).show();
                }

                updateSpinner();
            }

            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                Toast.makeText(AddMealActivity.this, "OFF selhalo: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Naplní Spinner názvy produktů a jejich hodnotami makroživin.
     */
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

    //Denug metoda vraci logDatum
    private void getSelectedDate() {
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth() + 1; // Měsíce jsou 0-indexované
        int year = datePicker.getYear();
        selectedDate = String.format("%d-%02d-%02d", year, month, day);
        Log.d("SelectedDate", "New date selected in Addmeal: " + selectedDate);


    }
}

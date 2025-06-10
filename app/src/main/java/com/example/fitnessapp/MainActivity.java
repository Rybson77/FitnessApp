package com.example.fitnessapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private ListView listViewMeals;
    private TextView tvProtein, tvCarbs, tvFat;
    private ProgressBar progressProtein, progressCarbs, progressFat;
    private Button btnAddMeal, btnChangeDate;
    private List<Meal> todayMeals = new ArrayList<>();
    private String selectedDate;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(this);
        listViewMeals = findViewById(R.id.listMeals);
        tvProtein = findViewById(R.id.tvProtein);
        tvCarbs = findViewById(R.id.tvCarbs);
        tvFat = findViewById(R.id.tvFat);
        progressProtein = findViewById(R.id.progressProtein);
        progressCarbs = findViewById(R.id.progressCarbs);
        progressFat = findViewById(R.id.progressFat);
        btnAddMeal = findViewById(R.id.btnAddMeal);
        btnChangeDate = findViewById(R.id.btnChangeDate);

        // Výchozí datum - dnešní datum
        selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // Tlačítko pro přidání jídla
        btnAddMeal.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddMealActivity.class);
            intent.putExtra("selectedDate", selectedDate);  // Předáme vybrané datum
            startActivity(intent);
        });

        // Změna data
        btnChangeDate.setOnClickListener(v -> showDatePickerDialog());

        // Dlouhé podržení položky pro smazání
        listViewMeals.setOnItemLongClickListener((parent, view, position, id) -> {
            Meal m = todayMeals.get(position);
            db.deleteMeal(m.getId());
            Toast.makeText(this, "Smazáno: " + m.getLabel(), Toast.LENGTH_SHORT).show();
            displayTodayMeals();
            return true;
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        displayTodayMeals();
    }



    private void displayTodayMeals() {
        todayMeals = db.getMealsByDate(selectedDate);  // Načteme jídla pro vybrané datum

        List<String> display = new ArrayList<>();
        int totalP = 0, totalC = 0, totalF = 0;
        for (Meal m : todayMeals) {
            int p = (int) (m.getProteinPer100g() * m.getPortions());
            int c = (int) (m.getCarbsPer100g() * m.getPortions());
            int f = (int) (m.getFatPer100g() * m.getPortions());
            display.add(String.format(
                    "%s ×%d = %dg P, %dg S, %dg T",
                    m.getLabel(), m.getPortions(), p, c, f
            ));
            totalP += p;
            totalC += c;
            totalF += f;
        }

        tvProtein.setText("Protein: " + totalP + " g");
        tvCarbs.setText("Sacharidy: " + totalC + " g");
        tvFat.setText("Tuky: " + totalF + " g");

        // Nastavení max hodnot pro progress bary
        progressProtein.setMax(100);
        progressCarbs.setMax(100);
        progressFat.setMax(100);

        progressProtein.setProgress(totalP);
        progressCarbs.setProgress(totalC);
        progressFat.setProgress(totalF);

        listViewMeals.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                display
        ));
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate = String.format("%d-%02d-%02d", year, month + 1, dayOfMonth);
                    displayTodayMeals();  // Načteme a zobrazíme jídla pro nové datum
                    Toast.makeText(this, "Vybráno datum: " + selectedDate, Toast.LENGTH_SHORT).show();
                },
                Integer.parseInt(selectedDate.substring(0, 4)),
                Integer.parseInt(selectedDate.substring(5, 7)) - 1,
                Integer.parseInt(selectedDate.substring(8, 10))
        );
        datePickerDialog.show();
    }
}

package com.example.fitnessapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper db;
    private ListView listView;
    private TextView tvSummary;
    private ProgressBar progressBar;

    private static final int PROTEIN_GOAL = 100; // g

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(this);
        listView = findViewById(R.id.listViewMeals);
        tvSummary = findViewById(R.id.tvSummary);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(PROTEIN_GOAL);

        Button btnAdd = findViewById(R.id.btnAddMeal);
        btnAdd.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AddMealActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMeals();
    }

    private void loadMeals() {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        List<Meal> meals = db.getMealsByDate(today);

        // Připrav data pro ListView
        List<String> display = new ArrayList<>();
        int total = 0;
        List<Food> foods = Food.getFoods();

        for (Meal m : meals) {
            for (Food f : foods) {
                if (f.getId().equals(m.getFoodId())) {
                    int prot = f.getProtein() * m.getQuantity();
                    total += prot;
                    display.add(f.getName() + " x" + m.getQuantity() + " = " + prot + "g");
                    break;
                }
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                display
        );
        listView.setAdapter(adapter);

        tvSummary.setText("Celkem bílkovin: " + total + "g / " + PROTEIN_GOAL + "g");
        progressBar.setProgress(Math.min(total, PROTEIN_GOAL));
    }
}
package com.example.fitnessapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private ListView listViewMeals;
    private TextView tvProtein;
    private ProgressBar progressProtein;
    private Button btnAddMeal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(this);
        listViewMeals = findViewById(R.id.listMeals);
        tvProtein = findViewById(R.id.tvProtein);
        progressProtein = findViewById(R.id.progressProtein);
        btnAddMeal = findViewById(R.id.btnAddMeal);

        btnAddMeal.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AddMealActivity.class))
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayTodayMeals();
    }

    private void displayTodayMeals() {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(new Date());
        List<Meal> meals = db.getMealsByDate(today);

        List<String> display = new ArrayList<>();
        int totalProtein = 0;
        for (Meal m : meals) {
            int qty = m.getQuantity();
            double protPerUnit = m.getProtein();
            int protTotal = (int)(protPerUnit * qty);
            display.add(m.getLabel() + " ×" + qty + " = " + protTotal + " g");
            totalProtein += protTotal;
        }

        tvProtein.setText("Protein: " + totalProtein + " g");
        progressProtein.setMax(100); // nastav si svůj cíl
        progressProtein.setProgress(totalProtein);

        listViewMeals.setAdapter(
                new android.widget.ArrayAdapter<>(
                        this,
                        android.R.layout.simple_list_item_1,
                        display
                )
        );
    }
}

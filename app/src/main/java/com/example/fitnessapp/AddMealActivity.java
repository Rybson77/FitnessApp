package com.example.fitnessapp;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddMealActivity extends AppCompatActivity {
    private Spinner spinner;
    private EditText etQuantity;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meal);

        spinner = findViewById(R.id.spinnerFoods);
        etQuantity = findViewById(R.id.etQuantity);
        Button btnSave = findViewById(R.id.btnSaveMeal);

        db = new DatabaseHelper(this);
        List<Food> foods = Food.getFoods();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                foods.stream().map(Food::getName).toArray(String[]::new)
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        btnSave.setOnClickListener(v -> {
            int pos = spinner.getSelectedItemPosition();
            Food chosen = foods.get(pos);
            int qty = Integer.parseInt(etQuantity.getText().toString());
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            db.addMeal(chosen.getId(), qty, date);
            finish();
        });
    }
}
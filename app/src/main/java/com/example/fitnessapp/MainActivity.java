package com.example.fitnessapp;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private ListView listViewMeals;
    private TextView tvProtein, tvCarbs, tvFat;
    private ProgressBar progressProtein, progressCarbs, progressFat;
    private Button btnAddMeal, btnChangeDate;
    private List<Meal> todayMeals = new ArrayList<>();
    private String selectedDate;


    int i = 0;

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


        // Tlačítko pro přidání jídla
        btnAddMeal.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddMealActivity.class);
            intent.putExtra("selectedDate", selectedDate);  // Předáme vybrané datum
            startActivityForResult(intent, 1);
        });

        // Změna data
        btnChangeDate.setOnClickListener(v -> openDatePicker());

        // Dlouhé podržení položky pro smazání
        listViewMeals.setOnItemLongClickListener((parent, view, position, id) -> {
            Meal m = todayMeals.get(position);
            db.deleteMeal(m.getId());
            Toast.makeText(this, "Smazáno: " + m.getLabel(), Toast.LENGTH_SHORT).show();
            displayTodayMeals(selectedDate);
            return true;
        });
        if(i==0){
            selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            updateLabel(selectedDate);
            i++;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        displayTodayMeals(selectedDate);
    }

    private void displayTodayMeals(String selectDate) {
        this.selectedDate=selectDate;
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

        listViewMeals.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, display));
    }

    private void openDatePicker() {
        // Získáme aktuální datum z kalendáře
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Vytvoření DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                MainActivity.this,  // Kontext
                new DatePickerDialog.OnDateSetListener() {  // Listener pro získání vybraného data
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                        // Formátování vybraného data ve formátu YYYY-MM-DD
                        selectedDate = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                        // Změna textu tlačítka na vybrané datum
                        updateLabel(selectedDate);
                        // Můžete použít vybrané datum jak potřebujete
                        // Například, uložit do databáze nebo provést nějakou akci s tímto datem
                        Log.d("Picker-SelectedDate", "Vybrané datum: " + selectedDate);
                    }
                },
                year, month, day // Výchozí hodnoty kalendáře (dnesní datum)
        );

        // Nastavíme chování při zavření dialogu (volitelné)
        datePickerDialog.setOnDismissListener(dialog -> Log.d("Picker-DatePicker", "Dialog zavřen"));
        displayTodayMeals(selectedDate);
        updateLabel(selectedDate);
        // Ujistěte se, že se DatePickerDialog neuzavře automaticky (pokud je nastaveno nějaké chování pro výběr datumu)
        datePickerDialog.setCancelable(true);

        // Zobrazení DatePickerDialog
        datePickerDialog.show();
    }



    private void updateLabel(String selectedDate) {
        this.selectedDate = selectedDate;
        btnChangeDate.setText(selectedDate);
        db.displayAllMeals();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Zkontrolujeme, jestli je výsledek úspěšný
        if (requestCode == 1) {  // Pokud jde o náš requestCode
            if (resultCode == RESULT_OK) {
                String selectedDate = data.getStringExtra("selectedDate");  // Získáme poslaný datum
                Log.d("SelectedDate", "Vraceno z addactivity " + selectedDate);
                displayTodayMeals(selectedDate);
                updateLabel(selectedDate);
            }
        }
    }

}

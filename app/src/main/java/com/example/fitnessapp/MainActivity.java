package com.example.fitnessapp;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
    private View rootLayout;


    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Načteme kořenový layout
        rootLayout = findViewById(R.id.rootLayout);

        //Inicializujeme DB helper
        db = new DatabaseHelper(this);

        //Načteme odkazy na UI komponenty
        listViewMeals = findViewById(R.id.listMeals);
        tvProtein = findViewById(R.id.tvProtein);
        tvCarbs = findViewById(R.id.tvCarbs);
        tvFat = findViewById(R.id.tvFat);
        progressProtein = findViewById(R.id.progressProtein);
        progressCarbs = findViewById(R.id.progressCarbs);
        progressFat = findViewById(R.id.progressFat);
        btnAddMeal = findViewById(R.id.btnAddMeal);
        btnChangeDate = findViewById(R.id.btnChangeDate);


        // Otevření AddMealActivity pro přidání jídla
        btnAddMeal.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddMealActivity.class);
            intent.putExtra("selectedDate", selectedDate);  // Předáme vybrané datum
            startActivityForResult(intent, 1);
        });

        // Otevření DatePickerDialogu
        btnChangeDate.setOnClickListener(v -> openDatePicker());

        // Dlouhý klik pro smazání položky
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
        displayTodayMeals(selectedDate); // Při návratu aktualizujeme UI
    }
    /**
     * Nacte z DB polozky pro selectedDate a prerendruje ListView + ProgressBary.
     */
    private void displayTodayMeals(String selectDate) {
        this.selectedDate=selectDate;
        Log.d("Picker-MealsTodayDate", "Vybrané datum: " + selectedDate);
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
    /**
     * Zobrazi DatePickerDialog, schova UI až po vyběru data.
     */
    private void openDatePicker() {
        rootLayout.setVisibility(View.GONE);
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR),
                month = c.get(Calendar.MONTH),
                day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(
                this,
                R.style.MyDatePickerDialogTheme,
                (view, y, m, d) -> {
                    String sel = String.format("%04d-%02d-%02d", y, m+1, d);
                    updateLabel(sel);
                    displayTodayMeals(sel);
                },
                year, month, day
        );
        //Po zavření dialogu UI opět zobrazíme
        dpd.setOnDismissListener(dialog -> rootLayout.setVisibility(View.VISIBLE));
        dpd.show();

        // Po zobrazeni dialogu upravime jeho Window tak, aby byl vycentrovany
        Window window = dpd.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.gravity = Gravity.CENTER;             // Ustrednani dialogu
            params.width   = WindowManager.LayoutParams.WRAP_CONTENT;
            params.height  = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(params);
        }
    }


    /** Aktualizuje text na tlačítku na aktuálně vybrané datum. */
    private void updateLabel(String selectedDate) {
        this.selectedDate = selectedDate;
        btnChangeDate.setText(selectedDate);
        db.displayAllMeals();
    }

    /** Po ukonceni aktivity Addmeal prijme parametr selected date aby se otevreli zaznamy k tomu dni*/
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
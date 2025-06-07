package com.example.fitnessapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "fitness.db";
    private static final int DB_VER = 1;
    private static final String TABLE_MEALS = "meals";

    public DatabaseHelper(Context ctx) {
        super(ctx, DB_NAME, null, DB_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_MEALS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "label TEXT," +
                "protein REAL," +
                "quantity INTEGER," +
                "date TEXT)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEALS);
        onCreate(db);
    }

    public void addMeal(String label, double proteinPerUnit, int qty, String date) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("label", label);
        cv.put("protein", proteinPerUnit);
        cv.put("quantity", qty);
        cv.put("date", date);
        db.insert(TABLE_MEALS, null, cv);
        db.close();
    }

    public List<Meal> getMealsByDate(String date) {
        List<Meal> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(
                TABLE_MEALS,
                new String[]{"label","protein","quantity"},
                "date = ?",
                new String[]{date},
                null,null,null
        );
        while (c.moveToNext()) {
            String label = c.getString(0);
            double prot = c.getDouble(1);
            int qty   = c.getInt(2);
            list.add(new Meal(label, prot, qty));
        }
        c.close();
        db.close();
        return list;
    }
}

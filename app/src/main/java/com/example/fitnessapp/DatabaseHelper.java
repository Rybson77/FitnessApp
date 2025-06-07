package com.example.fitnessapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME     = "fitness.db";
    private static final int    DB_VER      = 2;
    private static final String TABLE_MEALS = "meals";

    // sloupce
    private static final String COL_ID       = "id";
    private static final String COL_LABEL    = "label";
    private static final String COL_PROTEIN  = "protein";
    private static final String COL_QUANTITY = "quantity";
    private static final String COL_DATE     = "date";

    public DatabaseHelper(Context ctx) {
        super(ctx, DB_NAME, null, DB_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_MEALS + " ("
                + COL_ID       + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_LABEL    + " TEXT, "
                + COL_PROTEIN  + " REAL, "
                + COL_QUANTITY + " INTEGER, "
                + COL_DATE     + " TEXT"
                + ")";
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
        cv.put(COL_LABEL,    label);
        cv.put(COL_PROTEIN,  proteinPerUnit);
        cv.put(COL_QUANTITY, qty);
        cv.put(COL_DATE,     date);
        db.insert(TABLE_MEALS, null, cv);
        db.close();
    }

    public List<Meal> getMealsByDate(String date) {
        List<Meal> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(
                TABLE_MEALS,
                new String[]{COL_ID, COL_LABEL, COL_PROTEIN, COL_QUANTITY},
                COL_DATE + " = ?",
                new String[]{ date },
                null, null, null
        );
        while (c.moveToNext()) {
            int    id       = c.getInt(0);
            String label    = c.getString(1);
            double protein  = c.getDouble(2);
            int    quantity = c.getInt(3);
            list.add(new Meal(id, label, protein, quantity));
        }
        c.close();
        db.close();
        return list;
    }

    public void deleteMeal(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_MEALS, COL_ID + " = ?", new String[]{ String.valueOf(id) });
        db.close();
    }
}

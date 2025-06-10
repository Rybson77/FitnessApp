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
    private static final int    DB_VER      = 3;                  // ++ verze
    private static final String TABLE_MEALS = "meals";

    // sloupce
    private static final String COL_ID       = "id";
    private static final String COL_LABEL    = "label";
    private static final String COL_PROTEIN  = "protein";
    private static final String COL_CARBS    = "carbs";
    private static final String COL_FAT      = "fat";
    private static final String COL_QTY      = "quantity";
    private static final String COL_DATE     = "date";

    public DatabaseHelper(Context ctx) {
        super(ctx, DB_NAME, null, DB_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_MEALS + " ("
                + COL_ID      + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_LABEL   + " TEXT, "
                + COL_PROTEIN + " REAL, "
                + COL_CARBS   + " REAL, "
                + COL_FAT     + " REAL, "
                + COL_QTY     + " INTEGER, "
                + COL_DATE    + " TEXT"
                + ")";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEALS);
        onCreate(db);
    }

    public void addMeal(String label,
                        double proteinPer100g,
                        double carbsPer100g,
                        double fatPer100g,
                        int portions,
                        String date) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_LABEL,    label);
        cv.put(COL_PROTEIN,  proteinPer100g);
        cv.put(COL_CARBS,    carbsPer100g);
        cv.put(COL_FAT,      fatPer100g);
        cv.put(COL_QTY,      portions);
        cv.put(COL_DATE,     date);
        db.insert(TABLE_MEALS, null, cv);
        db.close();
    }

    public List<Meal> getMealsByDate(String date) {
        List<Meal> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(
                TABLE_MEALS,
                new String[]{COL_ID, COL_LABEL, COL_PROTEIN, COL_CARBS, COL_FAT, COL_QTY},
                COL_DATE + " = ?",
                new String[]{ date },
                null, null, null
        );
        while (c.moveToNext()) {
            int    id       = c.getInt(0);
            String label    = c.getString(1);
            double prot     = c.getDouble(2);
            double carbs    = c.getDouble(3);
            double fat      = c.getDouble(4);
            int    qty      = c.getInt(5);
            list.add(new Meal(id, label, prot, carbs, fat, qty));
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

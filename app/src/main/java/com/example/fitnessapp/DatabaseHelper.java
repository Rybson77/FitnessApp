package com.example.fitnessapp;



import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "fitness.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_MEALS = "meals";
    public static final String COL_ID = "id";
    public static final String COL_FOOD_ID = "food_id";
    public static final String COL_QUANTITY = "quantity";
    public static final String COL_DATE = "date";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_MEALS + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_FOOD_ID + " TEXT, "
                + COL_QUANTITY + " INTEGER, "
                + COL_DATE + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEALS);
        onCreate(db);
    }

    public long addMeal(String foodId, int quantity, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_FOOD_ID, foodId);
        values.put(COL_QUANTITY, quantity);
        values.put(COL_DATE, date);
        return db.insert(TABLE_MEALS, null, values);
    }

    public List<Meal> getMealsByDate(String date) {
        List<Meal> meals = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_MEALS,
                null,
                COL_DATE + " = ?",
                new String[]{ date },
                null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID));
                String foodId = cursor.getString(cursor.getColumnIndexOrThrow(COL_FOOD_ID));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COL_QUANTITY));
                String mealDate = cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE));
                meals.add(new Meal(id, foodId, quantity, mealDate));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return meals;
    }
}
package com.example.fitnessapp;

import java.util.Arrays;
import java.util.List;

public class Food {
    private final String id;
    private final String name;
    private final int protein;

    public Food(String id, String name, int protein) {
        this.id = id;
        this.name = name;
        this.protein = protein;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public int getProtein() { return protein; }

    public static List<Food> getFoods() {
        return Arrays.asList(
                new Food("egg", "Vejce na tvrdo", 6),
                new Food("chicken", "Kuřecí prso", 31),
                new Food("cottage_cheese", "Tvaroh", 11)
        );
    }
}
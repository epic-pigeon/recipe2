package com.kar.recipe.DataClasses;

public class Ingredient extends DataClass {
    private int id;
    private String name;

    public Ingredient(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

package com.kar.recipe.DataClasses;

public class Ingredient extends DataClass {
    private int id;
    private String name;
    private String units;

    public Ingredient(int id, String name, String units) {
        this.id = id;
        this.name = name;
        this.units = units;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUnits() {
        return units;
    }
}

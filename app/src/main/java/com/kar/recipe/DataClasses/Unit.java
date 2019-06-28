package com.kar.recipe.DataClasses;

public class Unit extends DataClass {
    private int id;
    private String name;

    public Unit(int id, String name) {
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

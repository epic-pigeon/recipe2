package com.kar.recipe;

public class IngerdientsUI {
    private String name;
    private int count;
    private int idOfUnit;

    public IngerdientsUI(String name, int count, int idOfUnit) {
        this.name = name;
        this.count = count;
        this.idOfUnit = idOfUnit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getIdOfUnit() {
        return idOfUnit;
    }

    public void setIdOfUnit(int idOfUnit) {
        this.idOfUnit = idOfUnit;
    }
}

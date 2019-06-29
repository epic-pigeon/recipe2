package com.kar.recipe.DataClasses;

public class RecipeIngredient extends DataClass {
    private int id;
    private int recipeID;
    private int ingredientID;
    private double amount;
    private int unitID;
    private transient Recipe recipe;
    private Ingredient ingredient;
    private Unit unit;

    public RecipeIngredient(int id, int recipeID, int ingredientID, int unitID, double amount) {
        this.id = id;
        this.recipeID = recipeID;
        this.ingredientID = ingredientID;
        this.amount = amount;
        this.unitID = unitID;
    }

    void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public int getId() {
        return id;
    }

    public int getRecipeID() {
        return recipeID;
    }

    public int getIngredientID() {
        return ingredientID;
    }

    public double getAmount() {
        return amount;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public int getUnitID() {
        return unitID;
    }

    public Unit getUnit() {
        return unit;
    }

    void setUnit(Unit unit) {
        this.unit = unit;
    }
}

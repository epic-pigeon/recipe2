package com.kar.recipe.DataClasses;

public class RecipeIngredient extends DataClass {
    private int id;
    private int recipeID;
    private int ingredientID;
    private double amount;
    private transient Recipe recipe;
    private Ingredient ingredient;

    public RecipeIngredient(int id, int recipeID, int ingredientID, double amount) {
        this.id = id;
        this.recipeID = recipeID;
        this.ingredientID = ingredientID;
        this.amount = amount;
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
}

package com.kar.recipe.DataClasses;

import android.os.Build;
import android.support.annotation.RequiresApi;

import com.kar.recipe.DBHandle.Collection;

public class Data extends DataClass {
    private Collection<User> users;
    private Collection<Recipe> recipes;
    private Collection<Ingredient> ingredients;
    private Collection<Connection> userSaves;
    private Collection<RecipeIngredient> recipeIngredients;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Data(Collection<User> users, Collection<Recipe> recipes, Collection<Ingredient> ingredients, Collection<Connection> userSaves, Collection<RecipeIngredient> recipeIngredients) {
        this.users = users;
        this.recipes = recipes;
        this.ingredients = ingredients;
        this.userSaves = userSaves;
        this.recipeIngredients = recipeIngredients;
        buildConnections();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void buildConnections() {
        for (User user: users) {
            Collection<Recipe> saves = new Collection<>();
            for (Integer userSave: userSaves.find(connection -> connection.getID(0) == user.getId()).map(connection -> connection.getID(1))) {
                saves.add(
                        recipes.findFirst(recipe -> recipe.getId() == userSave)
                );
            }
            user.setSaves(saves);
        }
        for (Recipe recipe: recipes) {
            Collection<RecipeIngredient> ingredients = new Collection<>();
            for (RecipeIngredient recipeIngredient: recipeIngredients.find(connection -> connection.getRecipeID() == recipe.getId())) {
                recipeIngredient.setRecipe(recipe);
                recipeIngredient.setIngredient(this.ingredients.findFirst(ingredient -> ingredient.getId() == recipeIngredient.getIngredientID()));
                ingredients.add(
                        recipeIngredient
                );
            }
            recipe.setIngredients(ingredients);
        }
    }

    public Collection<User> getUsers() {
        return users;
    }

    public Collection<Recipe> getRecipes() {
        return recipes;
    }

    public Collection<Ingredient> getIngredients() {
        return ingredients;
    }

    public Collection<Connection> getUserSaves() {
        return userSaves;
    }

    public Collection<RecipeIngredient> getRecipeIngredients() {
        return recipeIngredients;
    }
}

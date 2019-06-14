package com.kar.recipe.DataClasses;


import android.graphics.Bitmap;

import com.kar.recipe.DBHandle.Collection;
import com.kar.recipe.DBHandle.Constants;
import com.kar.recipe.DBHandle.DBHandler;

import java.io.IOException;

public class Recipe extends DataClass {
    private int id;
    private String name;
    private String cooking;
    private String photo;
    private transient Collection<RecipeIngredient> ingredients;
    private transient Bitmap image;

    public Recipe(int id, String name, String cooking, String photo) {
        this.id = id;
        this.name = name;
        this.cooking = cooking;
        this.photo = photo;
    }

    void setIngredients(Collection<RecipeIngredient> ingredients) {
        this.ingredients = ingredients;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCooking() {
        return cooking;
    }

    public String getPhoto() {
        return photo;
    }

    public String getPhotoExtension() {
        return photo.split("\\.")[1];
    }

    public Collection<RecipeIngredient> getIngredients() {
        return ingredients;
    }

    public Bitmap getImage() throws IOException {
        if (image == null) updateImage();
        return image;
    }
    public void updateImage() throws IOException {
        image = fetchImage();
    }
    private Bitmap fetchImage() throws IOException {
        return DBHandler.loadImageViaUrl(Constants.SERVER.getRecipePhotoURL(photo));
    }
}

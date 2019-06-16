package com.kar.recipe.DataClasses;


import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.kar.recipe.DBHandle.Collection;
import com.kar.recipe.DBHandle.Constants;
import com.kar.recipe.DBHandle.DBHandler;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

public class Recipe extends DataClass {
    private int id;
    private String name;
    private String cooking;
    private String photo;
    @DataClassDoNotShow
    private Collection<RecipeIngredient> ingredients;
    @DataClassDoNotShow
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
    public void getImageAsync(Consumer<Bitmap> callback) {
        final Bitmap[] bitmap = new Bitmap[1];
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    bitmap[0] = image == null ? DBHandler.loadImageViaUrl(Constants.SERVER.getRecipePhotoURL(photo)) : image;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            protected void onPostExecute(Void aVoid) {
                image = bitmap[0];
                callback.accept(image);
            }
        }.execute();
    }
    public void updateImage() throws IOException {
        image = fetchImage();
    }
    private Bitmap fetchImage() throws IOException {
        final Bitmap[] bitmap = new Bitmap[1];
        CountDownLatch latch = new CountDownLatch(1);
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    bitmap[0] = DBHandler.loadImageViaUrl(Constants.SERVER.getRecipePhotoURL(photo));
                    latch.countDown();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return bitmap[0];
    }
}

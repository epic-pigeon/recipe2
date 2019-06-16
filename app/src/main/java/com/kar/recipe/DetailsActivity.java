package com.kar.recipe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Scroller;
import android.widget.TextView;

import com.kar.recipe.DataClasses.Recipe;
import com.kar.recipe.DataClasses.RecipeIngredient;

import java.io.IOException;

public class DetailsActivity extends Activity {
    Recipe recipe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Intent intent = getIntent();
        recipe = (Recipe) (savedInstanceState != null ?
                savedInstanceState.getSerializable("recipe") :
                intent.getBundleExtra("recipe_bundle").getSerializable("recipe"));
        ImageView imageView = findViewById(R.id.imageView2);
        TextView textView = findViewById(R.id.textView);
        textView.setMovementMethod(new ScrollingMovementMethod());
        StringBuilder description = new StringBuilder();
        for (RecipeIngredient recipeIngredient: recipe.getIngredients()) {
            description.append("    ").append(recipeIngredient.getIngredient().getName()).append("(").append(recipeIngredient.getAmount()).append(" ").append(recipeIngredient.getIngredient().getUnits()).append(")\n");
        }
        description.append(recipe.getCooking());
        textView.setText(description.toString());
        recipe.getImageAsync(imageView::setImageBitmap);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("recipe", recipe);
        super.onSaveInstanceState(outState);
    }
}

package com.kar.recipe;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.kar.recipe.DBHandle.Collection;
import com.kar.recipe.DBHandle.DBHandler;
import com.kar.recipe.DataClasses.Data;
import com.kar.recipe.DataClasses.Ingredient;
import com.kar.recipe.DataClasses.Recipe;
import com.kar.recipe.DataClasses.RecipeIngredient;

import java.io.FileFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.ServiceConfigurationError;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Filter;
import java.util.stream.IntStream;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener , SearchView.OnQueryTextListener {

    private static Collection<Recipe> recipes;
    private ListView listView;
    private DishAdapter dishAdapter;
    private SearchView mSearchView;
    private boolean onlySaves = false;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        CountDownLatch latch = new CountDownLatch(1);

        GetRecipesTask task = new GetRecipesTask(latch);
        task.execute();

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Intent intent = getIntent();
        if (intent != null) {
            onlySaves = intent.getBooleanExtra("only_saves", false);
            Log.d("onlysaves", onlySaves ? "true" : "false");
        }

        if (onlySaves) {
            recipes = recipes.find(recipe -> GeneralData.user.getSaves().findFirst(recipe1 -> recipe.getId() == recipe1.getId()) != null);
        }

        listView = (ListView) findViewById(R.id.listView);
        listView.setTextFilterEnabled(true);
        dishAdapter = new DishAdapter(recipes);
        listView.setAdapter(dishAdapter);

        mSearchView = (SearchView) findViewById(R.id.searchView_dish);
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setQueryHint("Search Here");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        dishAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (TextUtils.isEmpty(newText)) {
            listView.clearTextFilter();
        } else {
            listView.setFilterText(newText);
        }
        return true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (GeneralData.user != null) {
            TextView textView_name = (TextView) findViewById(R.id.user_name);
            textView_name.setText(GeneralData.user.getName());
            ImageView imageView_avatar = (ImageView) findViewById(R.id.avatar_imageView);
            try {
                imageView_avatar.setImageBitmap(GeneralData.user.getAvatarImage());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.nav_recipes) {
            if (GeneralData.user != null) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("only_saves", false);
                startActivity(intent);
            } else Snackbar.make(getWindow().getDecorView().getRootView(), "Вы не вошли в аккаунт!", Snackbar.LENGTH_LONG).show();
        } else if (id == R.id.nav_favorite_recipes) {
            if (GeneralData.user != null) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("only_saves", true);
                startActivity(intent);
            } else Snackbar.make(getWindow().getDecorView().getRootView(), "Вы не вошли в аккаунт!", Snackbar.LENGTH_LONG).show();
        } else if (id == R.id.nav_sign_in) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        //@string/nav_header_title
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class DishAdapter extends BaseAdapter implements Filterable {

        private Collection<Recipe> current;

        public DishAdapter(Collection<Recipe> current) {
            this.current = current;
        }

        @Override
        public android.widget.Filter getFilter() {
            return new android.widget.Filter() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    Collection<Recipe> recipesNew = recipes.find(recipe -> {
                        if (recipe.getName().toLowerCase().contains(constraint.toString().toLowerCase())) return true;
                        return recipe.getIngredients().map(RecipeIngredient::getIngredient).findFirst(ingredient -> ingredient.getName().toLowerCase().contains(
                                constraint.toString().toLowerCase()
                        )) != null;
                    });

                    filterResults.count = recipesNew.size();
                    filterResults.values = recipesNew;
                    return filterResults;
                }

                /*@RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    Collection<Recipe> recipesNew = new Collection<>();
                    String[] ingredients = constraint.toString().toLowerCase().split(" ");
                    for (Recipe i : recipes) {
                        boolean fl = true;
                        for (String j : ingredients) {
                            if (i.getIngredients().findFirst(recipeIngredient -> recipeIngredient.getIngredient().getName().toLowerCase().contains(j)) == null &&
                            !i.getName().toLowerCase().contains(j)) {
                                fl = false;
                                break;
                            }
                        }
                        if (fl && recipesNew.indexOf(i) == -1 &&
                                (!onlySaves || GeneralData.user.getSaves().findFirst(save -> save.getId() == i.getId()) != null)) {
                            recipesNew.add(i);
                        }
                    }
                    filterResults.count = recipesNew.size();
                    filterResults.values = recipesNew;
                    return filterResults;
                }*/

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    current = (Collection<Recipe>) results.values;
                    notifyDataSetChanged();
                }
            };
        }

        @Override
        public int getCount() {
            return current.size();
        }

        @Override
        public Object getItem(int position) {
            return current.get(position);
        }

        @Override
        public long getItemId(int position) {
            return current.get(position).getId();
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.dishlayout, null);

            ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
            TextView textView = (TextView) convertView.findViewById(R.id.textView_name);
            ImageButton imageButton = (ImageButton) convertView.findViewById(R.id.imageButton_favorite);

            if(GeneralData.user != null){
                if (GeneralData.user.getSaves().findFirst(recipe -> recipe.getId() == current.get(position).getId()) != null){
                    imageButton.setImageResource(R.drawable.like);
                    imageButton.setSelected(true);
                }else{

                    imageButton.setImageResource(R.drawable.not_like);
                    imageButton.setSelected(false);
                }
            }else{
                imageButton.setImageResource(R.drawable.not_like);
                imageButton.setSelected(false);
            }

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Recipe recipe = current.get(position);
                    Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("recipe", recipe);
                    intent.putExtra("recipe_bundle", bundle);
                    startActivity(intent);
                }
            });

            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (GeneralData.user != null) {
                        imageButton.setSelected(!imageButton.isSelected());
                        if (imageButton.isSelected()) {
                            imageButton.setImageResource(R.drawable.like);
                            Snackbar.make(view, "Добавлено к помеченным", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            new AsyncTask<Void, Void, Data>() {
                                @RequiresApi(api = Build.VERSION_CODES.N)
                                @Override
                                protected Data doInBackground(Void... voids) {
                                    try {
                                        DBHandler.addSave(GeneralData.user.getId(), current.get(position).getId());
                                        DBHandler.updateData();
                                        return DBHandler.getData();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        return null;
                                    }
                                }

                                @Override
                                protected void onPostExecute(Data data) {
                                    recipes = data.getRecipes();
                                    GeneralData.user = data.getUsers().findFirst(user -> user.getId() == GeneralData.user.getId());
                                    if (onlySaves) {
                                        recipes = recipes.find(recipe -> GeneralData.user.getSaves().findFirst(recipe1 -> recipe.getId() == recipe1.getId()) != null);
                                    }
                                    notifyDataSetChanged();
                                }
                            }.execute();
                        } else {
                            imageButton.setImageResource(R.drawable.not_like);
                            Snackbar.make(view, "Удалено из помеченных", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            new AsyncTask<Void, Void, Data>() {
                                @RequiresApi(api = Build.VERSION_CODES.N)
                                @Override
                                protected Data doInBackground(Void... voids) {
                                    try {
                                        DBHandler.removeSave(GeneralData.user.getId(), current.get(position).getId());
                                        DBHandler.updateData();
                                        return DBHandler.getData();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        return null;
                                    }
                                }

                                @Override
                                protected void onPostExecute(Data data) {
                                    recipes = data.getRecipes();
                                    GeneralData.user = data.getUsers().findFirst(user -> user.getId() == GeneralData.user.getId());
                                    if (onlySaves) {
                                        recipes = recipes.find(recipe -> GeneralData.user.getSaves().findFirst(recipe1 -> recipe.getId() == recipe1.getId()) != null);
                                    }
                                    notifyDataSetChanged();
                                }
                            }.execute();

                        }
                    }else{
                        Snackbar.make(view, "Вы не вошли в аккаунт!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }
            });


            try {
                imageView.setImageBitmap(current.get(position).getImage());
            } catch (IOException e) {
                e.printStackTrace();
            }

            textView.setText(current.get(position).getName());

            return convertView;
        }

        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }
    }
    private static class GetRecipesTask extends AsyncTask<Void, Void, Collection<Recipe>> {
        private CountDownLatch latch;

        public GetRecipesTask(CountDownLatch latch) {
            this.latch = latch;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Collection<Recipe> doInBackground(Void... voids) {
            try {
                Collection<Recipe> recipes1 = DBHandler.getData().getRecipes();
                recipes = recipes1;
                latch.countDown();
                return recipes1;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}

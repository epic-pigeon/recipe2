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
import com.kar.recipe.DataClasses.Recipe;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;


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

    //Перезагружаю страницу в любой удобный момент
    @Override
    protected void onRestart() {
        super.onRestart();
        //Если пользователь вошел в свой аккаунт
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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Если нажата какая-то кнопка на шторке - вызывается этот метод
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_recipes) {
            //Если нажаты рецепты
            if (GeneralData.user != null) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("only_saves", false);
                startActivity(intent);
            } else Snackbar.make(getWindow().getDecorView().getRootView(), "Вы не вошли в аккаунт!", Snackbar.LENGTH_LONG).show();
        } else if (id == R.id.nav_favorite_recipes) {
            //Иначе если нажаты любимые рецепты
            if (GeneralData.user != null) {
                //Если мы вошли
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("only_saves", true);
                startActivity(intent);
            } else Snackbar.make(getWindow().getDecorView().getRootView(), "Вы не вошли в аккаунт!", Snackbar.LENGTH_LONG).show();
            //Иначе уведомление
        } else if (id == R.id.nav_sign_in) {
            //Иначе если нажат вход в аккаунт
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_exit) {
            if (GeneralData.user != null) {
                GeneralData.user = null;
                startActivity(new Intent(this, MainActivity.class));
            } else Snackbar.make(getWindow().getDecorView().getRootView(), "Вы не вошли в аккаунт!", Snackbar.LENGTH_LONG).show();

        } else;
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Адаптер для ListView (список блюд)
    private class DishAdapter extends BaseAdapter implements Filterable {

        private Collection<Recipe> current;

        public DishAdapter(Collection<Recipe> current) {
            this.current = current;
        }

        //Фильтр для поиска блюд
        @Override
        public android.widget.Filter getFilter() {
            return new android.widget.Filter() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    Collection<Recipe> recipesNew = new Collection<>();
                    //Разделяем все, что ввели в поиск по пробелам и присваиваем в массив
                    String[] ingredients = constraint.toString().toLowerCase().split(" ");
                    //Проходим по всем рецептам
                    for (Recipe i : recipes) {
                        boolean fl = true;//Подходит ли нам рецепт

                        //Проходим по всему массиву, который ввели в поиск
                        for (String j : ingredients) {
                            //Если в рецепте такого ингредиента нет и это не название блюда - выходим из цикла и говорим, что блюдо не подходит
                            if (i.getIngredients().findFirst(recipeIngredient -> recipeIngredient.getIngredient().getName().toLowerCase().contains(j)) == null &&
                            !i.getName().toLowerCase().contains(j)) {
                                fl = false;
                                break;
                            }
                        }
                        //Если блюдо подходит и мы его еще не присваивали в результат
                        if (fl && recipesNew.indexOf(i) == -1 &&
                                (!onlySaves || GeneralData.user.getSaves().findFirst(save -> save.getId() == i.getId()) != null)) {
                            recipesNew.add(i);
                        }
                    }
                    filterResults.count = recipesNew.size();
                    filterResults.values = recipesNew;
                    return filterResults;
                }

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


        //Метод по инициализации "полоски" из списка рецептов
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.dishlayout, null);

            ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);//фото рецепта
            TextView textView = (TextView) convertView.findViewById(R.id.textView_name);//Название рецепта
            ImageButton imageButton = (ImageButton) convertView.findViewById(R.id.imageButton_favorite);//Лайк

            //Если мы вошли в аккаунт - расставим лайки блюдам
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

            //Если мы нажали на фото блюда - более подробный его рецепт
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

            //Если мы нажали лайк
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


            //Присваиваем фото блюда и название
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

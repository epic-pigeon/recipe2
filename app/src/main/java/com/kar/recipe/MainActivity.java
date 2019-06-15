package com.kar.recipe;

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
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.kar.recipe.DBHandle.Collection;
import com.kar.recipe.DBHandle.DBHandler;
import com.kar.recipe.DataClasses.Recipe;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String[] namesOfRecipes = {"Голубцы" , "Пельмени" , "Запеканка" , "Бутереброд", "Омлет" , "Картошка Фри" ,
            "Борщ", "Окрошка", "Крабовый салат", "Оливье", "Запеченный карп" , "Яблочный штрудель" , "Эклер" , "Салат Цезарь"};
    private int[] IMAGES = {R.drawable.golobci, R.drawable.pelmeni, R.drawable.zapekanka, R.drawable.sandwich, R.drawable.omlet,
            R.drawable.fri , R.drawable.borch, R.drawable.okroshka, R.drawable.krabpviy_salat, R.drawable.olive, R.drawable.karp,
            R.drawable.yablochniy_shtrudel, R.drawable.ekler, R.drawable.cezar_salat};

    private static Collection<Recipe> recipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        CountDownLatch latch = new CountDownLatch(1);

        GetRecipesTask task = new GetRecipesTask(latch);
        task.execute();

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        ListView listView = (ListView) findViewById(R.id.listView);
        DishAdapter dishAdapter = new DishAdapter();
        listView.setAdapter(dishAdapter);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
        TextView textView = (TextView) findViewById(R.id.my_text_view);

        if (id == R.id.nav_recipes) {
            // Handle the camera action
            textView.setText("Рецепты");
        } else if (id == R.id.nav_favorite_recipes) {
            textView.setText("Любимые Рецепты");
        } else if (id == R.id.nav_search) {
            textView.setText("Поиск");
        } else if (id == R.id.nav_sign_in) {
            textView.setText("Войти");
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        //@string/nav_header_title
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    class DishAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return recipes.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.dishlayout, null);

            ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
            TextView textView = (TextView) convertView.findViewById(R.id.textView_name);
            ImageButton imageButton = (ImageButton) convertView.findViewById(R.id.imageButton_favorite);

            imageButton.setImageResource(R.drawable.ic_menu_gallery);

            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    imageButton.setSelected(!imageButton.isSelected());
                    if (imageButton.isSelected()){
                        imageButton.setImageResource(R.drawable.ic_menu_camera);
                        Snackbar.make(view, "Добавлено к помеченным", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }else{
                        imageButton.setImageResource(R.drawable.ic_menu_gallery);
                        Snackbar.make(view, "Удалено из помеченных", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }
            });


            try {
                imageView.setImageBitmap(recipes.get(position).getImage());
            } catch (IOException e) {
                e.printStackTrace();
            }

            textView.setText(recipes.get(position).getName());

            return convertView;
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
                Log.d("karkar", recipes1.toString());
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

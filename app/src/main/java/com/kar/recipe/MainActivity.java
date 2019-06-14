package com.kar.recipe;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String[] namesOfRecipes = {"Голубцы" , "Пельмени" , "Запеканка" , "Бутереброд", "Омлет" , "Картошка Фри" ,
            "Борщ", "Окрошка", "Крабовый салат", "Оливье", "Запеченный карп" , "Яблочный штрудель" , "Эклер" , "Салат Цезарь"};
    private int[] IMAGES = {R.drawable.golobci, R.drawable.pelmeni, R.drawable.zapekanka, R.drawable.sandwich, R.drawable.omlet,
            R.drawable.fri , R.drawable.borch, R.drawable.okroshka, R.drawable.krabpviy_salat, R.drawable.olive, R.drawable.karp,
            R.drawable.yablochniy_shtrudel, R.drawable.ekler, R.drawable.cezar_salat};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
            return namesOfRecipes.length;
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

            imageView.setImageResource(IMAGES[position]);
            textView.setText(namesOfRecipes[position]);

            return convertView;
        }
    }
}

package com.kar.recipe;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class AddRecipeActivity extends AppCompatActivity {

    private ImageView recipe_avatar;
    private EditText recipe_name;
    private EditText recipe_description;
    private Button add_recipe_button;
    private Button add_ingredient_button;
    private ListView ingredients;
    private String[] UNITS = {"ст." , "с.л." , "ч.л."};
    private ArrayList<String> ing = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        recipe_avatar = (ImageView) findViewById(R.id.recipe_photo_add);
        recipe_name = (EditText) findViewById(R.id.recipe_name_editText);
        recipe_description = (EditText) findViewById(R.id.recipe_des_editText);
        add_recipe_button = (Button) findViewById(R.id.button_add_recipe);
        add_ingredient_button = (Button) findViewById(R.id.button_add_ingredient);

        ing.add("kar");

        ingredients = (ListView) findViewById(R.id.listView_ingredients);
        IngredientCustomAdapter custom_adapter = new IngredientCustomAdapter();
        ingredients.setAdapter(custom_adapter);

        recipe_avatar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                attemptChooseAvatar();
            }
        });

        add_recipe_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptAddRecipe();
            }
        });

        add_ingredient_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ing.add("kar");
                custom_adapter.notifyDataSetChanged();
            }
        });
    }

    private void attemptAddRecipe(){
        if (recipe_name.getText().length() < 3){

        }else if (recipe_description.getText().length() < 15){

        }else{
            //TODO Dish Register

        }
    }

    private void attemptChooseAvatar(){
        Intent intent = new Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, "Choose photo"), 123);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 123 && resultCode == RESULT_OK) {
            Uri selectedfile = data.getData(); //The uri with the location of the file
            new AsyncTask<Void, Void, Bitmap>() {
                @Override
                protected Bitmap doInBackground(Void... voids) {
                    try {
                        ContentResolver cR = AddRecipeActivity.this.getContentResolver();
                        MimeTypeMap mime = MimeTypeMap.getSingleton();
                        String type = mime.getExtensionFromMimeType(cR.getType(selectedfile));
                        Bitmap image = MediaStore.Images.Media.getBitmap(AddRecipeActivity.this.getContentResolver(), selectedfile);
                        /*DBHandler.changeUserAvatar(
                                GeneralData.user.getId(),
                                image,
                                type
                        );
                        */
                        return image;
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    recipe_avatar.setImageBitmap(bitmap);
                }
            }.execute();
        }
    }

    public class IngredientCustomAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return ing.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }


        //FIXME fix this shit
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.ingedient_item , null);

            EditText ingredient_name = (EditText) convertView.findViewById(R.id.ing_name);
            EditText ingredient_count = (EditText) convertView.findViewById(R.id.ing_count);

            ingredient_name.setText(String.valueOf(position) + " position");

            Spinner unit_spinner = (Spinner) convertView.findViewById(R.id.unit_spinner);

            ArrayAdapter<String> unit_adapter = new ArrayAdapter<String>(AddRecipeActivity.this ,android.R.layout.simple_spinner_item, UNITS); //selected item will look like a spinner set from XML

            unit_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            unit_spinner.setAdapter(unit_adapter);
            // заголовок
            unit_spinner.setPrompt("Ingredient");
            // выделяем элемент
            unit_spinner.setSelection(0);
            // устанавливаем обработчик нажатия
            unit_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view,
                                           int position, long id) {
                    Toast.makeText(getBaseContext(), "Unit: " + position, Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });

            return convertView;
        }
    }
}

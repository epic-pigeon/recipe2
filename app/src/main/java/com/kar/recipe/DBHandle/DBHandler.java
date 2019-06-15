package com.kar.recipe.DBHandle;
/*
 *   -----------------------------------------------------------------------------------
 *    Copyright 2019 Ostaplyuk Nikita
 *   -----------------------------------------------------------------------------------
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 *    -----------------------------------------------------------------------------------
 *
 *    This Source Code Form is subject to the terms of the Mozilla Public
 *    License, v. 2.0. If a copy of the MPL was not distributed with this
 *    file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 *    -----------------------------------------------------------------------------------
 */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.kar.recipe.DataClasses.Connection;
import com.kar.recipe.DataClasses.Data;
import com.kar.recipe.DataClasses.Ingredient;
import com.kar.recipe.DataClasses.Recipe;
import com.kar.recipe.DataClasses.RecipeIngredient;
import com.kar.recipe.DataClasses.User;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;
import java.util.function.Function;

public final class DBHandler {
    private DBHandler() {}

    /**
     * Executes a query using POST method
     *
     * @param operation Operation name
     * @param argNames  Argument keys
     * @param argValues Argument values
     * @return Execution result
     * @throws IOException If connection to server failed
     */
    public static Object executePostQuery(String operation, Collection<String> argNames, Collection<String> argValues) throws IOException {
        String urlParameters = Constants.SERVER.buildURLParameters(
                new Collection<>("operation").merge(argNames),
                new Collection<>(operation).merge(argValues)
        );
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;
        URL url = Constants.SERVER.getAPIURL();
        HttpURLConnection conn = (HttpURLConnection) Objects.requireNonNull(url).openConnection();
        conn.setDoOutput(true);
        conn.setInstanceFollowRedirects(false);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("charset", "utf-8");
        conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
        conn.setUseCaches(false);
        try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
            wr.write(postData);
        }

        int responseCode = conn.getResponseCode();

        if (responseCode != 200) throw new IOException("Bad response code: " + responseCode);
        BufferedReader input = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = input.readLine()) != null) {
            response.append(inputLine);
        }
        input.close();

        String json = String.valueOf(response);

        JSONObject parsed;

        try {
            parsed = (JSONObject) new JSONParser().parse(json);
        } catch (ParseException e) {
            throw new IOException("Failed to parse JSON, got:\n" + json);
        }

        Object info = parsed.get("info");
        if (info != null) System.out.println(info.toString());

        if ("true".equals(parsed.get("success"))) {
            Object lastID = parsed.get("last_id");
            if (lastID == null) return parsed.get("result");
            else return (int) (long) lastID;
        } else {
            throw new IOException((String) parsed.get("error"));
        }
    }

    private static Data data;

    @RequiresApi(api = Build.VERSION_CODES.N)
    private static <T> Collection<T> queueTableFromDump(String table, Function<JSONObject, T> fn) {
        Collection<T> collection = new Collection<>();
        JSONArray result = (JSONArray) dump.get(table);
        for (Object o : result) collection.add(fn.apply((JSONObject) o));
        return collection;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static Data getData() throws IOException {
        if (data == null) updateData();
        return data;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void updateData() throws IOException {
        data = fetchData();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void updateDataIfNotNull() throws IOException {
        if (data != null) updateData();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private static Data fetchData() throws IOException {
        dump = (JSONObject) executePostQuery("getAll", new Collection<>(), new Collection<>());

        Collection<User> users = queueTableFromDump(Constants.USERS.TABLE_NAME, result -> new User(
                Integer.valueOf((String) result.get(Constants.USERS.ID)),
                (String) result.get(Constants.USERS.USERNAME),
                (String) result.get(Constants.USERS.PASSWORD),
                (String) result.get(Constants.USERS.AVATAR)
        ));

        Collection<Recipe> recipes = queueTableFromDump(Constants.RECIPES.TABLE_NAME, result -> new Recipe(
                Integer.valueOf((String) result.get(Constants.RECIPES.ID)),
                (String) result.get(Constants.RECIPES.NAME),
                (String) result.get(Constants.RECIPES.COOKING),
                (String) result.get(Constants.RECIPES.PHOTO)
        ));

        Collection<Ingredient> ingredients = queueTableFromDump(Constants.INGREDIENTS.TABLE_NAME, result -> new Ingredient(
                Integer.valueOf((String) result.get(Constants.INGREDIENTS.ID)),
                (String) result.get(Constants.INGREDIENTS.NAME),
                (String) result.get(Constants.INGREDIENTS.UNITS)
        ));

        Collection<Connection> userSaves = queueTableFromDump(Constants.SAVES.TABLE_NAME, result -> new Connection(
                Integer.valueOf((String) result.get(Constants.SAVES.USER_ID)),
                Integer.valueOf((String) result.get(Constants.SAVES.RECIPE_ID))
        ));

        Collection<RecipeIngredient> recipeIngredients = queueTableFromDump(Constants.RECIPE_INGREDIENTS.TABLE_NAME, result -> new RecipeIngredient(
                Integer.valueOf((String) result.get(Constants.RECIPE_INGREDIENTS.ID)),
                Integer.valueOf((String) result.get(Constants.RECIPE_INGREDIENTS.RECIPE_ID)),
                Integer.valueOf((String) result.get(Constants.RECIPE_INGREDIENTS.INGREDIENT_ID)),
                Double.valueOf((String) result.get(Constants.RECIPE_INGREDIENTS.AMOUNT))
        ));

        return new Data(
                users,
                recipes,
                ingredients,
                userSaves,
                recipeIngredients
        );
    }

    private static JSONObject dump;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void createUser(String username, String password) throws IOException {
        executePostQuery(
                "createUser",
                new Collection<>("username", "password"),
                new Collection<>(username, password)
        );
        updateDataIfNotNull();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void changeUserAvatar(int id, Bitmap avatar, String extension) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        avatar.compress(
                extension.equalsIgnoreCase("jpeg") || extension.equalsIgnoreCase("jpg") ? Bitmap.CompressFormat.JPEG :
                        extension.equalsIgnoreCase("png") ? Bitmap.CompressFormat.PNG :
                                extension.equalsIgnoreCase("webp") ? Bitmap.CompressFormat.WEBP : null,
                100, outputStream);
        String image = new String(android.util.Base64.encode(outputStream.toByteArray(), android.util.Base64.DEFAULT));
        executePostQuery(
                "changeUser",
                new Collection<>(
                        "id",
                        "avatar",
                        "extension"
                ),
                new Collection<>(
                        String.valueOf(id),
                        image,
                        extension
                )
        );
        updateDataIfNotNull();
    }

    public static Bitmap loadImageViaUrl(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.connect();
        InputStream input = connection.getInputStream();
        return BitmapFactory.decodeStream(input);
    }

    /**
     * Fetches computer's MAC address
     *
     * @return MAC address
     */
    public static String getMACAddress() {
        InetAddress ip;
        try {
            ip = InetAddress.getLocalHost();
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            byte[] mac = network.getHardwareAddress();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mac.length; i++) {
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
            }
            return sb.toString();
        } catch (UnknownHostException | SocketException e) {
            e.printStackTrace();
            return null;
        }
    }
}
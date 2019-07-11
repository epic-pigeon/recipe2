package com.kar.recipe.DBHandle;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;

public class Constants {
    public static final class INGREDIENTS {
        public static final String TABLE_NAME = "ingredients";
        public static final String ID = "ingredient_id";
        public static final String NAME = "name";
    }

    public static final class UNITS {
        public static final String TABLE_NAME = "units";
        public static final String ID = "id";
        public static final String NAME = "unit";
    }

    public static final class RECIPES {
        public static final String TABLE_NAME = "recipes";
        public static final String ID = "recipe_id";
        public static final String NAME = "name";
        public static final String COOKING = "cooking";
        public static final String PHOTO = "photo";
    }

    public static final class RECIPE_INGREDIENTS {
        public static final String TABLE_NAME = "recipe_ingredients";
        public static final String ID = "id";
        public static final String RECIPE_ID = "recipe_id";
        public static final String INGREDIENT_ID = "ingredient_id";
        public static final String UNIT_ID = "unit_id";
        public static final String AMOUNT = "amount";
    }

    public static final class USERS {
        public static final String TABLE_NAME = "users";
        public static final String ID = "user_id";
        public static final String USERNAME = "name";
        public static final String PASSWORD = "password";
        public static final String AVATAR = "avatar";
    }

    public static final class SAVES {
        public static final String TABLE_NAME = "user_saves";
        public static final String ID = "id";
        public static final String USER_ID = "user_id";
        public static final String RECIPE_ID = "recipe_id";
    }

    public static final class SERVER {
        public static final String PROTOCOL = "http";
        public static final String HOST = "3.89.196.174";
        public static final String PORT = "80";
        public static final String RECIPE_IMAGE_PATH = "/recipe/img/recipes/";
        public static final String USER_IMAGE_PATH = "/recipe/img/users/";
        public static final String API_PATH = "/recipe/api.php";

        public static URL getUserAvatarURL(String avatar) {
            try {
                return new URL(PROTOCOL + "://" + HOST + ":" + PORT + USER_IMAGE_PATH + avatar);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }
        }
        public static URL getRecipePhotoURL(String photo) {
            try {
                return new URL(PROTOCOL + "://" + HOST + ":" + PORT + RECIPE_IMAGE_PATH + photo);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }
        }
        public static URL getAPIURL() {
            try {
                return new URL(PROTOCOL + "://" + HOST + ":" + PORT + API_PATH);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }
        }
        public static String buildURLParameters(Collection<String> names, Collection<String> values) {
            return buildURLParameters(names, values, true);
        }
        public static String buildURLParameters(Collection<String> names, Collection<String> values, boolean encode) {
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < names.size(); i++) {
                try {
                    result.append(i == 0 ? "" : "&")
                            .append(names.get(i))
                            .append("=")
                            .append(encode ? URLEncoder.encode(values.get(i), String.valueOf(Charset.defaultCharset())) : values.get(i));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            return result.toString();
        }
    }
}

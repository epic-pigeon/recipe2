package com.kar.recipe.DataClasses;

import android.graphics.Bitmap;

import com.kar.recipe.DBHandle.Collection;
import com.kar.recipe.DBHandle.Constants;
import com.kar.recipe.DBHandle.DBHandler;

import java.io.IOException;

public class User extends DataClass {
    private int id;
    private String name;
    private String password;
    private String avatar;
    private transient Collection<Recipe> saves;
    private transient Bitmap avatarImage;

    public User(int id, String name, String password, String avatar) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.avatar = avatar;
    }

    void setSaves(Collection<Recipe> saves) {
        this.saves = saves;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getAvatarExtension() {
        return avatar.split("\\.")[1];
    }

    public Collection<Recipe> getSaves() {
        return saves;
    }

    public Bitmap getAvatarImage() throws IOException {
        if (avatarImage == null) updateAvatarImage();
        return avatarImage;
    }

    public void updateAvatarImage() throws IOException {
        avatarImage = loadAvatarImage(avatar);
    }

    private static Bitmap loadAvatarImage(String avatar) throws IOException {
        return DBHandler.loadImageViaUrl(Constants.SERVER.getUserAvatarURL(avatar));
    }
}

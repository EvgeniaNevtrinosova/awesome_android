package ru.mail.park.awesome_android;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;


public class Recipe implements Serializable {
    @Expose()
    @SerializedName("recipe_name")
    private String name = "";

    @Expose()
    @SerializedName("recipe_products")
    private ArrayList<String> products = new ArrayList<>();

    @Expose()
    @SerializedName("recipe_text")
    private String text = "";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @SuppressWarnings("WeakerAccess")
    public ArrayList<String> getProducts() {
        return products;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}

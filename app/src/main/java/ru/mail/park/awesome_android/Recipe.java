package ru.mail.park.awesome_android;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;



public class Recipe implements Serializable {
    private final String serialized_recipe_name = "recipe_name";
    private final String serializes_recipe_products = "recipe_products";
    private final String serialized_recipe_text = "recipe_text";

    @Expose()
    @SerializedName(serialized_recipe_name)
    private String name = "";

    @Expose()
    @SerializedName(serializes_recipe_products)
    private ArrayList<String> products = new ArrayList<>();

    @Expose()
    @SerializedName(serialized_recipe_text)
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

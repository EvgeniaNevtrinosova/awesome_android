package ru.mail.park.awesome_android;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.HashMap;
import java.util.Map;

public class Recipe {
    @Expose()
    @SerializedName("name")
    private String name = "";

    @Expose()
    @SerializedName("products")
    private Map<String, String> products = new HashMap<String, String>();

    @Expose()
    @SerializedName("text")
    private String text = "";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getProducts() {
        return products;
    }

    public void setProducts(Map<String, String> products) {
        this.products = products;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

package ru.mail.park.awesome_android;

import android.content.Loader;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Recipe implements Serializable{
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

//    public Recipe(Parcel in){
//        String[] data = new String[3];
//        in.readStringArray(data);
//
//        this.name = data[0];
//
//    }
}

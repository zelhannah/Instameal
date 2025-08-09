package com.example.instameal.models;

import com.google.gson.annotations.SerializedName;

public class Ingredient {

    @SerializedName("name")
    private String name;

    @SerializedName("amount")
    private float amount;

    @SerializedName("unit")
    private String unit;

    public String getName() {
        return name;
    }

    public float getAmount() {
        return amount;
    }

    public String getUnit() {
        return unit;
    }
}

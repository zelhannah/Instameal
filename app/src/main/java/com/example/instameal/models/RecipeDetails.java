package com.example.instameal.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RecipeDetails {

    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("image")
    private String imageUrl;

    @SerializedName("summary")
    private String summary;

    @SerializedName("extendedIngredients")
    private List<Ingredient> ingredients;

    @SerializedName("instructions")
    private String instructions;

    // Getters
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getSummary() {
        return summary;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public String getInstructions() {
        return instructions;
    }
}

package com.example.instameal.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Recipe {
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

    public Recipe(int id, String title, String summary, String imageUrl) {
        this.id = id;
        this.title = title;
        this.summary = summary;
        this.imageUrl = imageUrl;
    }

    public int getId(){
        return id;
    }

    public String getTitle(){
        return title;
    }

    public String getImageUrl(){
        return imageUrl;
    }

    public String getSummary(){
        return summary;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public String getInstructions() {
        return instructions;
    }
}

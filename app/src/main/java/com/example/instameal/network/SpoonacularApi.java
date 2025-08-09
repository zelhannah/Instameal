package com.example.instameal.network;

import com.example.instameal.models.RecipeResponse;
import com.example.instameal.models.RecipeDetails;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SpoonacularApi {

    // Fetch random recipes
    @GET("recipes/random")
    Call<RecipeResponse> getRandomRecipes(
            @Query("apiKey") String apiKey,
            @Query("number") int number
    );

    // Fetch recipe details by ID
    @GET("recipes/{id}/information")
    Call<RecipeDetails> getRecipeDetails(
            @Path("id") int id,
            @Query("apiKey") String apiKey
    );
}

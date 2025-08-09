package com.example.instameal;

import android.database.Cursor;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.instameal.network.RetrofitClient;
import com.example.instameal.models.RecipeDetails;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecipeAdapter recipeAdapter;
    private List<RecipeDetails> recipeList = new ArrayList<>();
    private DBHelper dbHelper;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DBHelper(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        // Set up the RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view_history);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Load recipe IDs from the database and fetch their details
        loadSavedRecipeIds();
        return view;
    }

    // Load saved recipe IDs from the database
    private void loadSavedRecipeIds() {
        List<Integer> savedRecipeIds = new ArrayList<>();
        String query = "SELECT " + DBHelper.COLUMN_RECIPE_ID + " FROM " + DBHelper.TABLE_RECIPES;
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int recipeIdIndex = cursor.getColumnIndex(DBHelper.COLUMN_RECIPE_ID);
                if (recipeIdIndex != -1) {
                    int recipeId = cursor.getInt(recipeIdIndex);
                    savedRecipeIds.add(recipeId);
                }
            } while (cursor.moveToNext());
        }

        cursor.close();

        if (!savedRecipeIds.isEmpty()) {
            loadRecipes(savedRecipeIds);
        } else {
            Toast.makeText(getActivity(), "No saved recipes found", Toast.LENGTH_SHORT).show();
        }
    }

    // Load recipes from API based on the saved recipe IDs
    private void loadRecipes(List<Integer> savedRecipeIds) {
        for (int recipeId : savedRecipeIds) {
            String apiKey = "9458d03c348e4fd8af0e944d8ab55977";

            Call<RecipeDetails> call = RetrofitClient.getInstance().getSpoonacularApi().getRecipeDetails(recipeId, apiKey);
            call.enqueue(new Callback<RecipeDetails>() {
                @Override
                public void onResponse(Call<RecipeDetails> call, Response<RecipeDetails> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        RecipeDetails recipe = response.body(); // Use RecipeDetails here
                        recipeList.add(recipe);

                        // Notify the adapter when all recipes are fetched
                        if (recipeList.size() == savedRecipeIds.size()) {
                            setupAdapter(recipeList);
                        }
                    }
                }

                @Override
                public void onFailure(Call<RecipeDetails> call, Throwable t) {
                    Toast.makeText(getActivity(), "Failed to load recipes", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // Setup the adapter for the RecyclerView
    private void setupAdapter(List<RecipeDetails> recipeDetails) {
        HistoryRecipeAdapter historyRecipeAdapter = new HistoryRecipeAdapter(getActivity(), recipeDetails, recipeId -> {
            // Create a new fragment instance
            RecipeDetailsFragment recipeDetailsFragment = new RecipeDetailsFragment();

            // Pass the recipe ID as an argument
            Bundle bundle = new Bundle();
            bundle.putInt("RECIPE_ID", recipeId);
            recipeDetailsFragment.setArguments(bundle);

            // Use FragmentManager to replace the current fragment with the RecipeDetailsFragment
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frameLayout, recipeDetailsFragment)
                    .addToBackStack(null)
                    .commit();
        });

        recyclerView.setAdapter(historyRecipeAdapter);
    }
}

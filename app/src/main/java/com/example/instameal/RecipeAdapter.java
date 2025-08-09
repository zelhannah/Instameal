package com.example.instameal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.instameal.models.Recipe;
import java.util.List;
import android.text.Html;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    private List<Recipe> recipeList;
    private OnRecipeClickListener onRecipeClickListener;
    private DBHelper dbHelper;
    private Context context;

    // Interface to handle recipe clicks
    public interface OnRecipeClickListener {
        void onRecipeClick(int recipeId);
    }

    // Constructor with click listener and DBHelper
    public RecipeAdapter(Context context, List<Recipe> recipeList, DBHelper dbHelper, OnRecipeClickListener listener) {
        this.recipeList = recipeList;
        this.onRecipeClickListener = listener;
        this.context = context;
        this.dbHelper = dbHelper;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_item, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);

        // Bind the recipe data to the view
        holder.titleTextView.setText(recipe.getTitle());
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            holder.descriptionTextView.setText(Html.fromHtml(recipe.getSummary(), Html.FROM_HTML_MODE_LEGACY));
        } else {
            holder.descriptionTextView.setText(recipe.getSummary());
        }

        // Use Glide to load the image from the URL
        Glide.with(holder.itemView.getContext())
                .load(recipe.getImageUrl())
                .into(holder.recipeImageView);

        // Handle item click and pass recipe ID
        holder.itemView.setOnClickListener(v -> {
            saveRecipeToDatabase(recipe); // Save the recipe to the database
            onRecipeClickListener.onRecipeClick(recipe.getId());
        });
    }

    // Method to save the recipe to the database
    private void saveRecipeToDatabase(Recipe recipe) {
        dbHelper.addRecipe(recipe);
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder {

        ImageView recipeImageView;
        TextView titleTextView;
        TextView descriptionTextView;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeImageView = itemView.findViewById(R.id.recipe_image);
            titleTextView = itemView.findViewById(R.id.recipe_name);
            descriptionTextView = itemView.findViewById(R.id.recipe_description);
        }
    }
}

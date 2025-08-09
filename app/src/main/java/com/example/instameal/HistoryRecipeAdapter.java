package com.example.instameal;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.instameal.models.RecipeDetails;

import java.util.List;

public class HistoryRecipeAdapter extends RecyclerView.Adapter<HistoryRecipeAdapter.RecipeDetailsViewHolder> {

    private List<RecipeDetails> recipeDetailsList;
    private OnRecipeClickListener onRecipeClickListener;
    private Context context;

    // Interface to handle recipe clicks
    public interface OnRecipeClickListener {
        void onRecipeClick(int recipeId);
    }

    // Constructor with click listener
    public HistoryRecipeAdapter(Context context, List<RecipeDetails> recipeDetailsList, OnRecipeClickListener listener) {
        this.recipeDetailsList = recipeDetailsList;
        this.onRecipeClickListener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public RecipeDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_item, parent, false);
        return new RecipeDetailsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeDetailsViewHolder holder, int position) {
        RecipeDetails recipeDetails = recipeDetailsList.get(position);

        // Bind the recipe data to the view
        holder.titleTextView.setText(recipeDetails.getTitle());

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            holder.descriptionTextView.setText(Html.fromHtml(recipeDetails.getSummary(), Html.FROM_HTML_MODE_LEGACY));
        } else {
            holder.descriptionTextView.setText(recipeDetails.getSummary());
        }

        // Use Glide to load the image from the URL
        Glide.with(holder.itemView.getContext())
                .load(recipeDetails.getImageUrl())
                .into(holder.recipeImageView);

        // Handle item click and pass recipe ID
        holder.itemView.setOnClickListener(v -> onRecipeClickListener.onRecipeClick(recipeDetails.getId()));
    }

    @Override
    public int getItemCount() {
        return recipeDetailsList.size();
    }

    public static class RecipeDetailsViewHolder extends RecyclerView.ViewHolder {
        ImageView recipeImageView;
        TextView titleTextView;
        TextView descriptionTextView;

        public RecipeDetailsViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeImageView = itemView.findViewById(R.id.recipe_image);
            titleTextView = itemView.findViewById(R.id.recipe_name);
            descriptionTextView = itemView.findViewById(R.id.recipe_description);
        }
    }
}

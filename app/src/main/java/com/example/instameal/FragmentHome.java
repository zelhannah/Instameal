package com.example.instameal;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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
import com.example.instameal.models.Recipe;
import com.example.instameal.models.RecipeResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class FragmentHome extends Fragment implements SensorEventListener {
    private RecyclerView recyclerView;
    private RecipeAdapter recipeAdapter;
    private List<Recipe> recipeList = new ArrayList<>();
    private DBHelper dbHelper;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private float accel; // current acceleration including gravity
    private float accelCurrent; // current acceleration
    private float accelLast; // last acceleration

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Set up the DBHelper
        dbHelper = new DBHelper(getActivity());

        // Set up the RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Set up the SensorManager for detecting shake
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        accel = 10f;
        accelCurrent = SensorManager.GRAVITY_EARTH;
        accelLast = SensorManager.GRAVITY_EARTH;
        loadRecipes();
        return view;
    }

    private void loadRecipes() {
        String apiKey = "9458d03c348e4fd8af0e944d8ab55977";
        Call<RecipeResponse> call = RetrofitClient.getInstance().getSpoonacularApi().getRandomRecipes(apiKey, 2);
        call.enqueue(new Callback<RecipeResponse>() {
            @Override
            public void onResponse(Call<RecipeResponse> call, Response<RecipeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Recipe> recipes = response.body().getRecipes();
                    // Set up the adapter with the list of recipes and a click listener
                    recipeAdapter = new RecipeAdapter(getActivity(), recipes, dbHelper, recipeId -> {
                        // Create a new bundle to pass the recipe ID
                        Bundle bundle = new Bundle();
                        bundle.putInt("RECIPE_ID", recipeId);
                        // Create and set up the RecipeDetailsFragment with the bundle
                        RecipeDetailsFragment detailsFragment = new RecipeDetailsFragment();
                        detailsFragment.setArguments(bundle);
                        // Navigate to the RecipeDetailsFragment
                        getParentFragmentManager().beginTransaction()
                                .replace(R.id.frameLayout, detailsFragment)
                                .addToBackStack(null)
                                .commit();
                    });
                    recyclerView.setAdapter(recipeAdapter);
                }
            }
            @Override
            public void onFailure(Call<RecipeResponse> call, Throwable t) {
                Toast.makeText(getActivity(), "Failed to load recipes", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        accelLast = accelCurrent;
        accelCurrent = (float) Math.sqrt((x * x + y * y + z * z));
        float delta = accelCurrent - accelLast;
        accel = accel * 0.9f + delta; // perform low-cut filter
        if (accel > 12) {
            // Shake detected, refresh the recipes
            Toast.makeText(getActivity(), "Boom! Shake success! Fetching new menus for you🍳", Toast.LENGTH_SHORT).show();
            loadRecipes();  // Call the method to load new recipes
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener((SensorEventListener) this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }
    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}


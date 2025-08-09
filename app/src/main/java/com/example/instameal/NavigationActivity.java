package com.example.instameal;


import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.instameal.databinding.ActivityNavigationBinding;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NavigationActivity extends AppCompatActivity {


    private ActivityNavigationBinding binding;
    private static final int CAMERA_PERMISSION_CODE = 101;
    private static final int REQUEST_CODE_CAMERA = 99;
    private Switch darkModeSwitch;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNavigationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // Initialize SharedPreferences for dark mode
        sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        editor = sharedPreferences.edit();


        // Set up the dark mode switch
        darkModeSwitch = findViewById(R.id.switch_dark_mode);
        boolean isDarkMode = sharedPreferences.getBoolean("dark_mode", false);
        darkModeSwitch.setChecked(isDarkMode);


        // Apply current theme based on the stored preference
        applyDarkMode(isDarkMode);


        // Listen for switch changes to toggle dark mode
        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            editor.putBoolean("dark_mode", isChecked);
            editor.apply();
            applyDarkMode(isChecked);
        });

        if (savedInstanceState == null) {
            replaceFragment(new FragmentHome());
        }

        binding.bottomNavigationView.setSelectedItemId(R.id.home);

        // Handle bottom navigation item selection
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    replaceFragment(new FragmentHome());
                    break;
                case R.id.scan:
                    checkPermissions();
                    break;
                case R.id.history:
                    replaceFragment(new HistoryFragment());
                    break;
            }
            return true;
        });
    }

    // Apply dark mode without recreating the entire activity
    private void applyDarkMode(boolean isDarkMode) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    // Replace the fragment in the container
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }


    // Check both camera and storage permissions
    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    CAMERA_PERMISSION_CODE);
        } else {
            openCamera();
        }
    }


    // Open the camera
    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, REQUEST_CODE_CAMERA);
    }


    // Prompt the user to rename the image and specify the folder
    private void promptUserToRenameAndSaveImage(Bitmap imageBitmap) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_rename_image, null);
        builder.setView(dialogView);


        EditText inputName = dialogView.findViewById(R.id.editImageName);
        EditText inputDirectory = dialogView.findViewById(R.id.editDirectory);


        builder.setTitle("Save Image")
                .setMessage("Rename the image and choose folder")
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String customName = inputName.getText().toString();
                        String folderName = inputDirectory.getText().toString();

                        if (customName.isEmpty()) {
                            customName = "IMG_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                        }

                        saveImageToGallery(imageBitmap, customName, folderName);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder.create().show();
    }

    // Save image to gallery using MediaStore with custom folder option
    private void saveImageToGallery(Bitmap imageBitmap, String customName, String folderName) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, customName + ".jpg");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

        if (!folderName.isEmpty()) {
            values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/" + folderName);
        } else {
            values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
        }

        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        try {
            if (uri != null) {
                OutputStream outputStream = getContentResolver().openOutputStream(uri);
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                if (outputStream != null) {
                    outputStream.flush();
                    outputStream.close();
                }
                Toast.makeText(this, "Image saved to " + (folderName.isEmpty() ? "Pictures" : folderName) + " folder", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
        }
    }
6
    // Handling the result from the camera capture
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CAMERA && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap capturedImage = (Bitmap) extras.get("data");
                promptUserToRenameAndSaveImage(capturedImage);
            }
        }

        // Reset the selected item to home after returning from the camera
        binding.bottomNavigationView.setSelectedItemId(R.id.home);
    }

    // Handle the result of the camera permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera and storage permissions are required", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

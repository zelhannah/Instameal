package com.example.instameal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.instameal.models.Recipe;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "instameal.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_RECIPES = "recipes";
    public static final String COLUMN_RECIPE_ID = "recipe_id";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_RECIPES_TABLE = "CREATE TABLE " + TABLE_RECIPES + " ("
                + COLUMN_RECIPE_ID + " INTEGER PRIMARY KEY"
                + ")";
        db.execSQL(CREATE_RECIPES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        onCreate(db);
    }

    // Method to add a recipe ID
    public boolean addRecipe(Recipe recipeId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_RECIPE_ID, recipeId.getId());
        long result = db.insert(TABLE_RECIPES, null, values);
        db.close();
        return result != -1; // Returns true if insert is successful
    }

    // Method to check if a recipe ID exists
    public boolean isRecipeExists(int recipeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_RECIPES, new String[]{COLUMN_RECIPE_ID}, COLUMN_RECIPE_ID + "=?", new String[]{String.valueOf(recipeId)}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Method to get all recipe IDs
    public Cursor getAllRecipeIds() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT " + COLUMN_RECIPE_ID + " FROM " + TABLE_RECIPES, null);
    }
}

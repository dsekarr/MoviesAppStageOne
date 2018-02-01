package com.example.dsekar.moviesstageone.utilities;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.widget.Toast;

import com.example.dsekar.moviesstageone.Data.Movie;
import com.example.dsekar.moviesstageone.Data.Review;
import com.example.dsekar.moviesstageone.Data.Video;
import com.example.dsekar.moviesstageone.Db.MovieContract;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MovieUtils {

    /**
     * Get the list of movies from JSON Response.
     *
     * @param jsonResponse
     * @return
     */
    public static List<Movie> getMoviesListFromJsonResponse(String jsonResponse) {
        Gson gson = new Gson();
        JsonElement jsonElement = new Gson().fromJson(jsonResponse, JsonElement.class);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        JsonArray jsonArray = jsonObject.getAsJsonArray("results");
        Type listType = new TypeToken<List<Movie>>() {
        }.getType();
        return gson.fromJson(jsonArray.toString(), listType);
    }

    public static List<Video> getVideosFromJsonResponse(String jsonResponse) {
        Gson gson = new Gson();
        JsonElement jsonElement = new Gson().fromJson(jsonResponse, JsonElement.class);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        JsonArray jsonArray = jsonObject.getAsJsonArray("results");
        Type listType = new TypeToken<List<Video>>() {
        }.getType();
        return gson.fromJson(jsonArray.toString(), listType);
    }

    public static List<Review> getReviewsFromJsonResponse(String jsonResponse) {
        Gson gson = new Gson();
        JsonElement jsonElement = new Gson().fromJson(jsonResponse, JsonElement.class);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        JsonArray jsonArray = jsonObject.getAsJsonArray("results");
        Type listType = new TypeToken<List<Review>>() {
        }.getType();
        return gson.fromJson(jsonArray.toString(), listType);
    }

    /**
     * get span count for grid layout.
     *
     * @param context
     * @return
     */
    public static int getSpanCount(Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        float dpWidth = context.getResources().getDisplayMetrics().widthPixels / density;
        return Math.round(dpWidth / 200);
    }

    public static void markFavorite(Movie movie, Context context) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.MoviesEntry.MOVIE_ID, movie.getId());
        contentValues.put(MovieContract.MoviesEntry.MOVIE_TITLE, movie.getTitle());
        contentValues.put(MovieContract.MoviesEntry.MOVIE_ORIGINAL_TITLE, movie.getOriginalLanguage());
        contentValues.put(MovieContract.MoviesEntry.MOVIE_POSTER_PATH, movie.getPosterPath());
        contentValues.put(MovieContract.MoviesEntry.MOVIE_BACKDROP_PATH, movie.getBackdropPath());
        contentValues.put(MovieContract.MoviesEntry.MOVIE_LANGUAGE, movie.getPopularity());
        contentValues.put(MovieContract.MoviesEntry.MOVIE_ADULT, movie.getAdult());
        contentValues.put(MovieContract.MoviesEntry.MOVIE_OVERVIEW, movie.getOverview());
        contentValues.put(MovieContract.MoviesEntry.MOVIE_RELEASE_DATE, movie.getReleaseDate());
        contentValues.put(MovieContract.MoviesEntry.MOVIE_VOTE_AVERAGE, movie.getVoteAverage());
        contentValues.put(MovieContract.MoviesEntry.MOVIE_VOTE_COUNT, movie.getVoteCount());
        contentValues.put(MovieContract.MoviesEntry.MOVIE_POPULARITY, movie.getPopularity());
        context.getContentResolver().insert(MovieContract.MoviesEntry.CONTENT_URI, contentValues);
    }

    public static void removeFavorite(int movieId, Context context) {
        context.getContentResolver().delete(MovieContract.MoviesEntry.CONTENT_URI, MovieContract.MoviesEntry.MOVIE_ID + " = " + movieId, null);
    }

    public static Boolean isFavorite(Movie movie, Context context) {
        Uri uri = ContentUris.withAppendedId(MovieContract.MoviesEntry.CONTENT_URI, movie.getId());
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        return cursor != null && cursor.moveToFirst();
    }
}

package com.example.dsekar.moviesstageone.utilities;

import android.content.Context;

import com.example.dsekar.moviesstageone.Movie;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MovieUtils {
    /**
     * Get the year from the date string.
     *
     * @param dateString
     * @return
     * @throws ParseException
     */
    public static String getNormalizedYearFromDate(String dateString) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date dateObj = dateFormat.parse(dateString);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateObj);
        int year = calendar.get(Calendar.YEAR);
        return Integer.toString(year);
    }

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

    /**
     * get span count for grid layout.
     * @param context
     * @return
     */
    public static int getSpanCount(Context context){
        float density  = context.getResources().getDisplayMetrics().density;
        float dpWidth  =  context.getResources().getDisplayMetrics().widthPixels / density;
        return Math.round(dpWidth/200);
    }
}

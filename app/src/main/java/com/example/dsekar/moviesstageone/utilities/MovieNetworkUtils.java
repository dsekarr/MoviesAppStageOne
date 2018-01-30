package com.example.dsekar.moviesstageone.utilities;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import com.example.dsekar.moviesstageone.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import static android.content.ContentValues.TAG;

public class MovieNetworkUtils {

    private static final String BASE_URL = "http://api.themoviedb.org/3/movie";
    private static final String QUERY_PARAM = "api_key";
    public static final String TOP_RATED = "top_rated";
    public static final String MOST_POPULAR = "popular";
    public static final String MOVIE_URI = "http://image.tmdb.org/t/p/w500/";

    public static final String VIDEO_BASE_URL = "http://img.youtube.com/vi/";

    /**
     * Check the network connectivity to make a web call.
     *
     * @param context
     * @return
     */
    public static boolean checkNetworkStatus(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isConnected = false;
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();
        }
        return isConnected;
    }

    /**
     * Get the movie database URL to fetch the movies.
     *
     * @param param
     * @param context
     * @return
     */
    public static URL getNetworkURL(String param, Context context) {
        final String QUERY_API_KEY = getQueryApiKey(context);
        Uri uri = Uri.parse(BASE_URL).buildUpon().appendPath(param)
                .appendQueryParameter(QUERY_PARAM, QUERY_API_KEY).build();

        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "Built URI " + url);

        return url;
    }

    public static URL buildUrl(int movieId, String param, Context context) {
        final String QUERY_API_KEY = getQueryApiKey(context);
        Uri uri = Uri.parse(BASE_URL).buildUpon().appendPath(Integer.toString(movieId))
                .appendPath(param)
                .appendQueryParameter(QUERY_PARAM, QUERY_API_KEY).build();

        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "Built URI " + url);

        return url;
    }

    /**
     * Get the api key to build the URL from android manifest file.
     *
     * @param context
     * @return
     */
    private static String getQueryApiKey(Context context) {
        ApplicationInfo ai = null;
        try {
            ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        assert ai != null;
        return ai.metaData.getString(context.getResources().getString(R.string.api_key));
    }

    /**
     * Get JSON Response from the URL api.
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}

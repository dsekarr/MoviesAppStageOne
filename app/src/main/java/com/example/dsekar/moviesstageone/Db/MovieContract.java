package com.example.dsekar.moviesstageone.Db;

import android.net.Uri;
import android.provider.BaseColumns;

public class MovieContract {
    public static final String CONTENT_AUTHORITY = "com.example.dsekar.moviesstageone";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIES = "movies";

    public static final class MoviesEntry implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIES).build();


        public static final String TABLE_NAME = "movies";
        public static final String MOVIE_ID = "id";
        public static final String MOVIE_VOTE_AVERAGE = "vote_average";
        public static final String MOVIE_TITLE = "title";
        public static final String MOVIE_POPULARITY = "popularity";
        public static final String MOVIE_POSTER_PATH = "poster_path";
        public static final String MOVIE_LANGUAGE = "original_language";
        public static final String MOVIE_ORIGINAL_TITLE = "original_title";
        public static final String MOVIE_BACKDROP_PATH = "backdrop_path";
        public static final String MOVIE_ADULT = "adult";
        public static final String MOVIE_OVERVIEW = "overview";
        public static final String MOVIE_RELEASE_DATE = "release_date";
        public static final String MOVIE_VOTE_COUNT = "vote_count";
    }
}

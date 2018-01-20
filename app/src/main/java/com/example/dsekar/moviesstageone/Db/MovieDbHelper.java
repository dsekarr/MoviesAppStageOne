package com.example.dsekar.moviesstageone.Db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MovieDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "popular_movies.db";

    private static final int DATABASE_VERSION = 1;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_TABLE =
                "CREATE TABLE " + MovieContract.MoviesEntry.TABLE_NAME + " (" +
                        MovieContract.MoviesEntry._ID                   + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        MovieContract.MoviesEntry.MOVIE_ID              + " INTEGER NOT NULL, " +
                        MovieContract.MoviesEntry.MOVIE_TITLE           + " TEXT NOT NULL, " +
                        MovieContract.MoviesEntry.MOVIE_ORIGINAL_TITLE  + " TEXT NOT NULL, " +
                        MovieContract.MoviesEntry.MOVIE_LANGUAGE        + " TEXT NOT NULL, " +
                        MovieContract.MoviesEntry.MOVIE_POSTER_PATH     + " TEXT NOT NULL, " +
                        MovieContract.MoviesEntry.MOVIE_BACKDROP_PATH   + " TEXT NOT NULL, " +
                        MovieContract.MoviesEntry.MOVIE_POPULARITY      + " INTEGER , "      +
                        MovieContract.MoviesEntry.MOVIE_ADULT           + " TEXT NOT NULL, " +
                        MovieContract.MoviesEntry.MOVIE_VOTE_COUNT      + " INTEGER , "      +
                        MovieContract.MoviesEntry.MOVIE_VOTE_AVERAGE    + " TEXT NOT NULL, " +
                        MovieContract.MoviesEntry.MOVIE_OVERVIEW        + " TEXT NOT NULL, " +
                        MovieContract.MoviesEntry.MOVIE_RELEASE_DATE    + " TEXT NOT NULL, " +

                        " UNIQUE (" + MovieContract.MoviesEntry.MOVIE_ID + ") ON CONFLICT REPLACE);";

        db.execSQL(SQL_CREATE_MOVIE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MoviesEntry.TABLE_NAME);
        onCreate(db);
    }
}

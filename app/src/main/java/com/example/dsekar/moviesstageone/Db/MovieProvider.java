package com.example.dsekar.moviesstageone.Db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class MovieProvider extends ContentProvider {

    private MovieDbHelper mDbHelper;
    private static final int CODE_MOVIE = 75;
    private static final int CODE_MOVIE_ID = 76;

    private static final UriMatcher uriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIES, CODE_MOVIE);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIES + "/#", CODE_MOVIE_ID);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;
        switch (uriMatcher.match(uri)) {
            case CODE_MOVIE_ID:
                String id = uri.getLastPathSegment();
                String[] selectionArguments = new String[]{id};
                cursor = mDbHelper.getReadableDatabase().query(MovieContract.MoviesEntry.TABLE_NAME,
                        projection,
                        MovieContract.MoviesEntry.MOVIE_ID + " = ? ",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);

                break;

            case CODE_MOVIE:
                cursor = mDbHelper.getReadableDatabase().query(MovieContract.MoviesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri" + uri);
        }
        if (getContext() != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Uri returnUri;
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowsInserted = 0;
        switch (uriMatcher.match(uri)) {
            case CODE_MOVIE:
                long id = db.insert(MovieContract.MoviesEntry.TABLE_NAME, null, values);
                returnUri = ContentUris.withAppendedId(MovieContract.MoviesEntry.CONTENT_URI, id);
                if (id != -1) {
                    rowsInserted++;
                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
        if (rowsInserted > 0 && getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return returnUri;

    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int numberOfRowsDeleted;
        if (selection == null) {
            selection = "1"; //passing 1 for selection will delete all the rows and return number of rows deleted
        }
        switch (uriMatcher.match(uri)) {
            case CODE_MOVIE:
                numberOfRowsDeleted = mDbHelper.getWritableDatabase().delete(MovieContract.MoviesEntry.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        if (numberOfRowsDeleted != 0 && getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numberOfRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int numberOfRowsUpdated;
        switch (uriMatcher.match(uri)) {
            case CODE_MOVIE:
                numberOfRowsUpdated = mDbHelper.getWritableDatabase().update(MovieContract.MoviesEntry.TABLE_NAME, values, selection, selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
        if (numberOfRowsUpdated > 0 && getContext() != null) {
            getContext().getContentResolver().update(uri, values, null, null);

            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numberOfRowsUpdated;
    }
}

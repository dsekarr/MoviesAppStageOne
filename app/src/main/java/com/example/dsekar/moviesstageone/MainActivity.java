package com.example.dsekar.moviesstageone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.dsekar.moviesstageone.Data.Movie;
import com.example.dsekar.moviesstageone.Db.MovieContract;
import com.example.dsekar.moviesstageone.utilities.MovieNetworkUtils;
import com.example.dsekar.moviesstageone.utilities.MovieUtils;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterClickHandler, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String EXTRA_MOVIE = "Movie_Intent";
    private static final String TAG = "Movies";

    @BindView(R.id.recycler_view_movies)
    RecyclerView mRecyclerView;

    private MovieAdapter mMovieAdapter;

    @BindView(R.id.pb_loading_indicator)
    ProgressBar mLoadingIndicator;

    @BindView(R.id.error_message)
    TextView mErrorMessage;

    @BindView(R.id.noFav_message)
    TextView mNoFavorites;

    private List<Movie> movieList = new ArrayList<>();
    private onNetworkChangeReceiver networkChangeReceiver;
    private IntentFilter intentFilter;
    private static final String CURRENT_MOVIES_DISPLAY = "current_movies";
    private static final int FAV_INIT_LOADER = 135;
    private boolean favorites = false;
    private String movies_option;
    private int mPosition = RecyclerView.NO_POSITION;

    public static final String[] MAIN_MOVIE_PROJECTION = {
            MovieContract.MoviesEntry.MOVIE_ID,
            MovieContract.MoviesEntry.MOVIE_TITLE,
            MovieContract.MoviesEntry.MOVIE_ORIGINAL_TITLE,
            MovieContract.MoviesEntry.MOVIE_POSTER_PATH,
            MovieContract.MoviesEntry.MOVIE_RELEASE_DATE,
            MovieContract.MoviesEntry.MOVIE_VOTE_AVERAGE,
            MovieContract.MoviesEntry.MOVIE_VOTE_COUNT,
            MovieContract.MoviesEntry.MOVIE_ADULT,
            MovieContract.MoviesEntry.MOVIE_OVERVIEW,
            MovieContract.MoviesEntry.MOVIE_BACKDROP_PATH,
            MovieContract.MoviesEntry.MOVIE_LANGUAGE,
            MovieContract.MoviesEntry.MOVIE_POPULARITY
    };

    private static final int INDEX_MOVIE_ID = 0;
    private static final int INDEX_MOVIE_TITLE = 1;
    private static final int INDEX_MOVIE_ORIGINAL_TITLE = 2;
    private static final int INDEX_MOVIE_POSTER_PATH = 3;
    private static final int INDEX_MOVIE_RELEASE_DATE = 4;
    private static final int INDEX_MOVIE_VOTE_AVERAGE = 5;
    private static final int INDEX_MOVIE_VOTE_COUNT = 6;
    private static final int INDEX_MOVIE_ADULT = 7;
    private static final int INDEX_MOVIE_OVERVIEW = 8;
    private static final int INDEX_MOVIE_BACKDROP_PATH = 9;
    private static final int INDEX_MOVIE_LANGUAGE = 10;
    private static final int INDEX_MOVIE_POPULARITY = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        GridLayoutManager layoutManager =
                new GridLayoutManager(MainActivity.this, MovieUtils.getSpanCount(this), GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mMovieAdapter = new MovieAdapter(MainActivity.this, this);
        mRecyclerView.setAdapter(mMovieAdapter);

        if (savedInstanceState == null) {
            executeNetworkCallTask(MovieNetworkUtils.MOST_POPULAR);
            movies_option = MovieNetworkUtils.MOST_POPULAR;
        } else {
            movieList = savedInstanceState.getParcelableArrayList(CURRENT_MOVIES_DISPLAY);
            if (movieList != null && movieList.size() > 0) {
                mMovieAdapter.setMovieData(movieList);
            } else {
                mRecyclerView.setVisibility(View.INVISIBLE);
                mErrorMessage.setVisibility(View.VISIBLE);
            }
        }
        networkChangeReceiver = new onNetworkChangeReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(networkChangeReceiver, intentFilter);
    }

    /**
     * Execute network Task.
     *
     * @param option
     */
    private void executeNetworkCallTask(String option) {
        if (MovieNetworkUtils.checkNetworkStatus(this)) {
            mLoadingIndicator.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);
            mErrorMessage.setVisibility(View.INVISIBLE);
            fetchMovieData(option);
        } else {
            if(movieList != null){
                movieList.clear();
            }
            mRecyclerView.setVisibility(View.INVISIBLE);
            mErrorMessage.setVisibility(View.VISIBLE);
            mLoadingIndicator.setVisibility(View.INVISIBLE);

        }
    }

    @Override
    public void onClick(Movie movie) {
        Intent startDetailsActivityIntent = new Intent(this, DetailActivity.class);
        startDetailsActivityIntent.putExtra(EXTRA_MOVIE, movie);
        startActivity(startDetailsActivityIntent);
    }

    /**
     * onCreateOptions Menu.
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sort_menu_item, menu);
        return true;
    }

    /**
     * onOptions Item Selected
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mNoFavorites.setVisibility(View.INVISIBLE);
        int id = item.getItemId();
        if (id == R.id.most_popular || id == R.id.action_refresh) {
            this.favorites = false;
            this.movies_option = MovieNetworkUtils.MOST_POPULAR;
            executeNetworkCallTask(MovieNetworkUtils.MOST_POPULAR);
        }
        if (id == R.id.high_rated) {
            this.favorites = false;
            this.movies_option = MovieNetworkUtils.TOP_RATED;
            executeNetworkCallTask(MovieNetworkUtils.TOP_RATED);
        }
        if (id == R.id.fav_button) {
            showFavorites();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * SavedStateInstance persist the data when screen rotates.
     *
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (movieList != null && movieList.size() > 0) {
            outState.putParcelableArrayList(CURRENT_MOVIES_DISPLAY, (ArrayList<? extends Parcelable>) movieList);
        } else {
            if (!MovieNetworkUtils.checkNetworkStatus(this)) {
                outState.putParcelableArrayList(CURRENT_MOVIES_DISPLAY, null);
            }
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        switch (id) {
            case FAV_INIT_LOADER:
                return new CursorLoader(this, MovieContract.MoviesEntry.CONTENT_URI, MAIN_MOVIE_PROJECTION,
                        null, null, null);

            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (!favorites) {
            return;
        }
        List<Movie> movies = new ArrayList<>();
        if (movieList != null) {
            movieList.clear();
        }
        if (data != null && data.moveToFirst()) {
            do {
                Movie cursorMovie = new Movie();

                int movieId = data.getInt(INDEX_MOVIE_ID);
                String movieTitle = data.getString(INDEX_MOVIE_TITLE);
                String movieOriginalTitle = data.getString(INDEX_MOVIE_ORIGINAL_TITLE);
                String moviePosterPath = data.getString(INDEX_MOVIE_POSTER_PATH);
                String moviReleaseDate = data.getString(INDEX_MOVIE_RELEASE_DATE);
                int movieVoteCount = data.getInt(INDEX_MOVIE_VOTE_COUNT);
                Double movieVoteAverage = data.getDouble(INDEX_MOVIE_VOTE_AVERAGE);
                Boolean movieIsAdult = data.getInt(INDEX_MOVIE_ADULT) > 0;
                String movieOverview = data.getString(INDEX_MOVIE_OVERVIEW);
                String movieBackdrop = data.getString(INDEX_MOVIE_BACKDROP_PATH);
                String language = data.getString(INDEX_MOVIE_LANGUAGE);
                Double popularity = data.getDouble(INDEX_MOVIE_POPULARITY);

                cursorMovie.setId(movieId);
                cursorMovie.setTitle(movieTitle);
                cursorMovie.setOriginalTitle(movieOriginalTitle);
                cursorMovie.setPosterPath(moviePosterPath);
                cursorMovie.setReleaseDate(moviReleaseDate);
                cursorMovie.setVoteCount(movieVoteCount);
                cursorMovie.setVoteAverage(movieVoteAverage);
                cursorMovie.setAdult(movieIsAdult);
                cursorMovie.setOverview(movieOverview);
                cursorMovie.setBackdropPath(movieBackdrop);
                cursorMovie.setOriginalLanguage(language);
                cursorMovie.setPopularity(popularity);

                movies.add(cursorMovie);
            } while (data.moveToNext());
        }

        if (movies.size() == 0) {
            mNoFavorites.setVisibility(View.VISIBLE);
        }
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mMovieAdapter.setMovieData(movies);
        movieList = movies;

        if (mPosition == RecyclerView.NO_POSITION)
            mPosition = 0;
        mRecyclerView.smoothScrollToPosition(mPosition);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    private void showFavorites() {
        this.favorites = true;
        UiChangeToShowFavorites();
        getSupportLoaderManager().initLoader(FAV_INIT_LOADER, null, this);
    }

    private void UiChangeToShowFavorites() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mErrorMessage.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkChangeReceiver);
    }

    private class onNetworkChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (MovieNetworkUtils.checkNetworkStatus(context) && !favorites && movieList.size() == 0) {
                executeNetworkCallTask(movies_option);
            }
        }
    }

    public void fetchMovieData(String option) {
        OkHttpClient okHttpClient = OkHttpClientHelper.getOkHttpClient();
        URL url = MovieNetworkUtils.getNetworkURL(option, MainActivity.this);
        Request request = new Request.Builder()
                .url(url.toString())
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e)
            {
                Log.e(TAG, "FailedResponse ");
            }

            @Override
            public void onResponse(Response response) throws IOException {
                List<Movie> movies = null;
                if (!response.isSuccessful()) {
                    Log.e(TAG, "onResponse:Unexpected code " + response);
                }
                InputStream stream;
                try {
                    String resultString = response.body().string();
                    movies = MovieUtils.getMoviesListFromJsonResponse(resultString);
                    updateUI(movies);
                } catch (IOException e) {
                    Log.e(TAG, "onResponse: ", e);
                }
            }
        });
    }

    public void updateUI(List<Movie> movies){
        movieList = movies;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMovieAdapter.setMovieData(movieList);
                mRecyclerView.smoothScrollToPosition(0);
                mLoadingIndicator.setVisibility(View.INVISIBLE);
            }
        });

    }
}

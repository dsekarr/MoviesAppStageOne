package com.example.dsekar.moviesstageone;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.dsekar.moviesstageone.utilities.MovieNetworkUtils;
import com.example.dsekar.moviesstageone.utilities.MovieUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterClickHandler {

    private static final String EXTRA_MOVIE = "Movie_Intent";
    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;
    private ProgressBar mLoadingIndicator;
    private TextView mErrorMessage;
    private List<Movie> movieList = new ArrayList<>();
    private static final String CURRENT_MOVIES_DISPLAY = "current_movies";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.initUI();

        GridLayoutManager layoutManager =
                new GridLayoutManager(MainActivity.this, MovieUtils.getSpanCount(this), GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mMovieAdapter = new MovieAdapter(MainActivity.this, this);
        mRecyclerView.setAdapter(mMovieAdapter);

        if (savedInstanceState == null) {
            executeNetworkCallTask(MovieNetworkUtils.MOST_POPULAR);
        } else {
            movieList = savedInstanceState.getParcelableArrayList(CURRENT_MOVIES_DISPLAY);
            if (movieList != null && movieList.size() > 0) {
                mMovieAdapter.setMovieData(movieList);
            } else {
                mRecyclerView.setVisibility(View.INVISIBLE);
                mErrorMessage.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * Initialize the UI.
     */
    private void initUI() {
        mRecyclerView = findViewById(R.id.recycler_view_movies);
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);
        mErrorMessage = findViewById(R.id.error_message);
    }

    /**
     * Execute network Task.
     *
     * @param option
     */
    private void executeNetworkCallTask(String option) {
        mRecyclerView.setVisibility(View.VISIBLE);
        mErrorMessage.setVisibility(View.INVISIBLE);
        if (MovieNetworkUtils.checkNetworkStatus(this)) {
            URL getMovieApiURL = MovieNetworkUtils.getNetworkURL(option, this);
            new FetchMovieTask().execute(getMovieApiURL);
        } else {
            mRecyclerView.setVisibility(View.INVISIBLE);
            movieList = null;
            mErrorMessage.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public void onClick(Movie movie) {
        Intent startDetailsActivityIntent = new Intent(this, DetailActivity.class);
        startDetailsActivityIntent.putExtra(EXTRA_MOVIE, movie);
        startActivity(startDetailsActivityIntent);
    }

    /**
     * Inner class to make network call in the background.
     */
    @SuppressLint("StaticFieldLeak")
    public class FetchMovieTask extends AsyncTask<URL, Void, List<Movie>> {

        @Override
        protected void onPreExecute() {
            mLoadingIndicator.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected List<Movie> doInBackground(URL... urls) {
            List<Movie> movies = null;
            try {
                URL url = urls[0];
                String jsonMovieResponse = MovieNetworkUtils.getResponseFromHttpUrl(url);
                movies = MovieUtils.getMoviesListFromJsonResponse(jsonMovieResponse);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return movies;
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            mMovieAdapter.setMovieData(movies);
            movieList = movies;
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            super.onPostExecute(movies);
        }
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
        int id = item.getItemId();
        if (id == R.id.most_popular || id == R.id.action_refresh) {
            executeNetworkCallTask(MovieNetworkUtils.MOST_POPULAR);
        }
        if (id == R.id.high_rated) {
            executeNetworkCallTask(MovieNetworkUtils.TOP_RATED);
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

}

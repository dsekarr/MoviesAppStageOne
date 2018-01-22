package com.example.dsekar.moviesstageone;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.dsekar.moviesstageone.Data.Movie;
import com.example.dsekar.moviesstageone.Data.Review;
import com.example.dsekar.moviesstageone.utilities.MovieNetworkUtils;
import com.example.dsekar.moviesstageone.utilities.MovieUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewActivity extends AppCompatActivity {

    private ReviewAdapter rAdapter;
    private static final String REVIEW = "reviews";
    private static final String EXTRA_MOVIE = "Movie_Intent";
    private Movie movie = new Movie();

    @BindView(R.id.recyler_review)
    RecyclerView rRecyclerView;

    @BindView(R.id.noNetwork)
    TextView noNetwork;

    @BindView(R.id.noReview_result)
    TextView noResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(EXTRA_MOVIE)) {
                movie = intent.getParcelableExtra(EXTRA_MOVIE);
            }
        }

        rAdapter = new ReviewAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rRecyclerView.setLayoutManager(layoutManager);
        rRecyclerView.setHasFixedSize(true);
        rRecyclerView.setAdapter(rAdapter);
        executeReviewTask();
    }

    private void executeReviewTask() {
        if (MovieNetworkUtils.checkNetworkStatus(this)) {
            rRecyclerView.setVisibility(View.VISIBLE);
            noNetwork.setVisibility(View.INVISIBLE);
            noResult.setVisibility(View.INVISIBLE);
            URL url = MovieNetworkUtils.buildUrl(movie.getId(), REVIEW, this);
            new FetchReviewTask().execute(url);
        } else {
            rRecyclerView.setVisibility(View.INVISIBLE);
            noResult.setVisibility(View.INVISIBLE);
            noNetwork.setVisibility(View.VISIBLE);
        }
    }

    public class FetchReviewTask extends AsyncTask<URL, Void, List<Review>> {
        @Override
        protected List<Review> doInBackground(URL... urls) {
            List<Review> reviewList = new ArrayList<>();
            URL url = urls[0];
            try {
                String jsonResponse = MovieNetworkUtils.getResponseFromHttpUrl(url);
                reviewList = MovieUtils.getReviewsFromJsonResponse(jsonResponse);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return reviewList;
        }

        @Override
        protected void onPostExecute(List<Review> reviews) {
            super.onPostExecute(reviews);
            if (reviews.size() == 0) {
                noResult.setVisibility(View.VISIBLE);
                noNetwork.setVisibility(View.INVISIBLE);
            }
            rAdapter.setReviewsList(reviews);
        }
    }
}

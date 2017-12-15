package com.example.dsekar.moviesstageone;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dsekar.moviesstageone.utilities.MovieNetworkUtils;
import com.example.dsekar.moviesstageone.utilities.MovieUtils;
import com.squareup.picasso.Picasso;

import java.text.ParseException;

public class DetailActivity extends AppCompatActivity {

    private TextView mTitle;
    private ImageView mImage;
    private TextView mReleaseYear;
    private TextView mRating;
    private TextView mFavorite;
    private TextView mOverview;
    private static final String EXTRA_MOVIE = "Movie_Intent";
    private boolean isPressed = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        initUI();

        Movie movie = null;
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(EXTRA_MOVIE)) {
                movie = intent.getParcelableExtra(EXTRA_MOVIE);
            }
        }

        //onClick Listener for movie
        mFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPressed)
                    mFavorite.setBackgroundResource(R.drawable.ic_star_black_18dp);
                else
                    mFavorite.setBackgroundResource(R.drawable.ic_star_border_black_18dp);

                isPressed = !isPressed;
            }
        });

        assert movie != null;
        String title = movie.getOriginalTitle();
        mTitle.setText(title);

        String poster_path = movie.getPosterPath();
        String posterPathURL = MovieNetworkUtils.MOVIE_URI + poster_path;
        Picasso.with(this).load(posterPathURL).into(mImage);

        String movieReleaseYear = movie.getReleaseDate();
        String getYear = null;
        try {
            getYear = MovieUtils.getNormalizedYearFromDate(movieReleaseYear);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        mReleaseYear.setText(getYear);


        Double movieRating = movie.getVoteAverage();
        mRating.setText(String.format("%s/10", Double.toString(movieRating)));

        String movieOverview = movie.getOverview();
        mOverview.setText(movieOverview);
    }

    /**
     * Initialize the variables.
     */
    private void initUI() {
        mTitle = findViewById(R.id.movie_title);
        mImage = findViewById(R.id.movie_imageView);
        mReleaseYear = findViewById(R.id.movie_release_year);
        mRating = findViewById(R.id.movie_rating);
        mFavorite = findViewById(R.id.movie_markFavorite);
        mOverview = findViewById(R.id.movie_overview);
    }
}


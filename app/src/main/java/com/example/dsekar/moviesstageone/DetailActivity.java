package com.example.dsekar.moviesstageone;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dsekar.moviesstageone.Data.Movie;
import com.example.dsekar.moviesstageone.utilities.MovieNetworkUtils;
import com.example.dsekar.moviesstageone.utilities.MovieUtils;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

    @BindView(R.id.detail_movie_CollapsingToolBar)
    CollapsingToolbarLayout collapsingToolbarLayout;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.backdrop_image)
    ImageView backdropImage;

    @BindView(R.id.movie_imageView)
    ImageView mImage;

    @BindView(R.id.movie_release_year)
    TextView mReleaseYear;

    @BindView(R.id.movie_rating)
    TextView mRating;

    @BindView(R.id.movie_language)
    TextView mLanguage;

    @BindView(R.id.movie_overview)
    TextView mOverview;

    @BindView(R.id.fav_button)
    FloatingActionButton mFavorite;

    @BindView(R.id.bottom_navigation_view)
    BottomNavigationView bottomNavigationView;

    private static final String EXTRA_MOVIE = "Movie_Intent";
    private Movie movie = new Movie();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(EXTRA_MOVIE)) {
                movie = intent.getParcelableExtra(EXTRA_MOVIE);
            }
        }
        //toolbar.setNavigationIcon(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_material);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
            }
        });

        collapsingToolbarLayout.setTitle(movie.getTitle());

        Boolean isFavorite = MovieUtils.isFavorite(movie, this);

        if(isFavorite){
            mFavorite.setImageResource(R.drawable.ic_star_black_18dp);
        }
        else {
            mFavorite.setImageResource(R.drawable.ic_star_border_black_18dp);
        }

        String backdrop_path = movie.getBackdropPath();
        String backdropPathURL = MovieNetworkUtils.MOVIE_URI + backdrop_path;
        Picasso.with(this).load(backdropPathURL).into(backdropImage);

        String poster_path = movie.getPosterPath();
        String posterPathURL = MovieNetworkUtils.MOVIE_URI + poster_path;
        Picasso.with(this).load(posterPathURL).into(mImage);

        String movieReleaseYear = movie.getReleaseDate();
        mReleaseYear.setText(movieReleaseYear);

        Double movieRating = movie.getVoteAverage();
        mRating.setText(String.format("%s/10", Double.toString(movieRating)));

        String movieLang = movie.getOriginalLanguage();
        mLanguage.setText(movieLang);

        String movieOverview = movie.getOverview();
        mOverview.setText(movieOverview);

        //onClick Listener for movie
        mFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFavorite();
            }
        });


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int menuId = item.getItemId();
                switch (menuId){
                    case R.id.overview:

                        break;

                    case R.id.Video:
                        Intent videoActivityIntent = new Intent(getApplicationContext(), VideoActivity.class);
                        videoActivityIntent.putExtra(EXTRA_MOVIE, movie);
                        startActivity(videoActivityIntent);
                        break;

                    case R.id.Review:
                        Intent reviewActivityIntent = new Intent(getApplicationContext(), ReviewActivity.class);
                        reviewActivityIntent.putExtra(EXTRA_MOVIE, movie);
                        startActivity(reviewActivityIntent);
                        break;
                }
                return true;
            }
        });
    }

    private void setFavorite(){
        if (MovieUtils.isFavorite(movie, this)) {
            MovieUtils.removeFavorite(movie.getId(), this);
            mFavorite.setImageResource(R.drawable.ic_star_border_black_18dp);
        } else {
            MovieUtils.markFavorite(movie, this);
            mFavorite.setImageResource(R.drawable.ic_star_black_18dp);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}





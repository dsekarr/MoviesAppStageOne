package com.example.dsekar.moviesstageone;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.dsekar.moviesstageone.Data.Movie;
import com.example.dsekar.moviesstageone.Data.Video;
import com.example.dsekar.moviesstageone.utilities.MovieNetworkUtils;
import com.example.dsekar.moviesstageone.utilities.MovieUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoActivity extends AppCompatActivity implements VideoAdapter.VideoClickHandler {

    private VideoAdapter vAdapter;
    private static final String VIDEO = "videos";
    private static final String EXTRA_MOVIE = "Movie_Intent";
    private Movie movie = new Movie();

    @BindView(R.id.recyler_Video)
    RecyclerView vRecyclerView;

    @BindView(R.id.noNetwork_video)
    TextView noNetwork_Video;

    @BindView(R.id.noVideo_result)
    TextView noVideoResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(EXTRA_MOVIE)) {
                movie = intent.getParcelableExtra(EXTRA_MOVIE);
            }
        }

        vAdapter = new VideoAdapter(VideoActivity.this, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        vRecyclerView.setLayoutManager(layoutManager);
        vRecyclerView.setHasFixedSize(true);
        vRecyclerView.setAdapter(vAdapter);

        if (MovieNetworkUtils.checkNetworkStatus(this)) {
            vRecyclerView.setVisibility(View.VISIBLE);
            noNetwork_Video.setVisibility(View.INVISIBLE);
            noVideoResult.setVisibility(View.INVISIBLE);
            executeVideoTask();
        } else {
            vRecyclerView.setVisibility(View.INVISIBLE);
            noVideoResult.setVisibility(View.INVISIBLE);
            noNetwork_Video.setVisibility(View.VISIBLE);
        }
    }

    private void executeVideoTask() {
        URL url = MovieNetworkUtils.buildUrl(movie.getId(), VIDEO, this);
        new FetchVideoTask().execute(url);
    }

    @Override
    public void onClick(Video video) {
//        Intent webIntent = new Intent(Intent.ACTION_VIEW,
//                MovieNetworkUtils.getVideoLink(video.getKey()));
        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + video.getKey()));
        if (webIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(webIntent);
        }
    }

    public class FetchVideoTask extends AsyncTask<URL, Void, List<Video>> {

        @Override
        protected List<Video> doInBackground(URL... urls) {
            List<Video> videoList = new ArrayList<>();
            URL url = urls[0];
            try {
                String jsonResponse = MovieNetworkUtils.getResponseFromHttpUrl(url);
                videoList = MovieUtils.getVideosFromJsonResponse(jsonResponse);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return videoList;
        }

        @Override
        protected void onPostExecute(List<Video> videos) {
            super.onPostExecute(videos);
            if (videos.size() == 0) {
                noVideoResult.setVisibility(View.VISIBLE);
                noNetwork_Video.setVisibility(View.INVISIBLE);
            }
            vAdapter.setVideosList(videos);
        }
    }
}

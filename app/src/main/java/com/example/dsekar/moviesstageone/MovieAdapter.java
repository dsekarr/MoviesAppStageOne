package com.example.dsekar.moviesstageone;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.dsekar.moviesstageone.Data.Movie;
import com.example.dsekar.moviesstageone.utilities.MovieNetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private List<Movie> movies_List = new ArrayList<>();
    private final Context mContext;
    private final MovieAdapterClickHandler clickHandler;

    public MovieAdapter(Context context, MovieAdapterClickHandler click_Handler) {
        mContext = context;
        clickHandler = click_Handler;
    }

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.movies_layout, viewGroup, false);
        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {
        Movie movie = movies_List.get(position);
        String poster_path = movie.getPosterPath();
        String posterPathURL = MovieNetworkUtils.MOVIE_URI + poster_path;
        Picasso.with(mContext).load(posterPathURL).into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        if (movies_List != null) {
            return movies_List.size();
        }
        return 0;
    }

    public void setMovieData(List<Movie> moviesList) {
        movies_List = moviesList;
        notifyDataSetChanged();
    }

    /**
     * MovieAdapterClickHandler
     */
    public interface MovieAdapterClickHandler {
        void onClick(Movie movie);
    }

    /**
     * View Holder
     */
    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.image_poster_view)
        ImageView mImageView;

        public MovieAdapterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Movie movie = movies_List.get(position);
            clickHandler.onClick(movie);
        }
    }
}
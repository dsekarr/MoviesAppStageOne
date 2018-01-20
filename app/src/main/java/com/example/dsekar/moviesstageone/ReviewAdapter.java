package com.example.dsekar.moviesstageone;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dsekar.moviesstageone.Data.Review;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder> {

    private List<Review> reviewsList = new ArrayList<>();

    public ReviewAdapter(){

    }

    @Override
    public ReviewAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_card, parent, false);
        return new ReviewAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewAdapterViewHolder holder, int position) {
        Review review = reviewsList.get(position);
        holder.reviewer.setText(review.getAuthor());
        holder.reviewDetails.setText(review.getContent());
    }

    @Override
    public int getItemCount() {
        if(reviewsList != null){
            return reviewsList.size();
        }
        return 0;
    }

    public void setReviewsList(List<Review> reviews){
        reviewsList = reviews;
        notifyDataSetChanged();
    }

    public class ReviewAdapterViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.reviewer)
        TextView reviewer;

        @BindView(R.id.reviewDetails)
        TextView reviewDetails;

        public ReviewAdapterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

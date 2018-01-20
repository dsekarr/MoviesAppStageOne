package com.example.dsekar.moviesstageone;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.dsekar.moviesstageone.Data.Video;
import com.example.dsekar.moviesstageone.utilities.MovieNetworkUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoAdapterViewHolder> {

    private List<Video> videosList = new ArrayList<>();

    private Context mContext;

    private VideoClickHandler mVideoClickHandler;

    public VideoAdapter(Context context, VideoClickHandler videoClickHandler){
        mContext = context;
        mVideoClickHandler = videoClickHandler;
    }

    @Override
    public VideoAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_card, parent, false);
        return new VideoAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final VideoAdapterViewHolder holder, int position) {
        Video video = videosList.get(position);
        String videoName = video.getName();
        holder.videoName.setText(videoName);
        String videoLink = MovieNetworkUtils.VIDEO_BASE_URL + video.getKey() + "/hqdefault.jpg";
        Picasso.with(mContext).load(videoLink).into(holder.videoPoster, new Callback() {
            @Override
            public void onSuccess() {
                holder.mProgressBar.setVisibility(View.GONE);
                holder.playButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError() {

            }
        });
    }

    @Override
    public int getItemCount() {
        if(videosList != null){
            return videosList.size();
        }
        return 0;
    }

    public void setVideosList(List<Video> videos){
        videosList = videos;
        notifyDataSetChanged();
    }

    public class VideoAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.video_poster)
        ImageView videoPoster;

        @BindView(R.id.play_pause_button)
        ImageButton playButton;

        @BindView(R.id.progress_bar)
        ProgressBar mProgressBar;

        @BindView(R.id.video_title)
        TextView videoName;

        public VideoAdapterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            playButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Video video = videosList.get(position);
            mVideoClickHandler.onClick(video);
        }
    }

    public interface VideoClickHandler{
        void onClick(Video video);
    }
}

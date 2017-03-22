package com.ansijax.udacity.popularmovies.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ansijax.udacity.popularmovies.popularmovies.pojo.Videos;
import com.ansijax.udacity.popularmovies.popularmovies.pojo.VideosResult;
import com.squareup.picasso.Picasso;

/**
 * Created by massimo on 22/03/17.
 */

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.ViedoListViewHolder> {

    VideosAdapterOnClick mClickHandler;
    Videos mVideos;

    public VideosAdapter(VideosAdapterOnClick clickHandler) {
        mClickHandler = clickHandler;
    }

    @Override
    public ViedoListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item, parent, false);
        ViedoListViewHolder holder = new ViedoListViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViedoListViewHolder holder, int position) {

        holder.mVideoTitle.setText(mVideos.getResults().get(position).getName());
        String imagePath = composeYoutubeThumbnail(mVideos.getResults().get(position).getKey());
        Picasso.with(holder.mContext).load(imagePath).error(R.drawable.video_place_holder).placeholder(R.drawable.video_place_holder).into(holder.mVideoThumbnail);
    }

    @Override
    public int getItemCount() {
        if (mVideos == null)
            return 0;
        else
            return mVideos.getResults().size();
    }

    public interface VideosAdapterOnClick {

        void onClick(VideosResult clickledVideos);
    }

    public class ViedoListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView mVideoThumbnail;
        TextView mVideoTitle;
        Context mContext;

        public ViedoListViewHolder(View itemView) {
            super(itemView);
            mVideoThumbnail = (ImageView) itemView.findViewById(R.id.iv_video);
            mVideoTitle = (TextView) itemView.findViewById(R.id.tv_video_desc);
            mContext = itemView.getContext();
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPostion = getAdapterPosition();
            VideosResult clickedVideos = mVideos.getResults().get(adapterPostion);
            mClickHandler.onClick(clickedVideos);
        }
    }


    private String composeYoutubeThumbnail(String key) {
        String baseYouTubeUrl = "https://img.youtube.com/vi/";
        String imgType = "/hqdefault.jpg";
        String path = baseYouTubeUrl + key + imgType;
        Log.d("ADAPTER_YOUTUBE_THUMB", path);
        return path;

    }

    public void setAdapter(Videos videos) {
        mVideos = videos;
        notifyDataSetChanged();
    }
}

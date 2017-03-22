package com.ansijax.udacity.popularmovies.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ansijax.udacity.popularmovies.popularmovies.pojo.Movie;
import com.ansijax.udacity.popularmovies.popularmovies.pojo.Videos;
import com.squareup.picasso.Picasso;

/**
 * Created by massimo on 22/03/17.
 */

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.ViedoListViewHolder> {

    VideosAdapterOnClick mClickHandler;
    Videos mVideos;
    public VideosAdapter(VideosAdapterOnClick clickHandler){
        mClickHandler=clickHandler;
    }

    @Override
    public ViedoListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ViedoListViewHolder holder, int position) {

        holder.mVideoTitle.setText(mVideos.getResults().get(position).getName());
        String imagePath=composeYoutubeThumbnail(mVideos.getResults().get(position).getKey());
        Picasso.with(holder.mContext).load(imagePath).error(R.drawable.placeholder).placeholder(R.drawable.placeholder).into(holder.mVideoThumbnail);
    }

    @Override
    public int getItemCount() {
        if(mVideos==null)
            return 0;
        else
            return mVideos.getResults().size();
    }

    public interface VideosAdapterOnClick{
        //TODO
        void onClick(Movie movie);
    }

    public class ViedoListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView mVideoThumbnail;
        TextView mVideoTitle;
        Context mContext;
        public ViedoListViewHolder(View itemView) {
            super(itemView);
            mVideoThumbnail = (ImageView) itemView.findViewById(R.id.iv_video);
            mVideoTitle=(TextView) itemView.findViewById(R.id.tv_video_desc);
            mContext=itemView.getContext();
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
                //TODO
        }
    }




    private String composeYoutubeThumbnail(String key){
        String baseYouTubeUrl="https://img.youtube.com/vi/";
        String imgType="/default.jpg";
        return baseYouTubeUrl+key+imgType;

    }

    public void setAdapter(Videos videos){
        mVideos=videos;
        notifyDataSetChanged();
    }
}

package com.ansijax.udacity.popularmovies.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ansijax.udacity.popularmovies.popularmovies.network.OkHttpRequest;
import com.ansijax.udacity.popularmovies.popularmovies.pojo.Movie;
import com.ansijax.udacity.popularmovies.popularmovies.pojo.MovieList;
import com.squareup.picasso.Picasso;

/**
 * Created by massimo on 02/02/17.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieListViewHolder>{

    MovieList mMovieList;
    MoviesAdapterOnClick mClickHandler;

    public MoviesAdapter(MoviesAdapterOnClick clickHandler){
        //TODO controllare se è giusta questa cosa
        mClickHandler=clickHandler;

    }


    @Override
    public MovieListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.poster_item,parent,false);
        MovieListViewHolder holder = new MovieListViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MovieListViewHolder holder, int position) {
        //Picasso.with(holder.mContext).setIndicatorsEnabled(true); //check image caching
        String posterPath=mMovieList.getMovies().get(position).getPosterPath();
        holder.mTitleTextView.setText(mMovieList.getMovies().get(position).getTitle());
        //needed to set via code, cause via xml only one item get the marquee effect
        holder.mTitleTextView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        holder.mTitleTextView.setSingleLine(true);
        holder.mTitleTextView.setSelected(true);
        holder.mTitleTextView.requestFocus();


        String imagePath=null;
        if (posterPath!=null) {
             imagePath= OkHttpRequest.buildImageUrl(posterPath);
        }
        Picasso.with(holder.mContext).load(imagePath).error(R.drawable.placeholder).placeholder(R.drawable.placeholder).into(holder.mImageView);
    }

    public interface MoviesAdapterOnClick{
           void onClick(Movie movie);
    }


    @Override
    public int getItemCount() {
        if(mMovieList==null) {

            return 0 ;
        }
        else {

            return mMovieList.getMovies().size();
        }
    }


    public class MovieListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public ImageView mImageView;
        public Context mContext;
        public TextView mTitleTextView;


        public MovieListViewHolder(View view){

            super(view);
            mImageView=(ImageView) view.findViewById(R.id.iv_movie_poster);
            mContext = view.getContext();
            mTitleTextView =(TextView) view.findViewById(R.id.tv_grid_movie_name);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPostion = getAdapterPosition();
            Movie clickedMovie=mMovieList.getMovies().get(adapterPostion);
            mClickHandler.onClick(clickedMovie);
        }
    }

    public void setAdapter(MovieList movieList){

        mMovieList=movieList;

        notifyDataSetChanged();
    }


}


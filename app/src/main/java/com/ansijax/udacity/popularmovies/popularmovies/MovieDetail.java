package com.ansijax.udacity.popularmovies.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.ansijax.udacity.popularmovies.popularmovies.network.OkHttpRequest;
import com.ansijax.udacity.popularmovies.popularmovies.pojo.Movie;
import com.squareup.picasso.Picasso;

public class MovieDetail extends AppCompatActivity {
    static private final String BUNDLE="bundle";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        Intent intent=getIntent();
        Movie movie=(Movie) intent.getParcelableExtra(BUNDLE);
        setInformation(movie);
    }

    public void setInformation(Movie movie){
        ImageView displayPoster = (ImageView) findViewById(R.id.iv_detail_poster);
        TextView displayName=(TextView)findViewById(R.id.tv_movie_name);
        TextView displayDescription = (TextView) findViewById(R.id.tv_movie_description);
        TextView displayRating = (TextView) findViewById(R.id.tv_detail_rating);
        TextView displayRelease = (TextView) findViewById(R.id.tv_detail_relase_date);



        displayName.setText(movie.getTitle());
        displayRating.setText(movie.getVoteAverage().toString());
        displayRelease.setText(movie.getReleaseDate());

        String posterPath= movie.getPosterPath();
        String imagePath=null;
        if(posterPath!=null)
             imagePath= OkHttpRequest.buildImageUrl(posterPath);
        Picasso.with(this).load(imagePath).error(R.drawable.placeholder).placeholder(R.drawable.placeholder).into(displayPoster);

        displayDescription.setText(movie.getOverview());
    }
}

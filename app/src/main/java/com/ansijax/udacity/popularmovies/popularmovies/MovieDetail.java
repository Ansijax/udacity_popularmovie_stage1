package com.ansijax.udacity.popularmovies.popularmovies;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ansijax.udacity.popularmovies.popularmovies.data.MovieColumns;
import com.ansijax.udacity.popularmovies.popularmovies.data.MoviesProvider;
import com.ansijax.udacity.popularmovies.popularmovies.network.OkHttpRequest;
import com.ansijax.udacity.popularmovies.popularmovies.pojo.Movie;
import com.ansijax.udacity.popularmovies.popularmovies.pojo.Reviews;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MovieDetail extends AppCompatActivity {
    static private final String BUNDLE="bundle";
    static private final String FAVORITE_MOVIE = "favorite_move";
    Movie _movie;
    ReviewsAdapter mReviewsAdapter;
    RecyclerView mReviewsRecycleView;
    ImageView displayPoster;
    Boolean isFavorite;
    RecyclerView.LayoutManager mLayoutManager;
    OkHttpClient httpClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        Intent intent=getIntent();
        mReviewsAdapter= new ReviewsAdapter();
        httpClient = new OkHttpClient();
        mReviewsRecycleView= (RecyclerView) findViewById(R.id.rv_reviews);
        mLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        mReviewsRecycleView.setLayoutManager(mLayoutManager);
        mReviewsRecycleView.setAdapter(mReviewsAdapter);

        if(intent.hasExtra(BUNDLE)){

            _movie=(Movie) intent.getParcelableExtra(BUNDLE);
            setInformation(_movie);
            callNetwork(_movie.getId(),false);

        }

        else{

            int id=intent.getIntExtra(FAVORITE_MOVIE,-1);
            callNetwork(id,true);

        }



    }

    public void setInformation(Movie movie){
        displayPoster = (ImageView) findViewById(R.id.iv_detail_poster);
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

        manageFavorite();
    }


    public boolean checkIsFavorite(int id){
        ContentResolver contentResolver =this.getContentResolver();
        Cursor cursor=contentResolver.query(MoviesProvider.FavoriteMovies.withId(new Long(id)),
                null,
                null,
                null,
                null);
        if((!(cursor.moveToFirst()) || cursor.getCount() ==0))
            return false;

        cursor.close();
        return true;
    }


    public void addToFavorite(){
        ContentResolver contentResolver =this.getContentResolver();
        ContentValues cv = new ContentValues();
        cv.put(MovieColumns.MOVIE_ID,_movie.getId());
        cv.put(MovieColumns.TITLE,_movie.getTitle());
        cv.put(MovieColumns.MOVIE_IMG,bitmapToByte());

        Toast.makeText(this,getResources().getString(R.string.movies_added),Toast.LENGTH_LONG).show();
        manageFavorite();

    }

    public void removeFromFavorite(){
        int id =_movie.getId();
        ContentResolver contentResolver =this.getContentResolver();
        contentResolver.delete(MoviesProvider.FavoriteMovies.withId(new Long(id)),
                null,
                null);

        manageFavorite();

        Toast.makeText(this,getResources().getString(R.string.movies_removed),Toast.LENGTH_LONG).show();
    }


    public void favoritePushed(View view){
        if(isFavorite){
            removeFromFavorite();
        }else{
            addToFavorite();
        }
        return;
    }


    public void manageFavorite(){
        isFavorite=checkIsFavorite(_movie.getId());
        ImageView imageView =(ImageView) findViewById(R.id.iv_favorite);
        if(isFavorite){

            imageView.setImageResource(R.drawable.heart_selected);
        }
        else {
            imageView.setImageResource(R.drawable.heart);
        }

    }

    public byte[] bitmapToByte() {
        Bitmap bmp =((BitmapDrawable)displayPoster.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    public void callNetwork(int id, boolean isFavorite){
        if(isFavorite)
            getMovie(OkHttpRequest.buildMovieUrl(id));
        getReviews(OkHttpRequest.buildReviewUrl(id));

    }

    public String getMovie(Request request) {

        final Gson gson = new Gson();
        final Handler mHandler = new Handler(Looper.getMainLooper());

        httpClient.newCall(request).enqueue(new Callback() {


            @Override
            public void onFailure(Call call, IOException e) {


                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //TODO insert here the progress bar
                    //    prepareErrorLayout(getResources().getString(R.string.error_connection));
                    //    mLoadingProgressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    _movie = gson.fromJson(response.body().charStream(), Movie.class);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                          //  prepareSucessLayout();
                            // mAdapter.setAdapter(movies);
                            setInformation(_movie);
                        }
                    });
                } else {
                    //prepareErrorLayout(getResources().getString(R.string.error_failure));
                }

            }
        });

        return null;

    }


    public String getReviews(Request request) {

        final Gson gson = new Gson();
        final Handler mHandler = new Handler(Looper.getMainLooper());

        httpClient.newCall(request).enqueue(new Callback() {


            @Override
            public void onFailure(Call call, IOException e) {


                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //TODO insert here the progress bar
                        //    prepareErrorLayout(getResources().getString(R.string.error_connection));
                        //    mLoadingProgressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final Reviews reviews = gson.fromJson(response.body().charStream(), Reviews.class);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            //  prepareSucessLayout();
                            // mAdapter.setAdapter(movies);
                            mReviewsAdapter.setAdapter(reviews);
                        }
                    });
                } else {
                    //prepareErrorLayout(getResources().getString(R.string.error_failure));
                }

            }
        });

        return null;

    }

}

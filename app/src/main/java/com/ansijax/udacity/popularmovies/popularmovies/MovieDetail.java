package com.ansijax.udacity.popularmovies.popularmovies;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ansijax.udacity.popularmovies.popularmovies.data.MovieContract;
import com.ansijax.udacity.popularmovies.popularmovies.network.OkHttpRequest;
import com.ansijax.udacity.popularmovies.popularmovies.pojo.Movie;
import com.ansijax.udacity.popularmovies.popularmovies.pojo.Reviews;
import com.ansijax.udacity.popularmovies.popularmovies.pojo.Videos;
import com.ansijax.udacity.popularmovies.popularmovies.pojo.VideosResult;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MovieDetail extends AppCompatActivity implements VideosAdapter.VideosAdapterOnClick {
    static private final String BUNDLE = "bundle";
    static private final String FAVORITE_MOVIE = "favorite_move";
    Movie _movie;
    ReviewsAdapter mReviewsAdapter;
    VideosAdapter mVideosAdapter;
    RecyclerView mReviewsRecycleView;
    RecyclerView mVideosRecycleView;
    ImageView displayPoster;
    Boolean isFavorite;
    Videos mVideos;
    Reviews mReviews;
    RecyclerView.LayoutManager mReviewLayoutManager;
    RecyclerView.LayoutManager mVideosLayoutManager;
    OkHttpClient httpClient;
    TextView mTvNoReview;
    TextView mTvNoVideo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        Intent intent = getIntent();

        mTvNoReview = (TextView) findViewById(R.id.tv_no_review);
        mTvNoVideo = (TextView) findViewById(R.id.tv_no_video);
        httpClient = new OkHttpClient();

        mReviewsAdapter = new ReviewsAdapter();
        mVideosAdapter = new VideosAdapter(this);

        mReviewsRecycleView = (RecyclerView) findViewById(R.id.rv_reviews);
        mReviewLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mReviewsRecycleView.setLayoutManager(mReviewLayoutManager);
        mReviewsRecycleView.setAdapter(mReviewsAdapter);
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
        mReviewsRecycleView.addItemDecoration(itemDecoration);


        mVideosRecycleView = (RecyclerView) findViewById(R.id.rv_videos);
        mVideosLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mVideosRecycleView.setLayoutManager(mVideosLayoutManager);
        mVideosRecycleView.setAdapter(mVideosAdapter);
        if(savedInstanceState==null){
            if (intent.hasExtra(BUNDLE)) {

                _movie = (Movie) intent.getParcelableExtra(BUNDLE);
                setInformation(_movie);
                callNetwork(_movie.getId(), false);

            } else {

                int id = intent.getIntExtra(FAVORITE_MOVIE, -1);
                callNetwork(id, true);

            }
        }


    }

    public void setInformation(Movie movie) {
        displayPoster = (ImageView) findViewById(R.id.iv_detail_poster);
        TextView displayName = (TextView) findViewById(R.id.tv_movie_name);
        TextView displayDescription = (TextView) findViewById(R.id.tv_movie_description);
        TextView displayRating = (TextView) findViewById(R.id.tv_detail_rating);
        TextView displayRelease = (TextView) findViewById(R.id.tv_detail_relase_date);


        displayName.setText(movie.getTitle());
        displayRating.setText(movie.getVoteAverage().toString());
        displayRelease.setText(movie.getReleaseDate());

        String posterPath = movie.getPosterPath();
        String imagePath = null;
        if (posterPath != null)
            imagePath = OkHttpRequest.buildImageUrl(posterPath);
        Picasso.with(this).load(imagePath).error(R.drawable.placeholder).placeholder(R.drawable.placeholder).into(displayPoster);

        displayDescription.setText(movie.getOverview());

        manageFavorite();
    }


    public boolean checkIsFavorite(int id) {
        ContentResolver contentResolver = this.getContentResolver();
        Cursor cursor = contentResolver.query(MovieContract.MovieEntry.buildMovieUriWithid(new Long(id)),
                null,
                null,
                null,
                null);
        if ((!(cursor.moveToFirst()) || cursor.getCount() == 0))
            return false;

        cursor.close();
        return true;
    }


    public void addToFavorite() {
        ContentResolver contentResolver = this.getContentResolver();
        ContentValues cv = new ContentValues();
        cv.put(MovieContract.MovieEntry.MOVIE_ID, _movie.getId());
        cv.put(MovieContract.MovieEntry.TITLE, _movie.getTitle());
        cv.put(MovieContract.MovieEntry.MOVIE_IMG, bitmapToByte());
        contentResolver.insert(MovieContract.MovieEntry.CONTENT_URI, cv);

        Toast.makeText(this, getResources().getString(R.string.movies_added), Toast.LENGTH_LONG).show();
        manageFavorite();

    }

    public void removeFromFavorite() {
        int id = _movie.getId();
        ContentResolver contentResolver = this.getContentResolver();
        contentResolver.delete(MovieContract.MovieEntry.buildMovieUriWithid(new Long(id)),
                null,
                null);

        manageFavorite();

        Toast.makeText(this, getResources().getString(R.string.movies_removed), Toast.LENGTH_LONG).show();
    }


    public void favoritePushed(View view) {
        if (isFavorite) {
            removeFromFavorite();
        } else {
            addToFavorite();
        }
        return;
    }


    public void manageFavorite() {
        isFavorite = checkIsFavorite(_movie.getId());
        ImageView imageView = (ImageView) findViewById(R.id.iv_favorite);
        if (isFavorite) {

            imageView.setImageResource(R.drawable.heart_selected);
        } else {
            imageView.setImageResource(R.drawable.heart);
        }

    }

    public byte[] bitmapToByte() {
        Bitmap bmp = ((BitmapDrawable) displayPoster.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    public void callNetwork(int id, boolean isFavorite) {
        if (isFavorite)
            getMovie(OkHttpRequest.buildMovieUrl(id));
        getReviews(OkHttpRequest.buildReviewUrl(id));
        getVideos(OkHttpRequest.buildVideosUrl(id));

    }

    public String getMovie(Request request) {

        final Gson gson = new Gson();
        final Handler mHandler = new Handler(Looper.getMainLooper());
        final ProgressBar mLoadingProgressBar = (ProgressBar) findViewById(R.id.pb_movie_info);
        mLoadingProgressBar.setVisibility(View.VISIBLE);
        httpClient.newCall(request).enqueue(new Callback() {


            @Override
            public void onFailure(Call call, IOException e) {


                mHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        //    prepareErrorLayout(getResources().getString(R.string.error_connection));
                        mLoadingProgressBar.setVisibility(View.INVISIBLE);
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
                            mLoadingProgressBar.setVisibility(View.INVISIBLE);
                            setInformation(_movie);
                        }
                    });
                }
            }
        });

        return null;

    }


    public String getReviews(Request request) {

        final Gson gson = new Gson();
        final Handler mHandler = new Handler(Looper.getMainLooper());
        final ProgressBar mLoadingProgressBar = (ProgressBar) findViewById(R.id.pb_review_loading);
        mLoadingProgressBar.setVisibility(View.VISIBLE);
        httpClient.newCall(request).enqueue(new Callback() {


            @Override
            public void onFailure(Call call, IOException e) {


                mHandler.post(new Runnable() {
                    @Override
                    public void run() {


                        mLoadingProgressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.isSuccessful()) {
                    mReviews = gson.fromJson(response.body().charStream(), Reviews.class);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mLoadingProgressBar.setVisibility(View.INVISIBLE);

                            if (mReviews.getResults().isEmpty())
                                mTvNoReview.setVisibility(View.VISIBLE);
                            else
                                mReviewsAdapter.setAdapter(mReviews);
                        }
                    });
                } else {
                    mTvNoReview.setVisibility(View.VISIBLE);

                }

            }
        });

        return null;

    }


    public String getVideos(Request request) {
        final ProgressBar mLoadingProgressBar = (ProgressBar) findViewById(R.id.pb_video_loading);

        final Gson gson = new Gson();
        final Handler mHandler = new Handler(Looper.getMainLooper());
        mLoadingProgressBar.setVisibility(View.VISIBLE);
        httpClient.newCall(request).enqueue(new Callback() {


            @Override
            public void onFailure(Call call, IOException e) {


                mHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        //    prepareErrorLayout(getResources().getString(R.string.error_connection));
                        mLoadingProgressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.isSuccessful()) {
                    mVideos = gson.fromJson(response.body().charStream(), Videos.class);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            //  prepareSucessLayout();
                            // mAdapter.setAdapter(movies);
                            mLoadingProgressBar.setVisibility(View.INVISIBLE);
                            if (mVideos.getResults().isEmpty())
                                mTvNoVideo.setVisibility(View.VISIBLE);
                            else
                                mVideosAdapter.setAdapter(mVideos);
                        }
                    });
                } else {
                    mTvNoVideo.setVisibility(View.VISIBLE);

                }

            }
        });

        return null;

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("movie",_movie);
        outState.putParcelable("videos",mVideos);
        outState.putParcelable("reviews",mReviews);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        _movie=savedInstanceState.getParcelable("movie");
        mVideos=savedInstanceState.getParcelable("videos");
        mReviews=savedInstanceState.getParcelable("reviews");

        mReviewsAdapter.setAdapter(mReviews);
        mVideosAdapter.setAdapter(mVideos);
        setInformation(_movie);

    }

    @Override
    public void onClick(VideosResult clickedVideo) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + clickedVideo.getKey()));
        if (intent.resolveActivity(getPackageManager()) == null) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + clickedVideo.getKey()));
        }
        startActivity(intent);

    }


}

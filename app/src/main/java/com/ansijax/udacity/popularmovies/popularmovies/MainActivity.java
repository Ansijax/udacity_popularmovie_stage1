package com.ansijax.udacity.popularmovies.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ansijax.udacity.popularmovies.popularmovies.network.OkHttpRequest;
import com.ansijax.udacity.popularmovies.popularmovies.pojo.Movie;
import com.ansijax.udacity.popularmovies.popularmovies.pojo.MovieList;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity implements MoviesAdapter.MoviesAdapterOnClick {

    RecyclerView mRecyclerView;
    Handler mHandler;
    RecyclerView.LayoutManager mLayoutManager;
    MoviesAdapter mAdapter;
    OkHttpClient httpClient;
    MovieList movies;
    ProgressBar mLoadingProgressBar;
    TextView mDisplayError;


    static private final int POPULAR = 0;
    static private final String BUNDLE = "bundle";
    static private final int TOP_RATED = 1;

    int mQueryType = POPULAR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        httpClient = new OkHttpClient();
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_posters);
        mLoadingProgressBar = (ProgressBar) findViewById(R.id.pb_loading);
        mDisplayError = (TextView) findViewById(R.id.tv_error_message);

        mLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MoviesAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        callNetwork();
    }


    public void callNetwork() {
        Request request = null;
        if (mQueryType == POPULAR) {

            request = OkHttpRequest.buildPopularRequest();

        } else if (mQueryType == TOP_RATED) {

            request = OkHttpRequest.buildTopRatedRequest();
        }

        getMovies(request);
        mLoadingProgressBar.setVisibility(View.VISIBLE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int idItem = item.getItemId();
        switch (idItem) {
            case R.id.menu_popular:
                mQueryType = POPULAR;
                break;
            case R.id.menu_rating:
                mQueryType = TOP_RATED;
                break;
        }
        mAdapter.setAdapter(null);
        Log.d("item_selected", "" + mQueryType);
        callNetwork();
        return super.onOptionsItemSelected(item);
    }

    public String getMovies(Request request) {

        final Gson gson = new Gson();
        mHandler = new Handler(Looper.getMainLooper());

        httpClient.newCall(request).enqueue(new Callback() {


            @Override
            public void onFailure(Call call, IOException e) {


                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        prepareErrorLayout(getResources().getString(R.string.error_connection));
                        mLoadingProgressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    movies = gson.fromJson(response.body().charStream(), MovieList.class);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            prepareSucessLayout();
                            mAdapter.setAdapter(movies);
                        }
                    });
                } else {
                    prepareErrorLayout(getResources().getString(R.string.error_failure));
                }

            }
        });

        return null;

    }


    public void prepareSucessLayout() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mLoadingProgressBar.setVisibility(View.INVISIBLE);
        mDisplayError.setVisibility(View.INVISIBLE);

    }

    public void prepareErrorLayout(String text) {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mLoadingProgressBar.setVisibility(View.INVISIBLE);
        mDisplayError.setVisibility(View.VISIBLE);
        mDisplayError.setText(text);
    }

    @Override
    public void onClick(Movie movie) {
        Intent intent = new Intent(MainActivity.this, MovieDetail.class);
        intent.putExtra(BUNDLE, movie);
        startActivity(intent);
    }
}

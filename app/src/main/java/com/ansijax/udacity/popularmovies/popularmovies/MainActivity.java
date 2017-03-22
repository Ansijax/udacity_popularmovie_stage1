package com.ansijax.udacity.popularmovies.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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

import com.ansijax.udacity.popularmovies.popularmovies.data.MovieColumns;
import com.ansijax.udacity.popularmovies.popularmovies.data.MoviesProvider;
import com.ansijax.udacity.popularmovies.popularmovies.network.OkHttpRequest;
import com.ansijax.udacity.popularmovies.popularmovies.pojo.Movie;
import com.ansijax.udacity.popularmovies.popularmovies.pojo.MovieList;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity implements MoviesAdapter.MoviesAdapterOnClick, LoaderManager.LoaderCallbacks<Cursor> {

    RecyclerView mRecyclerView;
    Handler mHandler;
    RecyclerView.LayoutManager mLayoutManager;
    MoviesAdapter mAdapter;
    OkHttpClient httpClient;
    MovieList movies;
    ProgressBar mLoadingProgressBar;
    TextView mDisplayError;
    boolean fromFavorite;

    static private final int POPULAR = 0;
    static private final String BUNDLE = "bundle";
    static private final String FAVORITE_MOVIE = "favorite_move";
    static private final int TOP_RATED = 1;
    static private final int FAVORITE = 2;
    static private final int ID_LOADER = 99;


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
            case R.id.menu_favorite:
                mQueryType = FAVORITE;
                break;


        }
        mAdapter.setAdapter(null);
        Log.d("item_selected", "" + mQueryType);
        if (mQueryType == FAVORITE)
            retrieveFromFavorite();
        else
            callNetwork();
        return super.onOptionsItemSelected(item);
    }

    public void retrieveFromFavorite() {

        getSupportLoaderManager().restartLoader(ID_LOADER, null, this);
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

    @Override
    protected void onResume() {
        super.onResume();
        if (mQueryType == FAVORITE) {
            retrieveFromFavorite();
        }
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
        if (mQueryType == FAVORITE)
            intent.putExtra(FAVORITE_MOVIE, movie.getId());
        else
            intent.putExtra(BUNDLE, movie);
        startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(this,
                MoviesProvider.FavoriteMovies.MOVIES,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        ArrayList<Movie> storedMovieList = new ArrayList<>();
        while (cursor.moveToNext()) {
            Movie movie = new Movie();
            movie.setImageBinary(cursor.getBlob(cursor.getColumnIndex(MovieColumns.MOVIE_IMG)));
            movie.setTitle(cursor.getString(cursor.getColumnIndex(MovieColumns.TITLE)));
            movie.setId(cursor.getInt(cursor.getColumnIndex(MovieColumns.MOVIE_ID)));
            storedMovieList.add(movie);

        }
        MovieList movieList = new MovieList();
        movieList.setMovies(storedMovieList);
        mAdapter.setAdapter(movieList);
        cursor.close();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.setAdapter(null);
    }
}

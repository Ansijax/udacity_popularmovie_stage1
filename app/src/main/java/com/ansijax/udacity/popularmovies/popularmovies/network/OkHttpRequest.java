package com.ansijax.udacity.popularmovies.popularmovies.network;

import android.util.Log;

import com.ansijax.udacity.popularmovies.popularmovies.BuildConfig;

import okhttp3.HttpUrl;
import okhttp3.Request;

/**
 * Created by massimo on 01/02/17.
 */



public class OkHttpRequest {

    static final private String SCHEMA_HTTPS = "https";
    static final private String SCHEMA_HTTP = "http";

    //BASE URL
    static final private String BASE_URL="api.themoviedb.org";
    static final private String BASE_URL_IMAGE="image.tmdb.org";

    static final private String MOVIE = "movie";
    static final private String API_VERSION = "3";

    //MOVIE DB FUNCTIONALITY
    static final private String DISCOVER = "discover";
    static final private String POPULAR = "popular";
    static final private String TOP_RATED = "top_rated";
    static final private String REVIEWS = "reviews";
    static final private String VIDEOS = "videos";

    //IMAGE
    static final private String IMAGE_SIZE="w185";

    //QUERY TYPE

    static final private String LANGUAGE="language";
    static final private String LANGUAGE_EN_US="en-US";
    static final private String PAGE="en-US";


    static final private String API_KEY = "api_key";

     //REQUEST EXAMPLE https://api.themoviedb.org/3/movie/popular?api_key=<<api_key>>&language=en-US&page=1
     public static Request buildPopularRequest(){
         HttpUrl url = new HttpUrl.Builder()
                 .scheme(SCHEMA_HTTPS)
                 .host(BASE_URL)
                 .addPathSegment(API_VERSION)
                 .addPathSegment(MOVIE)
                 .addPathSegment(POPULAR)
                 .addQueryParameter(API_KEY, BuildConfig.MOVIE_DB_API_KEY)
                 .addQueryParameter(LANGUAGE,LANGUAGE_EN_US)
                 .addQueryParameter(PAGE,String.valueOf(1))
                 .build();

         Log.d("CLIENT_URL_POPULARITY",url.toString());
         return  new Request.Builder()
                 .url(url)
                 .build();
     }



    //REQUEST EXAMPLE https://api.themoviedb.org/3/movie/top_rated?api_key=<<api_key>>&language=en-US&page=1
    public static Request buildTopRatedRequest(){
        HttpUrl url = new HttpUrl.Builder()
                .scheme(SCHEMA_HTTPS)
                .host(BASE_URL)
                .addPathSegment(API_VERSION)
                .addPathSegment(MOVIE)
                .addPathSegment(TOP_RATED)
                .addQueryParameter(API_KEY, BuildConfig.MOVIE_DB_API_KEY)
                .addQueryParameter(LANGUAGE,LANGUAGE_EN_US)
                .addQueryParameter(PAGE,String.valueOf(1))
                .build();

        Log.d("CLIENT_URL_VOTE",url.toString());
        return  new Request.Builder()
                .url(url)
                .build();
    }

    public static String buildImageUrl(String imagePath){
        HttpUrl url = new HttpUrl.Builder()
                .scheme(SCHEMA_HTTP)
                .host(BASE_URL_IMAGE)
                .addPathSegment("t")
                .addPathSegment("p")
                .addPathSegment(IMAGE_SIZE)
                .addPathSegment("")
                .addPathSegments(imagePath)
                .build();

        Log.d("CLIENT_URL_IMAGE",url.toString());

        return url.toString();

    }


    public static Request buildMovieUrl(int id){
        HttpUrl url = new HttpUrl.Builder()
                .scheme(SCHEMA_HTTP)
                .host(BASE_URL)
                .addPathSegment(API_VERSION)
                .addPathSegment(MOVIE)
                .addPathSegment(String.valueOf(id))
                .addQueryParameter(API_KEY, BuildConfig.MOVIE_DB_API_KEY)
                .build();

        Log.d("CLIENT_URL_MOVIE",url.toString());

        return  new Request.Builder()
                .url(url)
                .build();
    }


    public static Request buildReviewUrl(int id){
        HttpUrl url = new HttpUrl.Builder()
                .scheme(SCHEMA_HTTP)
                .host(BASE_URL)
                .addPathSegment(API_VERSION)
                .addPathSegment(MOVIE)
                .addPathSegment(String.valueOf(id))
                .addPathSegment(REVIEWS)
                .addQueryParameter(API_KEY, BuildConfig.MOVIE_DB_API_KEY)
                .build();

        Log.d("CLIENT_URL_REVIEW",url.toString());

        return  new Request.Builder()
                .url(url)
                .build();
    }

    public static Request buildVideosUrl(int id){
        HttpUrl url = new HttpUrl.Builder()
                .scheme(SCHEMA_HTTP)
                .host(BASE_URL)
                .addPathSegment(API_VERSION)
                .addPathSegment(MOVIE)
                .addPathSegment(String.valueOf(id))
                .addPathSegment(VIDEOS)
                .addQueryParameter(API_KEY, BuildConfig.MOVIE_DB_API_KEY)
                .build();

        Log.d("CLIENT_URL_VIDEOS",url.toString());

        return  new Request.Builder()
                .url(url)
                .build();
    }
}

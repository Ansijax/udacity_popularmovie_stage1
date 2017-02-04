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

    //IMAGE
    static final private String IMAGE_SIZE="w185";

    //QUERY TYPE
    static final private String SORT_OPERATION = "sort_by";
    static final private String SORT_TYPE_POPULARITY_DESC = "popularity.desc";
    static final private String SORT_TYPE_VOTE_DESC = "vote_average.desc";

    static final private String COUNTRY_OPERATION="certification_country";
    static final private String COUNTRY_IT="IT";

    static final private String API_KEY = "api_key";

     //REQUEST EXAMPLE /discover/movie?sort_by=popularity.desc
     public static Request buildPopularRequest(){
         HttpUrl url = new HttpUrl.Builder()
                 .scheme(SCHEMA_HTTPS)
                 .host(BASE_URL)
                 .addPathSegment(API_VERSION)
                 .addPathSegment(DISCOVER)
                 .addPathSegment(MOVIE)
                 .addQueryParameter(API_KEY, BuildConfig.MOVIE_DB_API_KEY)
                 .addQueryParameter(SORT_OPERATION,SORT_TYPE_POPULARITY_DESC)
                 .build();

         Log.d("CLIENT_URL_POPULARITY",url.toString());
         return  new Request.Builder()
                 .url(url)
                 .build();
     }



    //REQUEST EXAMPLE /discover/movie/?certification_country=US&certification=R&sort_by=vote_average.desc
    public static Request buildTopRatedRequest(){
        HttpUrl url = new HttpUrl.Builder()
                .scheme(SCHEMA_HTTPS)
                .host(BASE_URL)
                .addPathSegment(API_VERSION)
                .addPathSegment(DISCOVER)
                .addPathSegment(MOVIE)
                .addQueryParameter(API_KEY, BuildConfig.MOVIE_DB_API_KEY)
                .addQueryParameter(COUNTRY_OPERATION,COUNTRY_IT)
                .addQueryParameter(SORT_OPERATION,SORT_TYPE_VOTE_DESC)
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
}

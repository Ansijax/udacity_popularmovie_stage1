package com.ansijax.udacity.popularmovies.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by massimo on 23/03/17.
 */

public class MovieContract {


    public static final String CONTENT_AUTHORITY="com.ansijax.udacity.popularmovies.popularmovies.MoviesProvider";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    public static final String PATH_MOVIE= "movie";

    public static final class MovieEntry implements BaseColumns{
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIE)
                .build();


        public static String TABLE_NAME="movie";
        public static String TITLE="title";
        public static String MOVIE_ID="movie_id";
        public static String MOVIE_IMG="movie_img";

        public static Uri buildMovieUriWithid(Long id) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(id))
                    .build();
    }



    }

}

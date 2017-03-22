package com.ansijax.udacity.popularmovies.popularmovies.data;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Created by massimo on 21/03/17.
 */

@ContentProvider(authority = MoviesProvider.AUTHORITY, database = FavoriteMoviesDatabase.class,
packageName = "com.ansijax.udacity.popularmovies.popularmovies.provider")
public final class MoviesProvider {

    public static final String AUTHORITY="com.ansijax.udacity.popularmovies.popularmovies.MoviesProvider";


    @TableEndpoint(table = FavoriteMoviesDatabase.MOVIE) public static class FavoriteMovies{

        @ContentUri(
                path = FavoriteMoviesDatabase.MOVIE,
                type = "vnd.android.cursor.dir/movie",
                defaultSort = MovieColumns._ID + " ASC"
        )
        public static final Uri MOVIES = Uri.parse("content://" + AUTHORITY + "/"+FavoriteMoviesDatabase.MOVIE);

        @InexactContentUri(
                path = FavoriteMoviesDatabase.MOVIE + "/#",
                name = "LIST_ID",
                type = "vnd.android.cursor.item/list",
                whereColumn = MovieColumns.MOVIE_ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return Uri.parse("content://" + AUTHORITY + "/movie/" + id);
        }




    }
}

package com.ansijax.udacity.popularmovies.popularmovies.data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by massimo on 21/03/17.
 */

@Database(version = FavoriteMoviesDatabase.VERSION,
        packageName = "com.ansijax.udacity.popularmovies.popularmovies.provider")
public class FavoriteMoviesDatabase {

    public static final int VERSION=1;
    @Table(MovieColumns.class) public static final String MOVIE="movie";
}

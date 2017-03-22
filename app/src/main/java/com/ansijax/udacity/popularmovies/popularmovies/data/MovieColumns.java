package com.ansijax.udacity.popularmovies.popularmovies.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.Unique;

/**
 * Created by massimo on 21/03/17.
 */

public interface MovieColumns {
    @DataType(DataType.Type.INTEGER) @PrimaryKey @AutoIncrement String _ID="_id";
    @DataType(DataType.Type.TEXT) @NotNull String TITLE="title";
    @DataType(DataType.Type.INTEGER) @NotNull @Unique String MOVIE_ID="movie_id";
    @DataType(DataType.Type.BLOB) String MOVIE_IMG="movie_img";
}

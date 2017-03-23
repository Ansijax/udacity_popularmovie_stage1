package com.ansijax.udacity.popularmovies.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


/**
 * Created by massimo on 21/03/17.
 */


public class MoviesProvider extends ContentProvider {

    public static final int CODE_MOVIE = 100;
    public static final int CODE_MOVIE_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mOpenHelper;


    public static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;


        matcher.addURI(authority, MovieContract.PATH_MOVIE, CODE_MOVIE);


        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/#", CODE_MOVIE_WITH_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {

       Cursor cursor;
        switch (sUriMatcher.match(uri)){
            case CODE_MOVIE:
                cursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                break;
            case CODE_MOVIE_WITH_ID:

                String id = uri.getLastPathSegment();
                String[] selectionArguments = new String[]{id} ;
                cursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        null,
                        MovieContract.MovieEntry.MOVIE_ID +" =? ",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);


        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        SQLiteDatabase db =mOpenHelper.getWritableDatabase();

        int operation = sUriMatcher.match(uri);
        Uri returnUri;

        long resultID;
        switch (operation) {

            case CODE_MOVIE:
                resultID=db.insert(MovieContract.MovieEntry.TABLE_NAME,null, contentValues);
                if(resultID > 0){
                    returnUri= ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, resultID);
                    break;
                }
                else {
                    throw  new SQLException("Failed to insert into db :" + uri);
                }


            default:
                throw new UnsupportedOperationException("Unkown Uri: "+ uri);

        }

        getContext().getContentResolver().notifyChange(uri,null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[]selectionArgs) {
        int match = sUriMatcher.match(uri);
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        int rowAffected=-1;
        switch (match){
            case CODE_MOVIE_WITH_ID:
                String[] mSelectionArgs = {uri.getLastPathSegment()};
                rowAffected=db.delete(MovieContract.MovieEntry.TABLE_NAME, MovieContract.MovieEntry.MOVIE_ID+"=?",mSelectionArgs);
                getContext().getContentResolver().notifyChange(uri,null);
                break;
            case CODE_MOVIE:

                rowAffected=db.delete(MovieContract.MovieEntry.TABLE_NAME, selection,selectionArgs);
                getContext().getContentResolver().notifyChange(uri,null);
                break;
            default:
                throw new RuntimeException("Unknown uri:" + uri);


        }

        return rowAffected;

    }



    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}

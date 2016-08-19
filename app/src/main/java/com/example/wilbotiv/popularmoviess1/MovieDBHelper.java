package com.example.wilbotiv.popularmoviess1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by wilbotiv on 7/1/2016.
 */

public class MovieDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "movies.db";
    private static final String LOG_TAG  = MovieDbHelper.class.getSimpleName();

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
//TODO: When onCreate is called and database already exists why does it not crash...
    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + " (" +
                MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieContract.MovieEntry.COLUMN_ORIGINALTITLE + " TEXT UNIQUE ON CONFLICT REPLACE NOT NULL," +
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_RELEASEDATE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_VOTEAVERAGE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_SORT_ORDER + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_POSTERPATH + " TEXT NOT NULL" +
                " );";
        Log.v(LOG_TAG, SQL_CREATE_MOVIE_TABLE);

        final String SQL_CREATE_FAVORITE_TABLE = "CREATE TABLE " + MovieContract.FavoriteEntry.TABLE_NAME + " (" +
                MovieContract.FavoriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieContract.FavoriteEntry.COLUMN_ORIGINALTITLE + " TEXT UNIQUE ON CONFLICT REPLACE NOT NULL," +
                MovieContract.FavoriteEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL , " +
                //table messed up data in the wrong columns.... got to go to bed
                MovieContract.FavoriteEntry.COLUMN_RELEASEDATE + " TEXT NOT NULL, " +
                MovieContract.FavoriteEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MovieContract.FavoriteEntry.COLUMN_VOTEAVERAGE + " TEXT NOT NULL, " +
                MovieContract.FavoriteEntry.COLUMN_POSTERPATH + " TEXT NOT NULL" +
                " );";

        final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE " + MovieContract.ReviewEntry.TABLE_NAME + " (" +
                MovieContract.ReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL, " +
                MovieContract.ReviewEntry.COLUMN_AUTHOR + " TEXT NOT NULL, " +
                MovieContract.ReviewEntry.COLUMN_CONTENT + " TEXT NOT NULL, " +
                MovieContract.ReviewEntry.COLUMN_COMMENT_ID + " TEXT UNIQUE ON CONFLICT REPLACE NOT NULL " +
                ");";

//        TODO:I think that these are not working if already exist.
        db.execSQL(SQL_CREATE_MOVIE_TABLE);
        db.execSQL(SQL_CREATE_FAVORITE_TABLE);
        db.execSQL(SQL_CREATE_REVIEW_TABLE);
    }
}




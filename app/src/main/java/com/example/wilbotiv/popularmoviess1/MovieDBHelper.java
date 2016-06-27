package com.example.wilbotiv.popularmoviess1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by wilbotiv on 6/6/2016.
 */
public class MovieDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "movies.db";

    public MovieDbHelper(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + " (" +
                MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieContract.MovieEntry.COLUMN_ORIGINALTITLE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_ID_KEY + " TEXT UNIQUE NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_RELEASEDATE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_VOTEAVERAGE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_POSTERPATH + " TEXT NOT NULL" + " );";

        db.execSQL(SQL_CREATE_MOVIE_TABLE);
    }
/*
    @Override
    public void onCreate(SQLiteDatabase db) {
        addMovieTable(db);
    }

    *//*private void addMovieTable(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE" + MovieContract.MovieEntry.TABLE_NAME + "(" +
                        MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY, " +
                        MovieContract.MovieEntry.COLUMN_ORIGINALTITLE + " TEXT NOT NULL, " +
                        MovieContract.MovieEntry.COLUMN_RELEASEDATE + " TEXT NOT NULL, " +
                        MovieContract.MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                        MovieContract.MovieEntry.COLUMN_VOTEAVERAGE + " TEXT NOT NULL, " +
                        MovieContract.MovieEntry.COLUMN_POSTERPATH + " TEXT NOT NULL "
        );
    }*/
}

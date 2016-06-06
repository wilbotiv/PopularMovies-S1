package com.example.wilbotiv.popularmoviess1;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by wilbotiv on 6/6/2016.
 */
public class MovieContract {
    public static final String CONTENT_AUTHORITY = "com.example.wilbot.popularmoviess1";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movie";
    public static final String PATH_FAVORITE = "favorite";
//    public static final String PATH_REVIEWS = "movie";

    public static final class MovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir" + CONTENT_URI + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item" + CONTENT_URI + "/" + PATH_MOVIE;

        public static final String TABLE_NAME = "movieTable";

        public static final String COLUMN_POSTERPATH = "moviePosterPath";
        public static final String COLUMN_OVERVIEW = "movieOverview";
        public static final String COLUMN_RELEASEDATE = "movieReleaseDate";
        public static final String COLUMN_ORIGINALTITLE = "movieOriginalTitle";
        public static final String COLUMN_ID = "movieId";
        public static final String COLUMN_VOTEAVERAGE = "movieVoteAverage";

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}


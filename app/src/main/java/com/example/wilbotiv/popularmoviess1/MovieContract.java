package com.example.wilbotiv.popularmoviess1;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by wilbotiv on 6/6/2016.
 */
public class MovieContract {
    public static final String CONTENT_AUTHORITY = "com.example.wilbotiv.popularmoviess1";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movie";
//    public static final String PATH_FAVORITE =     "favorite";
//    public static final String PATH_REVIEWS = "reviews";

    public static final class MovieEntry implements BaseColumns {

        public static final String TABLE_NAME = "movieTable";

        public static final String COLUMN_POSTERPATH = "moviePosterPath";
        public static final String COLUMN_OVERVIEW = "movieOverview";
        public static final String COLUMN_RELEASEDATE = "movieReleaseDate";
        public static final String COLUMN_ORIGINALTITLE = "movieOriginalTitle";
        public static final String COLUMN_ID_KEY = "movieId";
        public static final String COLUMN_VOTEAVERAGE = "movieVoteAverage";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String[] PROJECTION_ALL = {_ID, COLUMN_POSTERPATH, COLUMN_OVERVIEW, COLUMN_RELEASEDATE, COLUMN_ORIGINALTITLE, COLUMN_VOTEAVERAGE};
        public static final String SORT_ORDER_ASC = COLUMN_ORIGINALTITLE + " ASC";

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class MovieReview implements BaseColumns{

        public static final String TABLE_NAME = "reviewTable";

        public static final String COLUMN_REVIEW = "movieReview";
        public static final String COLUMN_ID = "movieId";
    }
}


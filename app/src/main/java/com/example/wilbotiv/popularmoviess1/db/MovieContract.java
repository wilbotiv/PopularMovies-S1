package com.example.wilbotiv.popularmoviess1.db;

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
    public static final String PATH_FAVORITE = "favorite";
    public static final String PATH_REVIEWS = "review";
    public static final String PATH_TRAILERS = "trailer";

    public static final class MovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String TABLE_NAME = "movieTable";

        public static final String COLUMN_POSTERPATH = "moviePosterPath";
        public static final String COLUMN_OVERVIEW = "movieOverview";
        public static final String COLUMN_RELEASEDATE = "movieReleaseDate";
        public static final String COLUMN_ORIGINALTITLE = "movieOriginalTitle";
        public static final String COLUMN_MOVIE_ID = "movieId";
        public static final String COLUMN_VOTEAVERAGE = "movieVoteAverage";
        public static final String COLUMN_SORT_ORDER = "movieSortOrder";

        // For inserting a movie in to the movie table
        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
    //add Favorite table with two columns and update MovieDBHelper
    public static final class FavoriteEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String TABLE_NAME = "favoriteTable";

        public static final String COLUMN_POSTERPATH = "moviePosterPath";
        public static final String COLUMN_OVERVIEW = "movieOverview";
        public static final String COLUMN_RELEASEDATE = "movieReleaseDate";
        public static final String COLUMN_ORIGINALTITLE = "movieOriginalTitle";
        public static final String COLUMN_ID = "movieId";
        public static final String COLUMN_VOTEAVERAGE = "movieVoteAverage";
        public static final String COLUMN_MOVIE_ID = "movieId";

        // For inserting a movie in to the favorite table
        public static Uri buildFavoriteUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    //add Favorite table with two columns and update MovieDBHelper
    public static final class ReviewEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEWS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEWS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEWS;

        public static final String TABLE_NAME = "reviewTable";

        public static final String COLUMN_MOVIE_ID = "movieID";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_COMMENT_ID = "comment_id";

        // For inserting a movie in to the favorite table
        public static Uri buildReviewUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    //add Trailers table with three columns and update MovieDBHelper
    public static final class TrailerEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRAILERS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEWS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEWS;

        public static final String TABLE_NAME = "trailerTable";

        public static final String COLUMN_MOVIE_ID = "id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_SOURCE = "source";
//        public static final String COLUMN_COMMENT_ID = "comment_id";

        // For inserting a movie in to the favorite table
        public static Uri buildReviewUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}


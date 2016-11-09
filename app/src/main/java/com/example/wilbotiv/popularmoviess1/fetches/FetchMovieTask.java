package com.example.wilbotiv.popularmoviess1.fetches;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.wilbotiv.popularmoviess1.BuildConfig;
import com.example.wilbotiv.popularmoviess1.db.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by wilbotiv on 7/5/2016.
 */

// The addLocation method in Sunshine may be worth looking in to as a guide for implementing Favorites
// Done - Stopping here for the night. Tomorrow continue comparing PM to Sunshine fetch tasks.
// Done - Stopping here because Isabelle is about to finish Piano Lesson. Next modify table to include sort column.
// Done: 10/11/2016 FIX In a background thread, app queries the /movie/popular or /movie/top_rated API for the sort criteria specified in the settings menu.
// TODO: 10/13/2016 also try using Volley, OkHttp or Gson libraries for Android networking.
// DONE: 10/13/2016 use the required endpoints


public class FetchMovieTask extends AsyncTask<Void, Void, Void> {
    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();
    private final Context mContext;

    public FetchMovieTask(Context context) {
        mContext = context;
    }
    

    private void getMovieDataFromJson(String moviesJsonStr, String sortOrder) throws JSONException {
        final String RESULTS = "results";
        final String POSTER_PATH = "poster_path";
        final String OVERVIEW = "overview";
        final String RELEASE_DATE = "release_date";
        final String ORIGINAL_TITLE = "original_title";
        final String ID = "id";
        final String VOTE_AVERAGE = "vote_average";

        try {
            JSONObject jsonObject = new JSONObject(moviesJsonStr);
            JSONArray movieArray = jsonObject.getJSONArray(RESULTS);
            Vector<ContentValues> cVVector = new Vector<ContentValues>(movieArray.length());

            for (int i = 0; i < movieArray.length(); i++) {

                String posterPath;
                String overview;
                String releaseDate;
                String originalTitle;
                String movieID;
                String voteAverage;

                JSONObject result = movieArray.getJSONObject(i);

                posterPath = result.getString(POSTER_PATH);
                overview = result.getString(OVERVIEW);
                releaseDate = result.getString(RELEASE_DATE);
                originalTitle = result.getString(ORIGINAL_TITLE);
                movieID = result.getString(ID);
                voteAverage = result.getString(VOTE_AVERAGE);

                ContentValues movieValues = new ContentValues();

                movieValues.put(MovieContract.MovieEntry.COLUMN_POSTERPATH, posterPath);
                movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, overview);
                movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASEDATE, releaseDate);
                movieValues.put(MovieContract.MovieEntry.COLUMN_ORIGINALTITLE, originalTitle);
                movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movieID);
                movieValues.put(MovieContract.MovieEntry.COLUMN_VOTEAVERAGE, voteAverage);
                movieValues.put(MovieContract.MovieEntry.COLUMN_SORT_ORDER, sortOrder);

                cVVector.add(movieValues);
            }


            int inserted = 0;

//Done: Delete all rows in table before fetch? - Did this in updateMovie() method
// "I think not instead do this I used ContentResolver.delete() to delete the records in table.
// I had a ContentProvider implemented so it made sense to use this."

            // add to database
//            found the problem this runs twice the second time it runs it destoys table.....
//            mContext.getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI, null, null);
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = mContext.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);
            }

            Log.d(LOG_TAG, "FetchMovieTask Complete. " + inserted + " Inserted");

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground(Void... params) {

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String moviesJsonStr = null;
        String sortOrder;

//        mContext.getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI, null, null);

        for (int i = 0; i < 2; i++) {
            if (i == 0) {
                sortOrder = "popular";
                String[] mSelectionArgs = {sortOrder};
                mContext.getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI,
                        null,
                        null);
                //                Log.v("SORT_ORDER", sortOrder);

            } else {
                sortOrder = "top_rated";
//                String[] mSelectionArgs = {sortOrder};
//                mContext.getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI,
//                        MovieContract.MovieEntry.COLUMN_SORT_ORDER,
//                        mSelectionArgs);
            }
            try {
                final String BASE_URL = "http://api.themoviedb.org/3/movie";
                final String API_KEY = "api_key";
                final String SORT_ORDER = "sort_by";

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendPath(sortOrder)
                        .appendQueryParameter(API_KEY, BuildConfig.THE_MOVIE_DB_API_KEY).build();
                URL url = new URL(builtUri.toString());
                Log.v(LOG_TAG, "The URL for FetchMovie " + builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJsonStr = buffer.toString();
                Log.v(LOG_TAG, moviesJsonStr);
                getMovieDataFromJson(moviesJsonStr, sortOrder);
            } catch (IOException e) {
                Log.e("MoviesFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("MoviesFragment", "Error closing stream", e);
                    }
                }
            }
        }
        return null;
    }
}


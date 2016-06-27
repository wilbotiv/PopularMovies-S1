package com.example.wilbotiv.popularmoviess1;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.Log;
import android.widget.ArrayAdapter;

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
 * Created by wilbotiv on 6/16/2016.
 */
public class FetchMovieTask extends AsyncTask<Void, Void, Void> {

    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

    private final Context mContext;

    public FetchMovieTask(Context context) {
        mContext = context;
    }

    private Void getMovieDataFromJson(String movieJsonStr)
            throws JSONException {

        final String RESULTS = "results";
        final String POSTER_PATH = "poster_path";
        final String OVERVIEW = "overview";
        final String RELEASE_DATE = "release_date";
        final String ORIGINAL_TITLE = "original_title";
        final String ID = "id";
        final String VOTE_AVERAGE = "vote_average";

        try {
            JSONObject jsonObject = new JSONObject(movieJsonStr);
            JSONArray movieArray = jsonObject.getJSONArray(RESULTS);

            Vector<ContentValues> cVVector = new Vector<ContentValues>(movieArray.length());

            for (int i = 0; i < movieArray.length(); i++) {

                String results;
                String poster_path;
                String overview;
                String release_date;
                String original_title;
                String id;
                String vote_average;

                JSONObject movie = movieArray.getJSONObject(i);

                poster_path = movie.getString(POSTER_PATH);
                overview = movie.getString(OVERVIEW);
                release_date = movie.getString(RELEASE_DATE);
                original_title = movie.getString(ORIGINAL_TITLE);
                id = movie.getString(ID);
                vote_average = movie.getString(VOTE_AVERAGE);


                ContentValues movieValues = new ContentValues();

                movieValues.put(MovieContract.MovieEntry.COLUMN_POSTERPATH, poster_path);
                movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, overview);
                movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASEDATE, release_date);
                movieValues.put(MovieContract.MovieEntry.COLUMN_ORIGINALTITLE, original_title);
                movieValues.put(MovieContract.MovieEntry.COLUMN_ID_KEY, id);
                movieValues.put(MovieContract.MovieEntry.COLUMN_VOTEAVERAGE, vote_average);

                cVVector.add(movieValues);
            }
            int inserted = 0;
            // add to database
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
        return null;
    }

    @Override
    protected Void doInBackground(Void... params) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String moviesJsonStr = null;

        try {
            final String BASE_URL = "http://api.themoviedb.org/3/discover/movie";
            final String API_KEY = "api_key";
            final String SORT_ORDER = "sort_by";
            String sortOrder;
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            sortOrder = prefs.getString("sort_order", "popularity.desc");

            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(SORT_ORDER, sortOrder)
                    .appendQueryParameter(API_KEY, BuildConfig.THE_MOVIE_DB_API_KEY).build();
            URL url = new URL(builtUri.toString());
            Log.v(LOG_TAG, builtUri.toString());

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
            getMovieDataFromJson(moviesJsonStr);
            Log.v(LOG_TAG, moviesJsonStr);
        } catch (IOException e) {
            Log.e("MoviesFragment", "Error ", e);
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
        return null;
    }
}
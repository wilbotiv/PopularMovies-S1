package com.example.wilbotiv.popularmoviess1;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
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
import java.util.ArrayList;

/**
 * Created by wilbotiv on 7/5/2016.
 */
public class FetchMovieTask extends AsyncTask<Void, Void, ArrayList<Movie>> {
    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

    private ArrayAdapter<String> mMovieAdapter;
    private final Context mContext;

    public FetchMovieTask(ArrayAdapter<String> movieAdapter, Context context) {
        mMovieAdapter = movieAdapter;
        mContext = context;
    }
// The addLocation method in Sunshine may be worth looking in to as a guide for implementing Favorites
// Stopping here for the night. Tomorrow continue comparing PM to Sunshine fetch tasks.

    @Override
    protected ArrayList<Movie> doInBackground(Void... params) {

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String moviesJsonStr = null;

        try {
            final String BASE_URL = "http://api.themoviedb.org/3/discover/movie";
            final String API_KEY = "api_key";
            final String SORT_ORDER = "sort_by";
            String sortOrder;
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            sortOrder = prefs.getString("sort_order", "popularity.desc");
//                Log.v("SORT_ORDER", sortOrder);

            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(SORT_ORDER, sortOrder)
                    .appendQueryParameter(API_KEY, BuildConfig.THE_MOVIE_DB_API_KEY).build();
            URL url = new URL(builtUri.toString());
            Log.v(LOG_TAG, builtUri.toString());

            // Create the request to OpenWeatherMap, and open the connection
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
        } catch (IOException e) {
            Log.e("MoviesFragment", "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
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
        try {
            return getMovieDataFromJson(moviesJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<Movie> movies) {
        if (movies != null) {
            mMoviesAdapter.clear();
            for (Movie movie : movies) {
                mMoviesAdapter.add(movie);
            }
        }
    }


    private ArrayList<Movie> getMovieDataFromJson(String moviesJsonStr) throws JSONException {
        final String RESULTS = "results";
        final String POSTER_PATH = "poster_path";
        final String OVERVIEW = "overview";
        final String RELEASE_DATE = "release_date";
        final String ORIGINAL_TITLE = "original_title";
        final String ID = "id";
        final String VOTE_AVERAGE = "vote_average";

        JSONObject jsonObject = new JSONObject(moviesJsonStr);
        JSONArray jsonArray = jsonObject.getJSONArray(RESULTS);

        ArrayList<Movie> movieArrayList = new ArrayList<Movie>(jsonArray.length());

        for (int i = 0; i < jsonArray.length(); i++) {

            JSONObject result = jsonArray.getJSONObject(i);
            Movie movie = new Movie();
            movie.setPosterPath(result.getString(POSTER_PATH));
            movie.setOverview(result.getString(OVERVIEW));
            movie.setReleaseDate(result.getString(RELEASE_DATE));
            movie.setOriginalTitle(result.getString(ORIGINAL_TITLE));
            movie.setId(result.getString(ID));
            movie.setVoteAverage(result.getString(VOTE_AVERAGE));
            movieArrayList.add(movie);

        }
        for (Movie m : movieArrayList) {
            Log.v(LOG_TAG, POSTER_PATH + ": " + m.getPosterPath());
            Log.v(LOG_TAG, OVERVIEW + ": " + m.getOverview());
            Log.v(LOG_TAG, RELEASE_DATE + ": " + m.getReleaseDate());
            Log.v(LOG_TAG, ORIGINAL_TITLE + ": " + m.getOriginalTitle());
            Log.v(LOG_TAG, ID + ": " + m.getId());
            Log.v(LOG_TAG, VOTE_AVERAGE + ": " + m.getVoteAverage());
        }
        return movieArrayList;
    }
}
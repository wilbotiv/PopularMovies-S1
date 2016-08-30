package com.example.wilbotiv.popularmoviess1;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

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


public class FetchTrailerTask extends AsyncTask<String, Void, Void> {
    private final String LOG_TAG = FetchTrailerTask.class.getSimpleName();

    private final Context mContext;

    public FetchTrailerTask(Context context) {
        mContext = context;
    }


    private void getTrailerDataFromJson(String moviesJsonStr) throws JSONException {

//        final String RESULTS = "results";
        final String MOVIE_ID = "id";
        final String YOUTUBE = "youtube";
        final String NAME = "name";
        final String SOURCE = "source";
//        final String AUTHOR = "author";
//        final String CONTENT = "content";
//        final String COMMENT_ID = "id";

        try {
            JSONObject jsonObject = new JSONObject(moviesJsonStr);
            String id = jsonObject.getString(MOVIE_ID);
            JSONArray trailerArray = jsonObject.getJSONArray(YOUTUBE);

            Vector<ContentValues> cVVector = new Vector<ContentValues>(trailerArray.length());

            for (int i = 0; i < trailerArray.length(); i++) {

                String name;
                String source;
//                String commentID;

                JSONObject result = trailerArray.getJSONObject(i);

                name = result.getString(NAME);
                source = result.getString(SOURCE);
//                commentID = result.getString(COMMENT_ID);

                ContentValues trailerValues = new ContentValues();

                trailerValues.put(MovieContract.TrailerEntry.COLUMN_NAME, name);
                trailerValues.put(MovieContract.TrailerEntry.COLUMN_SOURCE, source);
                trailerValues.put(MovieContract.TrailerEntry.COLUMN_MOVIE_ID, id);
//                trailerValues.put(MovieContract.ReviewEntry.COLUMN_COMMENT_ID, commentID);

                cVVector.add(trailerValues);
            }

            int inserted = 0;

            //Done: Delete all rows in table before fetch? - Don't think that this is really necessary.
            // "I think not instead do this I used ContentResolver.delete() to delete the records in table.
            // I had a ContentProvider implemented so it made sense to use this."

//            mContext.getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI, null, null);

            // add to database
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
//              Done: Fix this.
                inserted = mContext.getContentResolver().bulkInsert(MovieContract.TrailerEntry.CONTENT_URI, cvArray);
            }

            Log.d(LOG_TAG, "FetchTrailerTask Complete. " + inserted + " Inserted");
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

    }

    @Override
    protected Void doInBackground(String... movieID) {

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String reviewsJsonStr = null;

            try {
                final String BASE_URL = "http://api.themoviedb.org/3/movie";
                final String API_KEY = "api_key";

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendPath(movieID[0])
                        .appendPath("trailers")
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
                reviewsJsonStr = buffer.toString();
                Log.v(LOG_TAG, reviewsJsonStr);
                getTrailerDataFromJson(reviewsJsonStr);
            } catch (IOException e) {
                Log.e("MoviesFragment", "Error ", e);
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

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}

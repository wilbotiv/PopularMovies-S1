package com.example.wilbotiv.popularmoviess1;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
        private String mMovieString;

        private static final String LOG_TAG = DetailFragment.class.getSimpleName();

        private static final String MOVIE_SHARE_HASHTAG = " #PopularMovies";

        private ShareActionProvider mShareActionProvider;
//        private String mForecast;

        private static final int DETAIL_LOADER = 0;

        private static final String[] MOVIE_COLUMNS = {
                MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
                MovieContract.MovieEntry.COLUMN_POSTERPATH,
                MovieContract.MovieEntry.COLUMN_ORIGINALTITLE,
                MovieContract.MovieEntry.COLUMN_RELEASEDATE,
                MovieContract.MovieEntry.COLUMN_OVERVIEW,
                MovieContract.MovieEntry.COLUMN_VOTEAVERAGE,
                MovieContract.MovieEntry.COLUMN_ID_KEY,
        };

        // these constants correspond to the projection defined above, and must change if the
        // projection changes
        private static final int COL_MOVIE_ID = 0;
        private static final int COL_POSTERPATH = 1;
        private static final int COL_ORIGINALTITLE = 2;
        private static final int COL_RELEASEDATE = 3;
        private static final int COL_OVERVIEW = 4;
        private static final int COL_VOTEAVERAGE = 5;
        private static final int COL_ID_KEY = 6;

        public DetailFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            return rootView;

        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            // Inflate the menu; this adds items to the action bar if it is present.
            inflater.inflate(R.menu.detailfragment, menu);

            // Retrieve the share menu item
            MenuItem menuItem = menu.findItem(R.id.action_share);

            // Get the provider and hold onto it to set/change the share intent.
            mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

            // If onLoadFinished happens before this, we can go ahead and set the share intent now.
            if (mMovieString != null) {
                mShareActionProvider.setShareIntent(createShareMovieIntent());
            }
        }

        private Intent createShareMovieIntent() {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, mMovieString + MOVIE_SHARE_HASHTAG);
            return shareIntent;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            getLoaderManager().initLoader(DETAIL_LOADER, null, this);
            super.onActivityCreated(savedInstanceState);
        }


        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Log.v(LOG_TAG, "In onCreateLoader");
            Intent intent = getActivity().getIntent();
            if (intent == null) {
                return null;
            }

            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    intent.getData(),
                    MOVIE_COLUMNS,
                    null,
                    null,
                    null
            );
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            Log.v(LOG_TAG, "In onLoadFinished");
            if (!data.moveToFirst()) { return; }

//            String dateString = Utility.formatDate(
//                    data.getLong(COL_WEATHER_DATE));

            String posterPath = data.getString(COL_POSTERPATH);
            String originalTitle = data.getString(COL_ORIGINALTITLE);
            String releaseDate = data.getString(COL_RELEASEDATE);
            String overview = data.getString(COL_OVERVIEW);
            String voteAverage = data.getString(COL_VOTEAVERAGE);
            String id = data.getString(COL_ID_KEY);

            TextView textViewOriginalTitle = (TextView) getView().findViewById(R.id.fragment_detail_textView_originalTitle);
            ImageView imageView = (ImageView) getView().findViewById(R.id.fragment_detail_imageView_poster);
            TextView textViewReleaseDate = (TextView) getView().findViewById(R.id.fragment_detail_textView_releaseDate);
            TextView textViewVoteAverage = (TextView) getView().findViewById(R.id.fragment_detail_textView_voteAverage);
            TextView textViewOverview = (TextView) getView().findViewById(R.id.fragment_detail_textView_overview);

            textViewOriginalTitle.setText(originalTitle);
            textViewReleaseDate.setText(releaseDate);
            textViewVoteAverage.setText(voteAverage);
            textViewOverview.setText(overview);

            Picasso.with(getContext()).load("http://image.tmdb.org/t/p/w500" +
                    posterPath).into(imageView);


//
//            boolean isMetric = Utility.isMetric(getActivity());
//
//            String high = Utility.formatTemperature(
//                    data.getDouble(COL_WEATHER_MAX_TEMP), isMetric);
//
//            String low = Utility.formatTemperature(
//                    data.getDouble(COL_WEATHER_MIN_TEMP), isMetric);
//
//            mForecast = String.format("%s - %s - %s/%s", dateString, weatherDescription, high, low);

//            TextView detailTextView = (TextView)getView().findViewById(R.id.detail_text);
//            detailTextView.setText(mForecast);

            // If onCreateOptionsMenu has already happened, we need to update the share intent now.
            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createShareMovieIntent());
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }
}

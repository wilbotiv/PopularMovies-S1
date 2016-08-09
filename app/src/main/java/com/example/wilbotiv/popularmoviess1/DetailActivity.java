package com.example.wilbotiv.popularmoviess1;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
//FIXED: DetailActivity does not scroll.....
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

        private static final int DETAIL_LOADER = 0;
        private static final String LOG_TAG = DetailActivity.class.getSimpleName();
        private static final String MOVIE_SHARE_HASHTAG = " #PopularMoviesS1";
        private ShareActionProvider mShareActionProvider;
//FIXED: mMovie needs to have originalTitle
        private String mMovie;

        private static final String[] MOVIE_COLUMNS = {
                MovieContract.MovieEntry._ID,
                MovieContract.MovieEntry.COLUMN_POSTERPATH,
                MovieContract.MovieEntry.COLUMN_ORIGINALTITLE,
                MovieContract.MovieEntry.COLUMN_VOTEAVERAGE,
                MovieContract.MovieEntry.COLUMN_OVERVIEW
        };

        private static final int _ID = 0;
        private static final int COL_POSTER_PATH = 1;
        private static final int COL_ORIGINAL_TITLE = 2;
        private static final int COL_VOTE_AVERAAGE = 3;
        private static final int COL_OVERVIEW = 4;

//        Uri detailUri;

        public DetailFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
//            Intent intent = getActivity().getIntent();
//            if (intent != null) {
//                mUri = intent.getDataString();
//                detailUri = Uri.parse(mUri);
//            }
//            Movie movie = intent.getParcelableExtra(MoviesFragment.PAR_KEY);
//            TextView textViewOriginalTitle = (TextView) rootView.findViewById(R.id.fragment_detail_textView_originalTitle);
//            ImageView imageView = (ImageView) rootView.findViewById(R.id.fragment_detail_imageView_poster);
//            TextView textViewReleaseDate = (TextView) rootView.findViewById(R.id.fragment_detail_textView_releaseDate);
//            TextView textViewVoteAverage = (TextView) rootView.findViewById(R.id.fragment_detail_textView_voteAverage);
//            TextView textViewOverview = (TextView) rootView.findViewById(R.id.fragment_detail_textView_overview);
//
//            textViewOriginalTitle.setText(mUri);
//            textViewReleaseDate.setText(movie.getReleaseDate());
//            textViewVoteAverage.setText(movie.getVoteAverage());
//            textViewOverview.setText(movie.getOverview());

//            Picasso.with(getContext()).load("http://image.tmdb.org/t/p/w500" + movie.getPosterPath()).into(imageView);
            return rootView;
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.detailfragment, menu);

            MenuItem menuItem = menu.findItem(R.id.action_share);

            mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

//            Working on share intent need to cook dinner...
            if (mMovie != null) {
                mShareActionProvider.setShareIntent(createShareMovieIntent());
            }
        }

        //FIXED: Share intent does not work
        private Intent createShareMovieIntent() {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            shareIntent.setType("text/plain");
//            shareIntent.putExtra(Intent.EXTRA_TEXT, mMovie + MOVIE_SHARE_HASHTAG);
           //FIXED: Add Original Title here.
            shareIntent.putExtra(Intent.EXTRA_TEXT, mMovie + MOVIE_SHARE_HASHTAG);
            return shareIntent;
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            getLoaderManager().initLoader(DETAIL_LOADER, null, this);
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

            String posterPath = data.getString(COL_POSTER_PATH);
            String originalTitle = data.getString(COL_ORIGINAL_TITLE);
            String voteAverage = data.getString(COL_VOTE_AVERAAGE);
            String overview = data.getString(COL_OVERVIEW);

            mMovie = originalTitle;

            TextView textViewOriginalTitle = (TextView) getView().findViewById(R.id.fragment_detail_textView_originalTitle);
            ImageView imageView = (ImageView) getView().findViewById(R.id.fragment_detail_imageView_poster);
//            TextView textViewReleaseDate = (TextView) rootView.findViewById(R.id.fragment_detail_textView_releaseDate);
            TextView textViewVoteAverage = (TextView) getView().findViewById(R.id.fragment_detail_textView_voteAverage);
            TextView textViewOverview = (TextView) getView().findViewById(R.id.fragment_detail_textView_overview);
//
            textViewOriginalTitle.setText(originalTitle);
//            textViewReleaseDate.setText(movie.getReleaseDate());
            textViewVoteAverage.setText(voteAverage);
            textViewOverview.setText(overview);

            Picasso.with(getContext()).load("http://image.tmdb.org/t/p/w500" + posterPath).into(imageView);

            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createShareMovieIntent());
            }

        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }

    }

}

package com.example.wilbotiv.popularmoviess1;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

//FIXED: DetailActivity does not scroll.....
//TODO: Add views programmatically or ListView...
//Todo: Let's call the API Review up front
//TODO: Multiple loaders.
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

        private static final int DETAIL_LOADER_HEADER = 0;
        private static final int DETAIL_LOADER_REVIEWS = 1;
        private static final String LOG_TAG = DetailActivity.class.getSimpleName();
        private static final String MOVIE_SHARE_HASHTAG = " #PopularMoviesS1";
        private ShareActionProvider mShareActionProvider;
        //FIXED: mMovie needs to have originalTitle
        private String mMovie;

        String posterPath;
        String originalTitle;
        String releaseDate;
        String overview;
        String movieID;
        String voteAverage;


        private static final String[] MOVIE_COLUMNS = {
//                MovieContract.MovieEntry._ID,
                MovieContract.MovieEntry.COLUMN_POSTERPATH,
                MovieContract.MovieEntry.COLUMN_ORIGINALTITLE,
                MovieContract.MovieEntry.COLUMN_RELEASEDATE,
                MovieContract.MovieEntry.COLUMN_OVERVIEW,
                MovieContract.MovieEntry.COLUMN_MOVIE_ID,
                MovieContract.MovieEntry.COLUMN_VOTEAVERAGE,
        };

        //        private static final int _ID = 0;
        private static final int COL_POSTER_PATH = 0;
        private static final int COL_ORIGINAL_TITLE = 1;
        private static final int COL_REALEASEDATE = 2;
        private static final int COL_OVERVIEW = 3;
        private static final int COL_MOVIE_ID = 4;
        private static final int COL_VOTE_AVERAAGE = 5;

//        Uri detailUri;

        public DetailFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            Button button = (Button) rootView.findViewById(R.id.fragment_detail_button);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    ContentValues movieValues = new ContentValues();

                    movieValues.put(MovieContract.FavoriteEntry.COLUMN_POSTERPATH, posterPath);
                    movieValues.put(MovieContract.FavoriteEntry.COLUMN_ORIGINALTITLE, originalTitle);
                    movieValues.put(MovieContract.FavoriteEntry.COLUMN_RELEASEDATE, releaseDate);
                    movieValues.put(MovieContract.FavoriteEntry.COLUMN_OVERVIEW, overview);
                    movieValues.put(MovieContract.FavoriteEntry.COLUMN_MOVIE_ID, movieID);
                    movieValues.put(MovieContract.FavoriteEntry.COLUMN_VOTEAVERAGE, voteAverage);

                    ContentResolver resolver = getContext().getContentResolver();
                    resolver.insert(MovieContract.FavoriteEntry.CONTENT_URI, movieValues);

                    Toast toast = Toast.makeText(getContext(), "'" + originalTitle + "' has been added to your Favorites", Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
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
//            TODO:Init another loader here?
            getLoaderManager().initLoader(DETAIL_LOADER_HEADER, null, this);
//            getLoaderManager().initLoader(DETAIL_LOADER_REVIEWS, null, this);
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Log.v(LOG_TAG, "In onCreateLoader");
            Intent intent = getActivity().getIntent();
            if (intent == null) {
                return null;
            }
            int loaderID = id;
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            switch (loaderID) {
                case 0:
                    Log.v(LOG_TAG, "In onCreateLoader case 0");
                    return new CursorLoader(
                            getActivity(),
                            intent.getData(),
                            MOVIE_COLUMNS,
                            null,
                            null,
                            null
                    );
                case 1:
//                    Uri uri = MovieContract.ReviewEntry.buildReviewUri(140607);
//                    TODO: You cant use movieID, because it may be null. Get movieID from intent?
//                    Uri uri = intent.getData();
//                    String movieIDFromPath = uri.getLastPathSegment();
                    Log.v(LOG_TAG, "In onCreateLoader case 1");
                    return new CursorLoader(
                            getActivity(),
                            MovieContract.ReviewEntry.CONTENT_URI,
                            null,
                            MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " =" + movieID,
                            null,
                            null
                    );
            }
            return null;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//            Log.v(LOG_TAG, "In onLoadFinished");

            int loaderID = loader.getId();
            Log.v(LOG_TAG, "In onLoadFinished " + "Loader ID is " + loaderID);

            if (!data.moveToFirst()) {
                return;
            }
//DONE: maybe make these member variables......

            switch (loaderID) {
                case 0:
                    Log.v(LOG_TAG, "In onLoadFinished case 0");

                    posterPath = data.getString(COL_POSTER_PATH);
                    originalTitle = data.getString(COL_ORIGINAL_TITLE);
                    releaseDate = data.getString(COL_REALEASEDATE);
                    overview = data.getString(COL_OVERVIEW);
                    movieID = data.getString(COL_MOVIE_ID);
                    voteAverage = data.getString(COL_VOTE_AVERAAGE);

                    mMovie = originalTitle;

                    TextView textViewOriginalTitle = (TextView) getView().findViewById(R.id.fragment_detail_textView_originalTitle);
                    ImageView imageView = (ImageView) getView().findViewById(R.id.fragment_detail_imageView_poster);
                    TextView textViewReleaseDate = (TextView) getView().findViewById(R.id.fragment_detail_textView_releaseDate);
                    TextView textViewVoteAverage = (TextView) getView().findViewById(R.id.fragment_detail_textView_voteAverage);
                    TextView textViewMovieID= (TextView) getView().findViewById(R.id.fragment_detail_textView_movieID);
                    TextView textViewOverview = (TextView) getView().findViewById(R.id.fragment_detail_textView_overview);
//
                    textViewOriginalTitle.setText(originalTitle);
                    textViewReleaseDate.setText(releaseDate);
                    textViewVoteAverage.setText(voteAverage);
                    textViewMovieID.setText(movieID);
                    textViewOverview.setText(overview);

                    Picasso.with(getContext()).load("http://image.tmdb.org/t/p/w500" + posterPath).into(imageView);
//TODO: updateMoviewReview() causes onLoadFinished() to run multiple times.
                    updateMovieReview();
                    getLoaderManager().initLoader(DETAIL_LOADER_REVIEWS, null, this);
                    break;
                case 1:
                    Log.v(LOG_TAG, "In onLoadFinished case 1");
//                    TODO: Create some views.
                    String reviewerName = data.getString(2);
                    String content = data.getString(3);
                    Log.v(LOG_TAG, " " + reviewerName + " " + content);

/*
                    for (data.moveToFirst(); !data.isAfterLast(); data.moveToNext()) {

                    }
*/
                        break;
            }
            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createShareMovieIntent());
            }

//            updateMovieReview();
        }

        private void updateMovieReview() {
//            FetchMovieTask movieTask = new FetchMovieTask(getActivity());
            FetchReviewTask fetchReviewTask = new FetchReviewTask(getContext());
            Log.v(LOG_TAG, "updateMovieReview() called");
//            movieTask.execute();
            fetchReviewTask.execute(movieID);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }


    }

}

package com.example.wilbotiv.popularmoviess1;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.media.RatingCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;


//DONE: DetailActivity does not scroll.....
//DONE: Add views programmatically or ListView...
//DONE: Let's call the API Review up front
//DONE: Multiple loaders.
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
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

        private static final int DETAIL_LOADER_HEADER = 0;
        private static final int DETAIL_LOADER_REVIEWS = 1;
        private static final int DETAIL_LOADER_TRAILERS = 2;


        private static final String LOG_TAG = DetailActivity.class.getSimpleName();
        private static final String MOVIE_SHARE_HASHTAG = " #PopularMoviesS1";
        private ShareActionProvider mShareActionProvider;
        //FIXED: mMovie needs to have originalTitle
        private String mMovie;
        private final String INTENT_KEY = "com.example.wilbotiv.popularmoviess1.movieID";

        String posterPath;
        String originalTitle;
        String releaseDate;
        String overview;
        String movieID;
        String voteAverage;


        private static final String[] MOVIE_COLUMNS = {
                MovieContract.MovieEntry.COLUMN_POSTERPATH,
                MovieContract.MovieEntry.COLUMN_ORIGINALTITLE,
                MovieContract.MovieEntry.COLUMN_RELEASEDATE,
                MovieContract.MovieEntry.COLUMN_OVERVIEW,
                MovieContract.MovieEntry.COLUMN_MOVIE_ID,
                MovieContract.MovieEntry.COLUMN_VOTEAVERAGE,
        };

        private static final int COL_POSTER_PATH = 0;
        private static final int COL_ORIGINAL_TITLE = 1;
        private static final int COL_REALEASEDATE = 2;
        private static final int COL_OVERVIEW = 3;
        private static final int COL_MOVIE_ID = 4;
        private static final int COL_VOTE_AVERAAGE = 5;

//// DONE: 8/28/2016 Projection and index for case 1

        private static final String[] REVIEWS_COLUMNS = {
            MovieContract.ReviewEntry.COLUMN_AUTHOR,
            MovieContract.ReviewEntry.COLUMN_CONTENT
        };

        private static final int COL_AUTHOR = 0;
        private static final int COL_CONTENT = 1;

        private static final String[] TRAILER_COLUMNS = {
                MovieContract.TrailerEntry.COLUMN_NAME,
                MovieContract.TrailerEntry.COLUMN_SOURCE
        };

        private static final int COL_NAME = 0;
        private static final int COL_SOURCE = 1;


        public DetailFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public void onResume() {
            super.onResume();
            getLoaderManager().initLoader(DETAIL_LOADER_HEADER, null, this);
            getLoaderManager().initLoader(DETAIL_LOADER_REVIEWS, null, this);
            getLoaderManager().initLoader(DETAIL_LOADER_TRAILERS, null, this);
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

            if (mMovie != null) {
                mShareActionProvider.setShareIntent(createShareMovieIntent());
            }
        }

        //FIXED: Share intent does not work
        private Intent createShareMovieIntent() {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            shareIntent.setType("text/plain");
            //FIXED: Add Original Title here.
            shareIntent.putExtra(Intent.EXTRA_TEXT, mMovie + MOVIE_SHARE_HASHTAG);
            return shareIntent;
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
//            DONE:Init another loader here? - No.
            Log.v(LOG_TAG, "In onActivityCreated");
            updateMovieReview();
            updateMovieTrailer();
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Log.v(LOG_TAG, "In onCreateLoader");
            Intent intent = getActivity().getIntent();
            String intentExtra = intent.getStringExtra(INTENT_KEY);
            if (intent == null) {
                return null;
            }
            int loaderID = id;

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
//                    DONE: You cant use movieID, because it may be null. Get movieID from intent?
                    Log.v(LOG_TAG, "In onCreateLoader case 1");
                    Log.v(LOG_TAG, "At intent extra " + intentExtra);
                    return new CursorLoader(
                            getActivity(),
                            MovieContract.ReviewEntry.CONTENT_URI,
                            REVIEWS_COLUMNS,
                            MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " =" + intentExtra,
                            null,
                            null
                    );

                case 2:
//                    DONE: You cant use movieID, because it may be null. Get movieID from intent?
                    Log.v(LOG_TAG, "In onCreateLoader case 2");
                    Log.v(LOG_TAG, "At intent extra " + intentExtra);
                    return new CursorLoader(
                            getActivity(),
                            MovieContract.TrailerEntry.CONTENT_URI,
                            TRAILER_COLUMNS,
                            MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " =" + intentExtra,
                            null,
                            null
                    );
            }
            return null;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            int loaderID = loader.getId();
            Log.v(LOG_TAG, "In onLoadFinished ");
            Context context = getContext();
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
                    TextView textViewMovieID = (TextView) getView().findViewById(R.id.fragment_detail_textView_movieID);
                    TextView textViewOverview = (TextView) getView().findViewById(R.id.fragment_detail_textView_overview);
//
                    textViewOriginalTitle.setText(originalTitle);
                    textViewReleaseDate.setText(releaseDate);
                    textViewVoteAverage.setText(voteAverage);
                    textViewMovieID.setText(movieID);
                    textViewOverview.setText(overview);

                    Picasso.with(context).load("http://image.tmdb.org/t/p/w500" + posterPath).into(imageView);
//DONE: updateMoviewReview() causes onLoadFinished() to run multiple times.
                    break;

                case 1:
                    Log.v(LOG_TAG, "In onLoadFinished case 1");
//                    DONE: Create some views.
//                    DONE: Change index in cursor to FINAL variable

//                    Using removeAllViews because onLoadFinished is being called twice even though I have
//                    it init'ing cursorLoader in onResume(). I think because my fetchMovieReview (AsyncTask) is not
//                    completed when onLoadFinish runs query??? Anyway this seems to work.

//                    DONE: This might be a problem with trailers...
                    LinearLayout linearLayout = (LinearLayout) getView().findViewById(R.id.fragment_detail_linearLayout_review);
                    linearLayout.removeAllViews();

                    for (data.moveToFirst(); !data.isAfterLast(); data.moveToNext()) {
                        String reviewerName = data.getString(COL_AUTHOR);
                        String content = data.getString(COL_CONTENT);
                        Log.v(LOG_TAG, " " + reviewerName + " " + content);
//DONE: use member variable context
                        TextView textViewReviewerName = new TextView(context);
                        TextView textViewContent = new TextView(context);

                        textViewReviewerName.setText(reviewerName);
//                        DONE: Use r.demension instead of hard code
                        textViewReviewerName.setTextSize(getResources().getDimension(R.dimen.text_size_reviewer));
                        textViewReviewerName.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT));

                        textViewContent.setText(content);
                        textViewContent.setTextSize(getResources().getDimension(R.dimen.text_size_review));
                        textViewContent.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT));

                        linearLayout.addView(textViewReviewerName);
                        linearLayout.addView(textViewContent);
//                      DONE: I don't need count anymore, right?
//DONE: Details page listing reviews twice....
                    }
                    break;

                case 2:
                    Log.v(LOG_TAG, "In onLoadFinished case 2");
                    LinearLayout linearLayoutTrailer = (LinearLayout) getView().findViewById(R.id.fragment_detail_linearLayout_trailer);
                    linearLayoutTrailer.removeAllViews();

                    for (data.moveToFirst(); !data.isAfterLast(); data.moveToNext()) {
                        final String name = data.getString(COL_NAME);
                        final String source = data.getString(COL_SOURCE);

                        Log.v(LOG_TAG, "In onLoadFinished case 2 " + name + " " + source);

                        Button trailerButton = new Button(getContext());
//                        TextView textViewTrailerName = new TextView(getContext());
// DONE: 8/26/2016 use string resource

                        trailerButton.setText(name);
                        trailerButton.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT));
                        trailerButton.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                Toast.makeText(getContext(),name,Toast.LENGTH_SHORT).show();
                                Log.v(LOG_TAG, "In on click");
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + source)));
                            }
                        });

//                        textViewTrailerName.setText(name);
//                        DONE: Use r.demension instead of hard code
//                        textViewTrailerName.setTextSize(25);
//                        textViewTrailerName.setLayoutParams(new LinearLayout.LayoutParams(
//                                LinearLayout.LayoutParams.MATCH_PARENT,
//                                LinearLayout.LayoutParams.MATCH_PARENT));

//                        linearLayoutTrailer.addView(textViewTrailerName);
                        linearLayoutTrailer.addView(trailerButton);
                    }
                    break;
            }
            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createShareMovieIntent());
            }
        }

        private void updateMovieReview() {
            Intent intent = getActivity().getIntent();
            String intentExtra = intent.getStringExtra(INTENT_KEY);
            FetchReviewTask fetchReviewTask = new FetchReviewTask(getContext());
            Log.v(LOG_TAG, "In updateMovieReview");
            fetchReviewTask.execute(intentExtra);
        }

        private void updateMovieTrailer() {
            Intent intent = getActivity().getIntent();
            String intentExtra = intent.getStringExtra(INTENT_KEY);
            FetchTrailerTask fetchTrailerTask = new FetchTrailerTask(getContext());
            Log.v(LOG_TAG, "In updateMovieTrailer");
            fetchTrailerTask.execute(intentExtra);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }


    }

}

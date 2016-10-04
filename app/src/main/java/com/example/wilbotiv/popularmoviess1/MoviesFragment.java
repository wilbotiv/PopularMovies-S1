package com.example.wilbotiv.popularmoviess1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

public class MoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    private final String LOG_TAG = MoviesFragment.class.getSimpleName();
    private MoviesAdapter mMoviesAdapter;
    private final String INTENT_KEY = "com.example.wilbotiv.popularmoviess1.movieID";

    private static final String movieColumns[] = {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_POSTERPATH,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID
    };

    static final int COL_ID = 0;
    static final int COL_MOVIE_POSTER_PATH = 1;
    static final int COL_MOVIE_ID = 2;

    private static final int MOVIE_LOADER = 0;

    public MoviesFragment() {
    }

    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri movieUri);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.moviesfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
//            Done: Do I really need to fetch... - Yes, I want to refresh here.
            updateMovie();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mMoviesAdapter = new MoviesAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movies);
        gridView.setAdapter(mMoviesAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long l) {
//                String movies = mMoviesAdapter.getItem(position).posterPath;
//                Toast.makeText(getActivity(), movies, Toast.LENGTH_SHORT).show();
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                int id = cursor.getInt(COL_ID);
                String movieID = cursor.getString(COL_MOVIE_ID);
                if (cursor != null) {
//                  DONE: setData will need to be changed here to support Favorites detail view....
                    // DONE: 9/22/2016 change the uri
                    ((Callback) getActivity()).onItemSelected(MovieContract.MovieEntry.buildMovieUri(Integer.parseInt(movieID)));

//                    Intent intent = new Intent(getActivity(), DetailActivity.class)
//                            .setData(MovieContract.MovieEntry.buildMovieUri(id));
//                    intent.putExtra(INTENT_KEY, movieID);
//                    startActivity(intent);

                }

                /*Intent intent = new Intent(getActivity(), DetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable(PAR_KEY, movies.get(position));
                intent.putExtras(bundle);*/
//                startActivity(intent);
            }
        });
        return rootView;
    }

    private void updateMovie() {
        getActivity().getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI, null, null);
        FetchMovieTask movieTask = new FetchMovieTask(getActivity());
//        FetchReviewTask reviewTask = new FetchReviewTask(getContext());
//        String location = Utility.getPreferredLocation(getActivity());
        Log.v(LOG_TAG, "updateMovie() called");
        movieTask.execute();
//      DONE: Start here this AsyncTask is working
//        reviewTask.execute("140607");
    }

    @Override
    public void onStart() {
        super.onStart();
//        DONE: Check Sunshine, when does it fetch...
        updateMovie();
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

//FIXED: Sort order code goes here? Look at this link
//https://discussions.udacity.com/t/loader-not-displaying-the-data-on-ui/180237
//FIXED: Refresh adapter after preference change
//https://discussions.udacity.com/t/change-sort-order-preference-wont-trigger-a-screen-refresh/44306/2
//FIXED: GRIDVIEW Poster scaling not right
//FIXED: GRIDVIEW Padding not right
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String sortOrder = sharedPreferences.getString(getString(R.string.key_prefs_general), "0");
        int sortOrderValueToInt = Integer.parseInt(sortOrder);
        Uri movie = null;
        String sort = null;
//        DONE: change sortOrder field to 1 or 0 instead of string vote_average.desc
        String selection = null;
        if (sortOrderValueToInt == 0) {
            movie = MovieContract.MovieEntry.CONTENT_URI;
//            sort = com.example.wilbotiv.popularmoviess1.MovieContract.MovieEntry.COLUMN_SORT_ORDER + " " + "ASC";
            selection = MovieContract.MovieEntry.COLUMN_SORT_ORDER + " = 'popularity.desc'";
        } else if (sortOrderValueToInt == 1) {
            movie = MovieContract.MovieEntry.CONTENT_URI;
//            sort = com.example.wilbotiv.popularmoviess1.MovieContract.MovieEntry.COLUMN_SORT_ORDER + " " + "DESC";
            selection = MovieContract.MovieEntry.COLUMN_SORT_ORDER + " = 'vote_average.desc'";
        } else if (sortOrderValueToInt == 2) {
            movie = MovieContract.FavoriteEntry.CONTENT_URI;
//            sort = com.example.wilbotiv.popularmoviess1.MovieContract.FavoriteEntry.COLUMN_ORIGINALTITLE + " " + "ASC";
        }
//        Need to read pref file then if else if a URI.. Girls back from ballet need to get dinner ready....

//What if changed sortOrder to int then switch to one of three different loaders, branch first...
        switch (id) {
            case MOVIE_LOADER:
//                Uri movie = com.example.wilbotiv.popularmoviess1.MovieContract.FavoriteEntry.CONTENT_URI;
//                        movie = com.example.wilbotiv.popularmoviess1.MovieContract.MovieEntry.CONTENT_URI;
                return new CursorLoader(getActivity(),
                        movie,
                        movieColumns,
                        selection,
                        null,
                        null);
//                        com.example.wilbotiv.popularmoviess1.MovieContract.MovieEntry.COLUMN_SORT_ORDER + " " + sortOrder);

//FIXED: Don't know why the above sort order now breaks app on new install. Mysteriously just started working....
//                return new CursorLoader(getActivity(), movie, null, null, null, null);

        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMoviesAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMoviesAdapter.swapCursor(null);
    }
}
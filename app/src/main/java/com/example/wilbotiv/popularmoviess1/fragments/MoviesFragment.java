package com.example.wilbotiv.popularmoviess1.fragments;

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

import com.example.wilbotiv.popularmoviess1.fetches.FetchMovieTask;
import com.example.wilbotiv.popularmoviess1.db.MovieContract;
import com.example.wilbotiv.popularmoviess1.adapters.MoviesAdapter;
import com.example.wilbotiv.popularmoviess1.R;

// TODO: 10/13/2016 no need to make a network call again when coming back from settings or detail if movie list exists.

public class MoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    private final String LOG_TAG = MoviesFragment.class.getSimpleName();
    private MoviesAdapter mMoviesAdapter;
    private boolean mSavedInstanceState;
    private GridView mGridView;

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
        if (savedInstanceState == null) {
            mSavedInstanceState = false;
        }
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

        mGridView = (GridView) rootView.findViewById(R.id.gridview_movies);

        mGridView.setAdapter(mMoviesAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                int id = cursor.getInt(COL_ID);
                String movieID = cursor.getString(COL_MOVIE_ID);
                if (cursor != null) {
//                  DONE: setData will need to be changed here to support Favorites detail view....
                    // DONE: 9/22/2016 change the uri
                    ((Callback) getActivity()).onItemSelected(MovieContract.MovieEntry.buildMovieUri(Integer.parseInt(movieID)));
                }
            }
        });
        return rootView;
    }

    private void updateMovie() {
        getActivity().getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI, null, null);
        FetchMovieTask movieTask = new FetchMovieTask(getActivity());
        Log.v(LOG_TAG, "updateMovie() called");
        movieTask.execute();
    }


    /*So you have to make some changes to your current implementation so that the movies list fetching
    task is kicked off only if the sort order has changed or the movies list is empty (the Internet connection
    might be poor so the data might fail to be fetched or when the app is initially launched):

Dear Reviewer : ) It was suggested that I check two conditions before calling updateMovie().
1. Check first to see if movie list is empty.
2. Check to see if sort order is changed.

It appears that I don't need to check the sort order because my loader does this work against the database. So I don't think that I need to check that.
However, I could be wrong :)

*/
    @Override
    public void onStart() {
        super.onStart();
        if (mMoviesAdapter.isEmpty()) {
            Log.v(LOG_TAG, "In onStart and adapter is empty");
            updateMovie();
        }
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
//DONE: change sortOrder field to 1 or 0 instead of string vote_average.desc
        String selection = null;
        if (sortOrderValueToInt == 0) {
            movie = MovieContract.MovieEntry.CONTENT_URI;
            selection = MovieContract.MovieEntry.COLUMN_SORT_ORDER + " = 'popular'";
        } else if (sortOrderValueToInt == 1) {
            movie = MovieContract.MovieEntry.CONTENT_URI;
            selection = MovieContract.MovieEntry.COLUMN_SORT_ORDER + " = 'top_rated'";
        } else if (sortOrderValueToInt == 2) {
            movie = MovieContract.FavoriteEntry.CONTENT_URI;
        }
//        Need to read pref file then if else if a URI.. Girls back from ballet need to get dinner ready....

//What if changed sortOrder to int then switch to one of three different loaders, branch first...
        switch (id) {
            case MOVIE_LOADER:
                return new CursorLoader(getActivity(),
                        movie,
                        movieColumns,
                        selection,
                        null,
                        null);
//FIXED: Don't know why the above sort order now breaks app on new install. Mysteriously just started working....
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMoviesAdapter.swapCursor(data);

        if (mSavedInstanceState == false) {
            Log.v(LOG_TAG, "In onCreate() savedInstanceState is null");
            Cursor cursor = (Cursor) mMoviesAdapter.getItem(1);
//            String movieID = cursor.getString(COL_MOVIE_ID);
        }

        mGridView.setSelection(1);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMoviesAdapter.swapCursor(null);
    }
}
package com.example.wilbotiv.popularmoviess1;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

public class MoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private MoviesAdapter mMoviesAdapter;
//    private static final int MOVIE_LOADER = 0;

    private static final String[] MOVIE_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_POSTERPATH,
            MovieContract.MovieEntry.COLUMN_ORIGINALTITLE,
            MovieContract.MovieEntry.COLUMN_RELEASEDATE,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_ID_KEY,
            MovieContract.MovieEntry.COLUMN_VOTEAVERAGE,
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_MOVIE_ID = 0;
    static final int COL_POSTERPATH = 1;
    static final int COL_ORIGINALTITLE = 2;
    static final int COL_RELEASEDATE = 3;
    static final int COL_OVERVIEW = 4;
    static final int COL_ID_KEY = 5;
    static final int COL_VOTEAVERAGE = 6;


    private final int LOADER_ID = 0;

    public MoviesFragment() {
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
            updateMovie();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        Uri moviesUri = MovieContract.MovieEntry.buildMovieUri();
//        Cursor cur = getActivity().getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, null, null, null, null);
//        String[] projection = new String[]{BaseColumns._ID, UserDictionary.Words.WORD};
//        Cursor cur = getActivity().getContentResolver().query(UserDictionary.Words.CONTENT_URI, projection, null, null, null);

        mMoviesAdapter = new MoviesAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movies);
        gridView.setAdapter(mMoviesAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                if (cursor != null) {
                    Intent intent = new Intent(getActivity(), DetailActivity.class)
                            .setData(MovieContract.MovieEntry.CONTENT_URI);
                    startActivity(intent);
                }
            }
        });

/*
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                String movies = mMoviesAdapter.getItem(position).posterPath;
//                Toast.makeText(getActivity(), movies, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable(PAR_KEY, movies.get(position));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
*/
        return rootView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }


    private void updateMovie() {
        FetchMovieTask movieTask = new FetchMovieTask(getActivity());
        /*SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String location = prefs.getString(getString(R.string.pref_location_key),
                getString(R.string.pref_location_default));*/
//        weatherTask.execute(location);
        movieTask.execute();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cur = new CursorLoader(getActivity(), MovieContract.MovieEntry.CONTENT_URI, null, null, null, null);
//        CursorLoader cur = getActivity().getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, null, null, null, null);
        return cur;
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


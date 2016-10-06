package com.example.wilbotiv.popularmoviess1.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.wilbotiv.popularmoviess1.R;
import com.example.wilbotiv.popularmoviess1.fragments.DetailFragment;
import com.example.wilbotiv.popularmoviess1.fragments.MoviesFragment;
import com.facebook.stetho.Stetho;

//DONE: INSTALL - Stetho guide - an Android app debug tool
//DONE: Allow users to view and play trailers
//DONE: read reviews of a selected movie.
//DONE: Mark a movie as a favorite in the details view by tapping a button
//Done: FavoriteActivity.
//Done: DetailActivity for Favorites.
//Done: Modify the existing sorting criteria for the main view to include an additional pivot to show their favorites collection.
//TODO: Lastly, you’ll optimize your app experience for tablet.
//Done: Add Release Date to Details Fragment
//Done: If no poster path or overview from movieDB included, then handle it...
//Done: movieTable keeps growing.... fetch called too often.
//Done: TOAST in Favorite to SAY "You added x to Favoites"....
//DONE: Don't know why the above sort order now breaks app on new install. Mysteriously just started working....
//Done: Top rated views are sometimes messed-up - Better but not perfect
//DONE: append us localization to moviedb api - Decided not to as I don't see how...
//Done: Change movie URI so that we use WHERE instead of sort order
//Done: Reviews table, create it
//Done: Create new Asynctask for review. Fetch and JSON Parsing
//// DONE: 8/29/2016 trailerButton view duplicates after trailer intent launches
//// DONE: 8/30/2016 Is top rated pulling trailers and reveiws? - Yes
// Done: 8/30/2016 Favorites>Details>Poster,etc.
// Done: 9/7/2016 move fragments to their own file
// Done: 9/29/2016 Movie Details View includes an Action Bar item that allows the user to share the first trailer video URL from the list of trailers
// TODO: 9/29/2016 In Master-Detail layout fix so that if nothing is selected first movie is shown in Detail fragment
public class MainActivity extends ActionBarActivity implements MoviesFragment.Callback {
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String DETAILS_FRAGMENT_TAG = "DFTAG";

    private boolean mTwoPane;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreate() called");
        super.onCreate(savedInstanceState);

        // Create an InitializerBuilder
        Stetho.InitializerBuilder initializerBuilder =
                Stetho.newInitializerBuilder(this);
        // Enable Chrome DevTools
        initializerBuilder.enableWebKitInspector(
                Stetho.defaultInspectorModulesProvider(this)
        );
        // Enable command line interface
        initializerBuilder.enableDumpapp(
                Stetho.defaultDumperPluginsProvider(this)
        );
        // Use the InitializerBuilder to generate an Initializer
        Stetho.Initializer initializer = initializerBuilder.build();
        // Initialize Stetho with the Initializer
        Stetho.initialize(initializer);

        setContentView(R.layout.activity_main);

        // DONE: 9/13/2016 should this say fragment_details
        if (findViewById(R.id.detail_container) != null) {
            mTwoPane = true;

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_container, new DetailFragment(), DETAILS_FRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }


        //Done: Do I need to do this?
        PreferenceManager.setDefaultValues(this, R.xml.prefs_general, false);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(LOG_TAG, "onResume() called");
        // TODO: 9/14/2016 Do I need to do this?
        DetailFragment df = (DetailFragment) getSupportFragmentManager().findFragmentByTag(DETAILS_FRAGMENT_TAG);
    }

    @Override
    public void onItemSelected(Uri contentUri) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL_URI, contentUri);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_container, fragment, DETAILS_FRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .setData(contentUri);
            startActivity(intent);
        }
    }
}

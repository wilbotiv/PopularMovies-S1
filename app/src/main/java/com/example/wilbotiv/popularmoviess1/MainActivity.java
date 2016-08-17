package com.example.wilbotiv.popularmoviess1;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.stetho.Stetho;

//DONE: INSTALL - Stetho guide - an Android app debug tool
//TODO: Allow users to view and play trailers
//TODO: read reviews of a selected movie.
//DONE: Mark a movie as a favorite in the details view by tapping a button
//Done: FavoriteActivity.
//Done: DetailActivity for Favorites.
//Done: Modify the existing sorting criteria for the main view to include an additional pivot to show their favorites collection.
//TODO: Lastly, you’ll optimize your app experience for tablet.
//Done: Add Release Date to Details Fragment
//TODO: If no poster path or overview from movieDB included, then handle it...
//TODO: movieTable keeps growing.... fetch called too often.
//Done: TOAST in Favorite to SAY "You added x to Favoites"....
//TODO: Don't know why the above sort order now breaks app on new install. Mysteriously just started working....
//TODO: Top rated views are sometimes messed-up
//TODO: Change movie URI so that we use WHERE instead of sort order

public class MainActivity extends ActionBarActivity {
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreate() called");
        super.onCreate(savedInstanceState);
//        Stetho.initializeWithDefaults(this);

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
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MoviesFragment())
                    .commit();
        }
        //TODO: Do I need to do this?
        PreferenceManager.setDefaultValues(this, R.xml.prefs_general, false);

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(LOG_TAG, "onPause() called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(LOG_TAG, "onResume() called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(LOG_TAG, "onStop() called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(LOG_TAG, "onDestroy() called");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(LOG_TAG, "onStart() called");
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
}

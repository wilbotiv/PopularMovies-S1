package com.example.wilbotiv.popularmoviess1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
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
                    .add(R.id.container, new PlaceholderFragment())
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
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            Intent intent = getActivity().getIntent();
            Movie movie = intent.getParcelableExtra(MoviesFragment.PAR_KEY);
            TextView textViewOriginalTitle = (TextView) rootView.findViewById(R.id.fragment_detail_textView_originalTitle);
            ImageView imageView = (ImageView) rootView.findViewById(R.id.fragment_detail_imageView_poster);
            TextView textViewReleaseDate = (TextView) rootView.findViewById(R.id.fragment_detail_textView_releaseDate);
            TextView textViewVoteAverage = (TextView) rootView.findViewById(R.id.fragment_detail_textView_voteAverage);
            TextView textViewOverview = (TextView) rootView.findViewById(R.id.fragment_detail_textView_overview);

            textViewOriginalTitle.setText(movie.getOriginalTitle());
            textViewReleaseDate.setText(movie.getReleaseDate());
            textViewVoteAverage.setText(movie.getVoteAverage());
            textViewOverview.setText(movie.getOverview());

            Picasso.with(getContext()).load("http://image.tmdb.org/t/p/w500" + movie.getPosterPath()).into(imageView);
            return rootView;
        }
    }
}

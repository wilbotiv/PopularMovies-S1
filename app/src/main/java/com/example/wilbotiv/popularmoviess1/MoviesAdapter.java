package com.example.wilbotiv.popularmoviess1;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class MoviesAdapter extends CursorAdapter {
    private static final String LOG_TAG = MoviesAdapter.class.getSimpleName();

    public MoviesAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);


    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.grid_item_movie, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
//        TextView tvoriginalTitle = (TextView)view.findViewById(R.id.grid_item_original_title);
        ImageView IVposterPath = (ImageView) view.findViewById(R.id.grid_item_movie_image);

//        int originalTitle = cursor.getColumnIndex(com.example.wilbotiv.popularmoviess1.MovieContract.MovieEntry.COLUMN_ORIGINALTITLE);
        int posterPath = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTERPATH);
        String path = cursor.getString(posterPath);

        Picasso.with(context)
                .load("http://image.tmdb.org/t/p/w600" + path)
//                .placeholder(R.drawable.sample_6)
                .error(R.drawable.sample_7)
                .into(IVposterPath);

//        tvoriginalTitle.setText(cursor.getString(originalTitle));
//        tvposterPath.setText(cursor.getString(posterPath));
//        tv.setText("Test");
    }
}

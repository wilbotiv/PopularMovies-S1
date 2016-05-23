package com.example.wilbotiv.popularmoviess1;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {
    private String posterPath;
    private String overview;
    private String releaseDate;
    private String originalTitle;
    private String id;
    private String voteAverage;

    public Movie() {

    }

    @Override

    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(posterPath);
        dest.writeString(overview);
        dest.writeString(releaseDate);
        dest.writeString(originalTitle);
        dest.writeString(id);
        dest.writeString(voteAverage);

    }

    private Movie(Parcel in) {
        this.posterPath = in.readString();
        this.overview = in.readString();
        this.releaseDate = in.readString();
        this.originalTitle = in.readString();
        this.id = in.readString();
        this.voteAverage = in.readString();

    }

    public static final Parcelable.Creator<Movie> CREATOR = new Creator<Movie>() {

        public Movie createFromParcel(Parcel source) {

            Movie mMovie = new Movie();
            mMovie.posterPath = source.readString();
            mMovie.overview = source.readString();
            mMovie.releaseDate = source.readString();
            mMovie.originalTitle = source.readString();
            mMovie.id = source.readString();
            mMovie.voteAverage = source.readString();
            return mMovie;
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(String voteAverage) {
        this.voteAverage = voteAverage;
    }
}
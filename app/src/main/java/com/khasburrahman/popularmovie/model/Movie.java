package com.khasburrahman.popularmovie.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Kharis on 07-Feb-18.
 */

public class Movie implements Parcelable {

    public Movie(JSONObject jsonMovieObject){
        try {
            this.vote_count = jsonMovieObject.getString("vote_count");
            this.id = jsonMovieObject.getString("id");
            this.video = jsonMovieObject.getString("video");
            this.vote_average = jsonMovieObject.getString("vote_average");
            this.title = jsonMovieObject.getString("title");
            this.popularity = jsonMovieObject.getString("popularity");
            this.poster_path = jsonMovieObject.getString("poster_path");
            this.original_language = jsonMovieObject.getString("original_language");
            this.original_title = jsonMovieObject.getString("original_title");

            //masukkan array genre id ke field genre_ids
            JSONArray jsonArrayGenreId = jsonMovieObject.getJSONArray("genre_ids");
            this.genre_ids = new String[jsonArrayGenreId.length()];
            for (int i = 0; i < jsonArrayGenreId.length(); i++){
                this.genre_ids[i] = jsonArrayGenreId.getString(i);
            }

            this.backdrop_path = jsonMovieObject.getString("backdrop_path");
            this.adult = jsonMovieObject.getString("adult");
            this.overview = jsonMovieObject.getString("overview");
            this.release_date = jsonMovieObject.getString("release_date");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String vote_average;

    private String backdrop_path;

    private String adult;

    private String id;

    private String title;

    private String overview;

    private String original_language;

    private String[] genre_ids;

    private String release_date;

    private String original_title;

    private String vote_count;

    private String poster_path;

    private String video;

    private String popularity;

    protected Movie(Parcel in) {
        vote_average = in.readString();
        backdrop_path = in.readString();
        adult = in.readString();
        id = in.readString();
        title = in.readString();
        overview = in.readString();
        original_language = in.readString();
        genre_ids = in.createStringArray();
        release_date = in.readString();
        original_title = in.readString();
        vote_count = in.readString();
        poster_path = in.readString();
        video = in.readString();
        popularity = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public String getVote_average ()
    {
        return vote_average;
    }

    public void setVote_average (String vote_average)
    {
        this.vote_average = vote_average;
    }

    public String getBackdrop_path ()
    {
        return backdrop_path;
    }

    public void setBackdrop_path (String backdrop_path)
    {
        this.backdrop_path = backdrop_path;
    }

    public String getAdult ()
    {
        return adult;
    }

    public void setAdult (String adult)
    {
        this.adult = adult;
    }

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getTitle ()
    {
        return title;
    }

    public void setTitle (String title)
    {
        this.title = title;
    }

    public String getOverview ()
    {
        return overview;
    }

    public void setOverview (String overview)
    {
        this.overview = overview;
    }

    public String getOriginal_language ()
    {
        return original_language;
    }

    public void setOriginal_language (String original_language)
    {
        this.original_language = original_language;
    }

    public String[] getGenre_ids ()
    {
        return genre_ids;
    }

    public void setGenre_ids (String[] genre_ids)
    {
        this.genre_ids = genre_ids;
    }

    public String getRelease_date ()
    {
        return release_date;
    }

    public void setRelease_date (String release_date)
    {
        this.release_date = release_date;
    }

    public String getOriginal_title ()
    {
        return original_title;
    }

    public void setOriginal_title (String original_title)
    {
        this.original_title = original_title;
    }

    public String getVote_count ()
    {
        return vote_count;
    }

    public void setVote_count (String vote_count)
    {
        this.vote_count = vote_count;
    }

    public String getPoster_path ()
    {
        return poster_path;
    }

    public void setPoster_path (String poster_path)
    {
        this.poster_path = poster_path;
    }

    public String getVideo ()
    {
        return video;
    }

    public void setVideo (String video)
    {
        this.video = video;
    }

    public String getPopularity ()
    {
        return popularity;
    }

    public void setPopularity (String popularity)
    {
        this.popularity = popularity;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [vote_average = "+vote_average+", backdrop_path = "+backdrop_path+", adult = "+adult+", id = "+id+", title = "+title+", overview = "+overview+", original_language = "+original_language+", genre_ids = "+genre_ids+", release_date = "+release_date+", original_title = "+original_title+", vote_count = "+vote_count+", poster_path = "+poster_path+", video = "+video+", popularity = "+popularity+"]";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(vote_average);
        parcel.writeString(backdrop_path);
        parcel.writeString(adult);
        parcel.writeString(id);
        parcel.writeString(title);
        parcel.writeString(overview);
        parcel.writeString(original_language);
        parcel.writeStringArray(genre_ids);
        parcel.writeString(release_date);
        parcel.writeString(original_title);
        parcel.writeString(vote_count);
        parcel.writeString(poster_path);
        parcel.writeString(video);
        parcel.writeString(popularity);
    }
}

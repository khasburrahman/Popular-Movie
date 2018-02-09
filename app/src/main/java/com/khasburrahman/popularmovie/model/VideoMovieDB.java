package com.khasburrahman.popularmovie.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Kharis on 09-Feb-18.
 */

public class VideoMovieDB implements Parcelable {
    public String name;
    public String site;
    public String key;

    public VideoMovieDB(JSONObject jsonMovieObject){
        try {
            this.name = jsonMovieObject.getString("name");
            this.site = jsonMovieObject.getString("site");
            this.key = jsonMovieObject.getString("key");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected VideoMovieDB(Parcel in) {
        name = in.readString();
        site = in.readString();
        key = in.readString();
    }

    public static final Creator<VideoMovieDB> CREATOR = new Creator<VideoMovieDB>() {
        @Override
        public VideoMovieDB createFromParcel(Parcel in) {
            return new VideoMovieDB(in);
        }

        @Override
        public VideoMovieDB[] newArray(int size) {
            return new VideoMovieDB[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(site);
        parcel.writeString(key);
    }
}

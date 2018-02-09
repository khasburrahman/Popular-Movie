package com.khasburrahman.popularmovie.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Kharis on 09-Feb-18.
 */

public class Reviews implements Parcelable {
    public Reviews(JSONObject jsonMovieObject){
        try {
            this.author = jsonMovieObject.getString("author");
            this.content = jsonMovieObject.getString("content");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public String author;
    public String content;

    protected Reviews(Parcel in) {
        author = in.readString();
        content = in.readString();
    }

    public static final Creator<Reviews> CREATOR = new Creator<Reviews>() {
        @Override
        public Reviews createFromParcel(Parcel in) {
            return new Reviews(in);
        }

        @Override
        public Reviews[] newArray(int size) {
            return new Reviews[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(author);
        parcel.writeString(content);
    }
}

package com.khasburrahman.popularmovie.utility;

import android.content.SharedPreferences;

import java.util.ArrayList;

/**
 * Created by Kharis on 08-Feb-18.
 */

public class FavoriteMovieHelper {
    final static private String FAVORITE_PREFERENCES = "favorite_pref";

    public static void addMoviesIdToJSON(String movieId, SharedPreferences sharedPreferences){
        String jsonSharedPref = sharedPreferences.getString(FAVORITE_PREFERENCES, null);
        ArrayList<String> listMovie = new ArrayList<>();
        if (jsonSharedPref != null) {

        } else {

        }
        sharedPreferences.edit()
    }
}

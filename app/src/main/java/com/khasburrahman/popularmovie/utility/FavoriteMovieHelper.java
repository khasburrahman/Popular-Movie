package com.khasburrahman.popularmovie.utility;

import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by Kharis on 08-Feb-18.
 * I know probably this isn't a good idea manually parse list of string to JSONArray, then storing it on sharedpreferenes
 * but kinda curious how it turns out,
 */

public class FavoriteMovieHelper {
    final static private String FAVORITE_PREFERENCES = "favorite_pref";

    public static ArrayList<String> getAllFavoriteMovie(SharedPreferences sharedPreferences){
        ArrayList<String> list;
        String stringJSONfromPreferences = sharedPreferences.getString(FAVORITE_PREFERENCES, null);
        if (stringJSONfromPreferences == null){
            return null;
        } else {
            return getListFromJSON(stringJSONfromPreferences);
        }
    }

    public static void addMoviesIdToFavorite(String movieId, SharedPreferences sharedPreferences){
        String stringJsonSharedPref = sharedPreferences.getString(FAVORITE_PREFERENCES, null);

        ArrayList<String> listMovie;

        if (stringJsonSharedPref != null) {
            listMovie = getListFromJSON(stringJsonSharedPref);
            listMovie.add(movieId);
        } else {
            listMovie = new ArrayList<>();
            listMovie.add(movieId);
        }

        commitChanges(listMovie, sharedPreferences);
    }

    public static void removeMovieIdFromFavorite(String movieId, SharedPreferences sharedPreferences){
        String stringJsonSharedPref = sharedPreferences.getString(FAVORITE_PREFERENCES, null);
        ArrayList<String> list = getListFromJSON(stringJsonSharedPref);
        for (int i = 0; i < list.size(); i++){
            if (list.get(i).equals(movieId)){
                list.remove(i);
                commitChanges(list, sharedPreferences);
                return;
            }
        }
    }

    public static boolean isFavorite(String movieId, SharedPreferences sharedPreferences){
        String stringJSONSharedPref = sharedPreferences.getString(FAVORITE_PREFERENCES, null);

        if (stringJSONSharedPref != null){
            ArrayList<String> list = getListFromJSON(stringJSONSharedPref);
            if (list.size() > 0){
                for(String idMovie : list){
                    if (idMovie.equals(movieId)){
                        return true;
                    }
                }
                return false;
            } else
                return false;
        } else {
            return false;
        }
    }

    private static void commitChanges(ArrayList<String> list, SharedPreferences sharedPreferences){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(FAVORITE_PREFERENCES, getJSONStringFromList(list));
        editor.commit();
    }

    private static String getJSONStringFromList(ArrayList<String> list){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        int size = list.size();
        for(int i = 0; i < size; i++){
            stringBuilder.append("\""+list.get(i)+"\"");
            if (i < size - 1){
                stringBuilder.append(",");
            }
        }
        stringBuilder.append("]");

        return stringBuilder.toString();
    }

    private static ArrayList<String> getListFromJSON(String jsonString){
        ArrayList<String> list = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i = 0; i < jsonArray.length(); i++){
                list.add(jsonArray.getString(i));
            }
        } catch (JSONException e) {
            Log.d("JSON FAIL", "readFromJSON: gagal membaca dari json sharedpreferences ");
            e.printStackTrace();
        }
        return list;
    }
}

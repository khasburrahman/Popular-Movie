package com.khasburrahman.popularmovie.utility;

import com.khasburrahman.popularmovie.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Kharis on 07-Feb-18.
 */

public class MovieDBJSONResultHelper {
    /**
     * mengubah objek JSON hasil dari api call ke movie db menjadi array list dari Movie
     * @param jsonObject hasil JSON string dari API themoviedb dijadikan JSON object
     * @return list film
     */
    public static ArrayList<Movie> getMovieList(JSONObject jsonObject){
        //list yang di return
        ArrayList<Movie> listMovie = new ArrayList<>();

        try {
            JSONArray jsonArrayMovie = jsonObject.getJSONArray("results");
            for (int i = 0; i < jsonArrayMovie.length(); i++){
                listMovie.add(new Movie(jsonArrayMovie.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listMovie;
    }

    public static int getTotalResults(JSONObject jsonObject){
        int total_resutls = 0;
        try {
            total_resutls =  jsonObject.getInt("total_results");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return total_resutls;
    }

    public static int getTotalPages(JSONObject jsonObject){
        int total_pages = 0;
        try {
            total_pages =  jsonObject.getInt("total_pages");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return total_pages;
    }


}

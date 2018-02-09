package com.khasburrahman.popularmovie.utility;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Kharis on 07-Feb-18.
 */

public class NetworkUtils {
    //konstan untuk view
    public static final String POPULAR = "popular";
    public static final String TOP = "top";
    public static final String FAVORITE = "fav";
    public static final String VIDEOS = "vid";
    public static final String REVIEWS = "review";

    /**
     * URL API untuk popular dan top rated movie
     */
    final static String MOVIEDB_API_3_POPULAR_URL = "https://api.themoviedb.org/3/movie/popular";
    final static String MOVIEDB_API_3_TOP_URL = "https://api.themoviedb.org/3/movie/top_rated";
    final static String MOVIEDB_API_3_DETAIL_MOVIE_URL = "https://api.themoviedb.org/3/movie";
    static String MOVIEDB_API_3_REVIEW(String movieid){
        return "https://api.themoviedb.org/3/movie/"+movieid+"/reviews";
    }
    static String MOVIEDB_API_3_VIDEOS(String movieid){
        return "https://api.themoviedb.org/3/movie/"+movieid+"/videos";
    }

    final static String MOVIEDB_POSTER_BASE_URL = "http://image.tmdb.org/t/p/w185/";

    /**
     * parameter untuk moviedb api 3
     */
    final static String PARAM_api_key = "api_key";
    final static String PARAM_language = "language";
    final static String PARAM_page = "page";
    final static String PARAM_region = "region";


    /**
     * Method untuk membikin url dari parameter yang dibuat
     * @param type string "popular" untuk popular movie, "top" untuk top rated movie
     * @param page untuk halaman, default 1
     * @param apiKey apikey moviedb 3
     * @param language untuk bahasa default "en-US"
     * @param region untuk region default ID
     * @param movieId
     * @return url api yang dibuat
     */
    public static URL buildURL(String type, Integer page, String apiKey, String language, String region, String movieId){
        Uri.Builder builderUri;
        if (type.equals(FAVORITE) || type.equals(REVIEWS) || type.equals(VIDEOS)){
            if (movieId == null){
                Log.d("URL BUILDEr", "buildURL: movie id belum dimasukin");
                return null;
            }
        }

        if (type.equals(POPULAR))
            builderUri = Uri.parse(MOVIEDB_API_3_POPULAR_URL).buildUpon();
        else if (type.equals(TOP))
            builderUri = Uri.parse(MOVIEDB_API_3_TOP_URL).buildUpon();
        else if (type.equals(FAVORITE))
            builderUri = Uri.parse(MOVIEDB_API_3_DETAIL_MOVIE_URL+"/"+movieId).buildUpon();
        else if (type.equals(VIDEOS))
            builderUri = Uri.parse(MOVIEDB_API_3_VIDEOS(movieId)).buildUpon();
        else if (type.equals(REVIEWS))
            builderUri = Uri.parse(MOVIEDB_API_3_REVIEW(movieId)).buildUpon();
        else
            return null;
        builderUri.appendQueryParameter(PARAM_api_key, apiKey);
        if (page != null && page != 0 && movieId == null){
            builderUri.appendQueryParameter(PARAM_page, page.toString());
        }
        if (!isEmptyString(language)){
            builderUri.appendQueryParameter(PARAM_language, language);
        }
        if (!isEmptyString(region) && movieId == null){
            builderUri.appendQueryParameter(PARAM_region, region);
        }
        Uri uri = builderUri.build();
        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e){
            e.printStackTrace();
        }
        return  url;
    }

    private static boolean isEmptyString(String language){
        return language == null || !(TextUtils.isEmpty(language));
    }

    public static String getResponseFromHTTPUrl(URL url) throws IOException{
        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");
            boolean hasInput = scanner.hasNext();
            if (hasInput){
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    /**
     * mengecek internet aktif atau tidak
     * @param cm dari kelas activity (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
     * @return nilai apakah internet ada atau tidak
     */
    static public boolean isOnline(ConnectivityManager cm) {
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    static public String getMovieDBPosterStringPath(String posterPath){
        return MOVIEDB_POSTER_BASE_URL+posterPath;
    }


}

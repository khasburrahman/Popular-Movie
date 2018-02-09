package com.khasburrahman.popularmovie;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.khasburrahman.popularmovie.adapter.AdapterPopularMovie;
import com.khasburrahman.popularmovie.model.Movie;
import com.khasburrahman.popularmovie.utility.FavoriteMovieHelper;
import com.khasburrahman.popularmovie.utility.MovieDBJSONResultHelper;
import com.khasburrahman.popularmovie.utility.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>, AdapterPopularMovie.PopularMovieItemClickListener {
    //view yang dipakai
    RecyclerView rv_listPopularMovie;
    ProgressBar pb_loadingPopularMovie;

    //adapter
    AdapterPopularMovie adapterPopularMovie;

    //tipe yang ditampilkan di recyclerView
    ArrayList<Movie> listMovie;
    final String SAVE_BUNDLE_LIST_MOVIE = "savebundlelistmovie";

    //tipe yang ditampilan : popular || top || fav
    String typeMovie;
    private static final String ID_BUNDLE_TYPE_SHOWN = "whatisshown";

    //total
    int TOTAL_RESULTS = 0;
    int TOTAL_PAGES = 0;
    int TOTAL_FAVORITE = 0;
    private static final String ID_BUNDLE_TOTAL_FAV = "totalfav";


    //konstan untuk loader manager & bundle
    private static final String ID_BUNDLE_MOVIEDB_QUERY_EXTRA = "moviequery";
    private static final int ID_POPULAR_MOVIE_LOADER = 23;
    //this is the initial id, depending on how much the favorite list the id used for loading the movie detail will be this to number of favorite
    private static final int ID_DETAIL_MOVIE_LOADER_START = 3212;

    //konstan untuk view
    private static final String REFRESH_POPULAR = "popular";
    private static final String  REFRESH_TOP = "top";
    private static final String REFRESH_FAVORITE = "fav";

    public static final String ID_APP_SHARED_PREF = "popularmoviewdnjkas";
    SharedPreferences sharedPreferences;

    final static int REQUEST_CODE_DETAIL_ACTIVITY = 3251;
    static ArrayList<Integer> loaderIdList = new ArrayList<>();

    final static String SAVE_VISIBILITY_RECYCLERVIEW = "visibilityrecyview";
    final static String SAVE_VISIBILITY_PROGRESSBAR = "visibilityprogressbar";
    final static String SAVE_TITLE = "savetitle";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //recycler view, pakai grid layout manager, fixed size == false
        rv_listPopularMovie = findViewById(R.id.rv_listPopularMovie);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
        rv_listPopularMovie.setLayoutManager(gridLayoutManager);
        rv_listPopularMovie.setHasFixedSize(false);
        pb_loadingPopularMovie = findViewById(R.id.pb_loading_popular_movie);

        sharedPreferences = getSharedPreferences(ID_APP_SHARED_PREF, MODE_PRIVATE);

        //init loader
//        for (int i = 0; i < TOTAL_FAVORITE; i++){
//            getSupportLoaderManager().initLoader(ID_DETAIL_MOVIE_LOADER_START+i, null, this);
//        }
//        getSupportLoaderManager().initLoader(ID_POPULAR_MOVIE_LOADER, null, this);




        listMovie = new ArrayList<>();
        adapterPopularMovie = new AdapterPopularMovie(this, listMovie, this);
        this.rv_listPopularMovie.setAdapter(adapterPopularMovie);

        //load typeview dari bundle
        if (savedInstanceState != null){
            this.setTitle(savedInstanceState.getString(SAVE_TITLE));
            this.pb_loadingPopularMovie.setVisibility(savedInstanceState.getInt(SAVE_VISIBILITY_PROGRESSBAR));
            this.rv_listPopularMovie.setVisibility(savedInstanceState.getInt(SAVE_VISIBILITY_RECYCLERVIEW));
            //this.listMovie = savedInstanceState.getParcelableArrayList(SAVE_BUNDLE_LIST_MOVIE);
            this.typeMovie = savedInstanceState.getString(ID_BUNDLE_TYPE_SHOWN);
            this.TOTAL_FAVORITE = savedInstanceState.getInt(ID_BUNDLE_TOTAL_FAV);
            refreshView(typeMovie);
        } else {
            this.typeMovie = REFRESH_POPULAR;
        }


    }

    @Override
    protected void onStart() {
        super.onStart();

        boolean isOnline = NetworkUtils.isOnline((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE));
        if (isOnline) {
            loadMovie(this.typeMovie, 1, null);

        } else
            Toast.makeText(this, "Please Ensure Network Connection is Established", Toast.LENGTH_SHORT).show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity, menu);
        return true;
    }

    @Override
    public Loader<String> onCreateLoader(final int i, final Bundle args) {
        return new AsyncTaskLoader<String>(this) {
            @Override
            protected void onStartLoading() {
                if (args == null){
                    return;
                }
                if (i == ID_POPULAR_MOVIE_LOADER) {
                    rv_listPopularMovie.setVisibility(View.INVISIBLE);
                    pb_loadingPopularMovie.setVisibility(View.VISIBLE);
                }
                forceLoad();
            }

            @Override
            public String loadInBackground() {
                String queryURLString = args.getString(ID_BUNDLE_MOVIEDB_QUERY_EXTRA);
                if (queryURLString == null || TextUtils.isEmpty(queryURLString)){
                    return null;
                }
                try {
                    URL queryURL = new URL(queryURLString);
                    String result = NetworkUtils.getResponseFromHTTPUrl(queryURL);
                    return result;

                } catch (IOException e){
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }

    private void loadMovie(String type, int page, String movieId){
        String apiKey = getResources().getString(R.string.moviedb_api_3_key);
        URL apiURL = NetworkUtils.buildURL(type, page, apiKey, "en-US", "ID", movieId);

        Bundle queryBundle = new Bundle();
        queryBundle.putString(ID_BUNDLE_MOVIEDB_QUERY_EXTRA, apiURL.toString());

        int ID_LOADER;
        if (movieId == null){
            ID_LOADER = ID_POPULAR_MOVIE_LOADER;
        } else {
            ID_LOADER = ID_DETAIL_MOVIE_LOADER_START+mRequestFavoriteMovie;
            --mRequestFavoriteMovie;
        }
        loaderIdList.add(ID_LOADER);

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> movieLoader = loaderManager.getLoader(ID_LOADER);

        if (movieLoader == null){
            loaderManager.initLoader(ID_LOADER, queryBundle, this);
        } else {
            loaderManager.restartLoader(ID_LOADER, queryBundle, this);
        }
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String dataStringJSON) {
        if (dataStringJSON == null){
            return;
        }
        pb_loadingPopularMovie.setVisibility(View.INVISIBLE);
        JSONObject jsonObjectMovieDB = null;
        try {
            jsonObjectMovieDB = new JSONObject(dataStringJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (jsonObjectMovieDB != null) {
            int loaderID = loader.getId();
            //if load the detail movie, (favorite)
            if (loaderID >= ID_DETAIL_MOVIE_LOADER_START){
                onLoadFinishedDetailMovie(jsonObjectMovieDB);
            } else {
                onLoadFinishedMovieList(jsonObjectMovieDB);
            }

            adapterPopularMovie.updateListMovie(this.listMovie);
            rv_listPopularMovie.setVisibility(View.VISIBLE);
        }

    }

    public void onLoadFinishedMovieList(JSONObject jsonObjectMovieDB){
        this.listMovie = MovieDBJSONResultHelper.getMovieList(jsonObjectMovieDB);
        this.TOTAL_PAGES = MovieDBJSONResultHelper.getTotalPages(jsonObjectMovieDB);
        this.TOTAL_RESULTS = MovieDBJSONResultHelper.getTotalResults(jsonObjectMovieDB);
    }

    private static int mRequestFavoriteMovie = 0;
    public void onLoadFinishedDetailMovie(JSONObject jsonObject){
        this.listMovie.add(new Movie(jsonObject));
        this.TOTAL_PAGES = 0;
        this.TOTAL_RESULTS = 0;
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SAVE_TITLE, this.getTitle().toString());
        outState.putInt(SAVE_VISIBILITY_PROGRESSBAR, this.pb_loadingPopularMovie.getVisibility());
        outState.putInt(SAVE_VISIBILITY_RECYCLERVIEW, this.rv_listPopularMovie.getVisibility());
        outState.putInt(ID_BUNDLE_TOTAL_FAV, this.TOTAL_FAVORITE);
        outState.putString(ID_BUNDLE_TYPE_SHOWN, this.typeMovie);
    }

    @Override
    public void onListItemPopularMovieClick(int position) {
        Intent intent = new Intent(this, MovieDetailActivity.class);
        Movie movie = this.listMovie.get(position);
        intent.putExtra("movie", movie);
        intent.putExtra("isfavorite", FavoriteMovieHelper.isFavorite(movie.getId(), sharedPreferences));
        this.startActivityForResult(intent, REQUEST_CODE_DETAIL_ACTIVITY);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_get_favorites:
                this.typeMovie = REFRESH_FAVORITE;
                refreshView(REFRESH_FAVORITE);
                setTitle("Favorite Movies");
                break;
            case R.id.action_get_popular:
                this.typeMovie = REFRESH_POPULAR;
                refreshView(REFRESH_POPULAR);
                setTitle("Popular Movies");
                break;
            case R.id.action_get_top:
                this.typeMovie = REFRESH_TOP;
                refreshView(REFRESH_TOP);
                setTitle("Top Rated Movies");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshView(String refreshId){
        if (!refreshId.equals(REFRESH_FAVORITE)){
            loadMovie(refreshId, 1, null);
        } else{
            //TODO bikin favorit
            this.rv_listPopularMovie.setVisibility(View.INVISIBLE);
            this.listMovie.clear();
            this.adapterPopularMovie.notifyDataSetChanged();
            ArrayList<String> listFavoriteMovie = FavoriteMovieHelper.getAllFavoriteMovie(sharedPreferences);
            mRequestFavoriteMovie = listFavoriteMovie.size();
            for(String movieId : listFavoriteMovie){
                loadMovie(REFRESH_FAVORITE, 0, movieId);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStop() {
        this.listMovie.clear();
        this.adapterPopularMovie.notifyDataSetChanged();
        super.onStop();
        for (int id : loaderIdList){
            getLoaderManager().destroyLoader(id);
        }
    }
}

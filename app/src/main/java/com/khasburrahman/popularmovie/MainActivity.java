package com.khasburrahman.popularmovie;


import android.content.Context;
import android.content.Intent;
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

    //tipe yang ditampilan : popular || top
    String typeMovie;

    //total
    int TOTAL_RESULTS = 0;
    int TOTAL_PAGES = 0;

    //konstan untuk loader manager
    private static final String MOVIEDB_QUERY_EXTRA = "moviequery";
    private static final int POPULAR_MOVIE_LOADER = 23;
    private static final String REFRESH_POPULAR = "popular";
    private static final String  REFRESH_TOP = "top";
    private static final String REFRESH_FAVORITE = "fav";

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


        getSupportLoaderManager().initLoader(POPULAR_MOVIE_LOADER, null, this);

        //init list
        listMovie = new ArrayList<>();
        adapterPopularMovie = new AdapterPopularMovie(this, listMovie, this);
        rv_listPopularMovie.setAdapter(adapterPopularMovie);

        boolean isOnline = NetworkUtils.isOnline((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE));
        if (isOnline) {
            loadMovie(REFRESH_POPULAR, 1);

        } else
            Toast.makeText(this, "Please Ensure Network Connection is Established", Toast.LENGTH_SHORT).show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity, menu);
        return true;
    }

    @Override
    public Loader<String> onCreateLoader(int i, final Bundle args) {
        return new AsyncTaskLoader<String>(this) {
            @Override
            protected void onStartLoading() {
                if (args == null){
                    return;
                }
                //TODO: bikin loading indicator
                rv_listPopularMovie.setVisibility(View.INVISIBLE);
                pb_loadingPopularMovie.setVisibility(View.VISIBLE);
                forceLoad();
            }

            @Override
            public String loadInBackground() {
                String queryURLString = args.getString(MOVIEDB_QUERY_EXTRA);
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

    private void loadMovie(String type, int page){
        String apiKey = getResources().getString(R.string.moviedb_api_3_key);
        URL apiURL = NetworkUtils.buildURL(type, page, apiKey, "en-US", "ID");

        Bundle queryBundle = new Bundle();
        queryBundle.putString(MOVIEDB_QUERY_EXTRA, apiURL.toString());

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> movieLoader = loaderManager.getLoader(POPULAR_MOVIE_LOADER);

        if (movieLoader == null){
            loaderManager.initLoader(POPULAR_MOVIE_LOADER, queryBundle, this);
        } else {
            loaderManager.restartLoader(POPULAR_MOVIE_LOADER, queryBundle, this);
        }
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String dataStringJSON) {
        pb_loadingPopularMovie.setVisibility(View.INVISIBLE);
        JSONObject jsonObjectMovieDB = null;
        try {
            jsonObjectMovieDB = new JSONObject(dataStringJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jsonObjectMovieDB != null) {
            this.listMovie = MovieDBJSONResultHelper.getMovieList(jsonObjectMovieDB);
            this.TOTAL_PAGES = MovieDBJSONResultHelper.getTotalPages(jsonObjectMovieDB);
            this.TOTAL_RESULTS = MovieDBJSONResultHelper.getTotalResults(jsonObjectMovieDB);
        }

        adapterPopularMovie.updateListMovie(this.listMovie);
        rv_listPopularMovie.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        String type = this.typeMovie;
        outState.putString(MOVIEDB_QUERY_EXTRA, type);
    }

    @Override
    public void onListItemPopularMovieClick(int position) {
        Intent intent = new Intent(this, MovieDetailActivity.class);
        Movie movie = this.listMovie.get(position);
        intent.putExtra("movie", movie);
        this.startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_get_favorites:
                refreshView(REFRESH_FAVORITE);
                setTitle("Favorite Movies");
                break;
            case R.id.action_get_popular:
                refreshView(REFRESH_POPULAR);
                setTitle("Popular Movies");
                break;
            case R.id.action_get_top:
                refreshView(REFRESH_TOP);
                setTitle("Top Rated Movies");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshView(String refreshId){
        if (!refreshId.equals(REFRESH_FAVORITE)){
            loadMovie(refreshId, 1);
        } else{
            //TODO bikin favorit
        }
    }
}

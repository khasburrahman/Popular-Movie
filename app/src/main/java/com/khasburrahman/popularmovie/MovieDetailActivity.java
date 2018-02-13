package com.khasburrahman.popularmovie;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.khasburrahman.popularmovie.db.FavoriteMovieDBHelper;
import com.khasburrahman.popularmovie.model.Movie;
import com.khasburrahman.popularmovie.model.Reviews;
import com.khasburrahman.popularmovie.model.VideoMovieDB;
import com.khasburrahman.popularmovie.utility.FavoriteMovieHelper;
import com.khasburrahman.popularmovie.utility.MovieDBJSONResultHelper;
import com.khasburrahman.popularmovie.utility.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MovieDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {
    TextView tv_title;
    TextView tv_releasse_date;
    TextView tv_running_time;
    TextView tv_rating;
    Button btn_favorite;
    ImageView iv_poster;
    TextView tv_synopsis;
    boolean isFavorite;
    SharedPreferences sharedPreferences;
    boolean isLoadedReview = false;
    boolean isLoadedVideos = false;

    Movie movie;

    ProgressBar progressBarReview;
    ProgressBar progressBarVideo;
    LinearLayout containerReview;
    LinearLayout containerVideo;
    TextView tv_reviewnone;
    TextView tv_videonone;

    ArrayList<Reviews> mReview = new ArrayList<>();
    ArrayList<VideoMovieDB> mVideo = new ArrayList<>();

    final static int ID_LOADER_REVIEW = 2;
    final static int ID_LOADER_VIDEO =895;

    final static String ID_LOADER_EXTRA_URL = "url";
    FavoriteMovieDBHelper mDatabaseFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("LIFECYCLE", "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        setTitle("Detail Movie");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tv_rating = findViewById(R.id.tv_rating);
        tv_releasse_date = findViewById(R.id.tv_release_date);
        tv_running_time = findViewById(R.id.tv_running_time);
        tv_title = findViewById(R.id.tv_movie_title);
        tv_synopsis = findViewById(R.id.tv_synopsis);
        iv_poster = findViewById(R.id.iv_detail_movie_poster);
        btn_favorite = findViewById(R.id.btn_mark_favorite);
        progressBarReview = findViewById(R.id.pb_review);
        progressBarVideo = findViewById(R.id.pb_trailer);
        containerReview = findViewById(R.id.container_reviews);
        containerVideo = findViewById(R.id.container_trailer);
        tv_reviewnone = findViewById(R.id.tv_staus_review_not_available);
        tv_videonone = findViewById(R.id.tv_staus_trailer_not_available);

        sharedPreferences = getSharedPreferences(MainActivity.ID_APP_SHARED_PREF, MODE_PRIVATE);
        mDatabaseFavorite = new FavoriteMovieDBHelper(this);

        //get parceable dari aktivitas sebelumnya
        movie = getIntent().getExtras().getParcelable("movie");
        //get apakah film ini favorite
        isFavorite = getIntent().getExtras().getBoolean("isfavorite");

        if (isFavorite){
            btn_favorite.setText("Delete as Favorite");
        }

        btn_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFavorite){
                    mDatabaseFavorite.addFavoriteMovie(movie);
                    btn_favorite.setText("Delete as Favorite");
                    Toast.makeText(MovieDetailActivity.this, "Added as Favorite", Toast.LENGTH_SHORT).show();
                } else {
                    mDatabaseFavorite.deleteFavoriteMovie(movie.getId());
                    btn_favorite.setText("Mark as Favorite");
                    Toast.makeText(MovieDetailActivity.this, "Removed as Favorite", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (movie != null) {
            String posterFullPath = NetworkUtils.getMovieDBPosterStringPath(movie.getPoster_path());
            Picasso.with(this).load(posterFullPath).into(iv_poster);
            tv_title.setText(movie.getTitle());
            tv_synopsis.setText(movie.getOverview());
            tv_rating.setText(movie.getVote_average()+" / 10");
            tv_releasse_date.setText(movie.getRelease_date());
            tv_running_time.setText(movie.getOriginal_language());
        } else {
            Toast.makeText(this, "Error saat mengambil parceable", Toast.LENGTH_SHORT).show();
        }

        if (savedInstanceState != null){
            this.isLoadedVideos = savedInstanceState.getBoolean("isLoadedVideos");
            this.isLoadedReview = savedInstanceState.getBoolean("isLoadedReview");
        }


    }

    private void loadData(){
        Log.d("LIFECYCLE REVIEW", "loadData: ");
        String apiKey = getResources().getString(R.string.moviedb_api_3_key);
        URL apiURLReview = NetworkUtils.buildURL(NetworkUtils.REVIEWS, 1, apiKey, "en-US", "ID", movie.getId());

        if (apiURLReview == null){
            return;
        }

        Bundle queryBundle = new Bundle();
        queryBundle.putString(ID_LOADER_EXTRA_URL, apiURLReview.toString());

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> loader = loaderManager.getLoader(ID_LOADER_REVIEW);

        if (loader == null){
            loaderManager.initLoader(ID_LOADER_REVIEW, queryBundle, this);
        } else {
            loaderManager.restartLoader(ID_LOADER_REVIEW, queryBundle, this);
        }
    }

    private void loadVideos(){
        Log.d("LIFECYCLE VIDEOS", "loadData: ");
        String apiKey = getResources().getString(R.string.moviedb_api_3_key);
        URL apiURLVideo = NetworkUtils.buildURL(NetworkUtils.VIDEOS, 1, apiKey, "en-US", "ID", movie.getId());

        if (apiURLVideo == null){
            return;
        }

        Bundle queryBundle = new Bundle();
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> loader = loaderManager.getLoader(ID_LOADER_VIDEO);

        queryBundle.putString(ID_LOADER_EXTRA_URL, apiURLVideo.toString());


        if (loader == null){
            loaderManager.initLoader(ID_LOADER_VIDEO, queryBundle, this);
        } else {
            loaderManager.restartLoader(ID_LOADER_VIDEO, queryBundle, this);
        }
    }

    @Override
    protected void onStart() {
        Log.d("LIFECYCLE", "onStart: isLoadedReview : "+this.isLoadedReview+", isLoadedVideos "+this.isLoadedVideos);
        super.onStart();
        boolean isOnline = NetworkUtils.isOnline((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE));
        if (isOnline) {
            if (!this.isLoadedReview)
                loadData();
            if (!this.isLoadedVideos)
                loadVideos();
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<String> onCreateLoader(final int id,final Bundle args) {
        return new AsyncTaskLoader<String>(this) {
            @Override
            protected void onStartLoading() {
                if (args == null) {
                    return;
                }
                if (id == ID_LOADER_REVIEW) {
                    progressBarReview.setVisibility(View.VISIBLE);
                    containerReview.setVisibility(View.INVISIBLE);
                }
                if (id == ID_LOADER_VIDEO) {
                    progressBarVideo.setVisibility(View.VISIBLE);
                    containerVideo.setVisibility(View.INVISIBLE);
                }
                forceLoad();
            }

            @Override
            public String loadInBackground() {
                String queryURLString = args.getString(ID_LOADER_EXTRA_URL);
                if (queryURLString == null || TextUtils.isEmpty(queryURLString)) {
                    return null;
                }
                try {
                    URL queryURL = new URL(queryURLString);
                    String result = NetworkUtils.getResponseFromHTTPUrl(queryURL);
                    return result;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        int id = loader.getId();

        JSONObject jsonObjectMovieDB = null;
        try {
            jsonObjectMovieDB = new JSONObject(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (jsonObjectMovieDB != null) {
            //if load the detail movie, (favorite)
            if (id == ID_LOADER_REVIEW){
                this.mReview = MovieDBJSONResultHelper.getReviewList(jsonObjectMovieDB);
                updateReviewView();
            } else if (id == ID_LOADER_VIDEO) {
                this.mVideo = MovieDBJSONResultHelper.getVideoList(jsonObjectMovieDB);
                updateVideoView();
            }
        }

    }

    private void updateReviewView(){
        if (this.mReview != null && this.mReview.size() > 0){
            this.progressBarReview.setVisibility(View.INVISIBLE);
            this.containerReview.setVisibility(View.VISIBLE);
            for(Reviews reviews : this.mReview){
                LayoutInflater layoutInflater = LayoutInflater.from(this);
                int layout = R.layout.item_review;
                View view = layoutInflater.inflate(R.layout.item_review, null, false);
                TextView textAuthor = view.findViewById(R.id.tv_review_author);
                textAuthor.setText(reviews.author);
                TextView textContent = view.findViewById(R.id.tv_review_content);
                textContent.setText(reviews.content);
                this.containerReview.addView(view);
            }
        } else {
            this.containerReview.setVisibility(View.INVISIBLE);
            this.progressBarReview.setVisibility(View.INVISIBLE);
            this.tv_reviewnone.setVisibility(View.VISIBLE);
            Log.d("UPDATE REVIEW", "updateReviewView: data ga ada");
        }
        this.isLoadedReview = true;
    }

    private void updateVideoView(){
        if (this.mVideo != null && this.mVideo.size() > 0){
            this.progressBarVideo.setVisibility(View.INVISIBLE);
            this.containerVideo.setVisibility(View.VISIBLE);
            for(VideoMovieDB video : this.mVideo){
                LayoutInflater layoutInflater = LayoutInflater.from(this);
                View view = layoutInflater.inflate(R.layout.item_trailers, null, false);
                TextView textTrailer = view.findViewById(R.id.tv_trailer_name);
                textTrailer.setText(video.name);
                final String key = video.key;
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v="+key));
                        Intent chooser = Intent.createChooser(intent, "Open With..");
                        if (intent.resolveActivity(getPackageManager()) != null){
                            startActivity(chooser);
                        }
                    }
                });
                this.containerVideo.addView(view);
            }
        } else {
            this.containerVideo.setVisibility(View.INVISIBLE);
            this.progressBarVideo.setVisibility(View.INVISIBLE);
            this.tv_videonone.setVisibility(View.VISIBLE);
            Log.d("UPDATE VIDEO", "updateVideoView: data ga ada");
        }
        this.isLoadedVideos = true;
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

    @Override
    protected void onResume() {
        Log.d("LIFECYCLE", "onResume: ");
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d("LIFECYCLE", "onSaveInstanceState: ");
        super.onSaveInstanceState(outState);
        outState.putBoolean("isLoadedReview", false);
        outState.putBoolean("isLoadedVideos", false);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.d("LIFECYCLE", "onRestoreInstanceState: ");
        super.onRestoreInstanceState(savedInstanceState);
        this.isLoadedVideos = savedInstanceState.getBoolean("isLoadedVideos");
        this.isLoadedReview = savedInstanceState.getBoolean("isLoadedReview");
    }

    @Override
    protected void onPause() {
        Log.d("LIFECYCLE", "onPause: ");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d("LIFECYCLE", "onStop: ");
        this.containerVideo.removeAllViews();
        this.containerReview.removeAllViews();
        this.isLoadedVideos = false;
        this.isLoadedReview = false;
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        Log.d("LIFECYCLE", "onDestroy: ");
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }
}

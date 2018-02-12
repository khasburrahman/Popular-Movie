package com.khasburrahman.popularmovie;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
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
    final static int ID_LOADER_VIDEO =1;

    final static String ID_LOADER_EXTRA_URL = "url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
                    FavoriteMovieHelper.addMoviesIdToFavorite(movie.getId(), sharedPreferences);
                    btn_favorite.setText("Delete as Favorite");
                    Toast.makeText(MovieDetailActivity.this, "Added as Favorite", Toast.LENGTH_SHORT).show();
                } else {
                    FavoriteMovieHelper.removeMovieIdFromFavorite(movie.getId(), sharedPreferences);
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


    }

    private void loadData(){
        String apiKey = getResources().getString(R.string.moviedb_api_3_key);
        URL apiURLReview = NetworkUtils.buildURL(NetworkUtils.REVIEWS, 1, apiKey, "en-US", "ID", movie.getId());
        URL apiURLVideo = NetworkUtils.buildURL(NetworkUtils.VIDEOS, 1, apiKey, "en-US", "ID", movie.getId());

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

    @Override
    protected void onStart() {
        super.onStart();
        boolean isOnline = NetworkUtils.isOnline((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE));
        if (isOnline) {
            loadData();
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
    }

    private void updateVideoView(){

    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }
}

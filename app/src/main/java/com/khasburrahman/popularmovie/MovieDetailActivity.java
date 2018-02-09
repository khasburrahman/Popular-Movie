package com.khasburrahman.popularmovie;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.khasburrahman.popularmovie.model.Movie;
import com.khasburrahman.popularmovie.utility.NetworkUtils;
import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends AppCompatActivity {
    TextView tv_title;
    TextView tv_releasse_date;
    TextView tv_running_time;
    TextView tv_rating;
    Button btn_favorite;
    ImageView iv_poster;
    TextView tv_synopsis;

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

        //get parceable dari aktivitas sebelumnya

        Movie movie = getIntent().getExtras().getParcelable("movie");

        if (movie != null) {
            String posterFullPath = NetworkUtils.getMovieDBPosterStringPath(movie.getPoster_path());
            Picasso.with(this).load(posterFullPath).into(iv_poster);
            tv_title.setText(movie.getTitle());
            tv_synopsis.setText(movie.getOverview());
            tv_rating.setText(movie.getVote_average()+" / 10");
            tv_releasse_date.setText(movie.getRelease_date());
        } else {
            Toast.makeText(this, "Error saat mengambil parceable", Toast.LENGTH_SHORT).show();
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
}

package com.khasburrahman.popularmovie.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.khasburrahman.popularmovie.R;
import com.khasburrahman.popularmovie.model.Movie;
import com.khasburrahman.popularmovie.utility.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Kharis on 07-Feb-18.
 */

public class AdapterPopularMovie extends RecyclerView.Adapter<AdapterPopularMovie.ViewHolderPopularMovie>{
    Context context;
    ArrayList<Movie> listMovie;
    PopularMovieItemClickListener listener;

    public AdapterPopularMovie(Context context, ArrayList<Movie> listMovie, PopularMovieItemClickListener listener){
        this.context = context;
        this.listMovie = listMovie;
        this.listener = listener;
    }

    @Override
    public ViewHolderPopularMovie onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_popularmovie, parent, false);
        ViewHolderPopularMovie viewHolderPopularMovie = new ViewHolderPopularMovie(view);

        return viewHolderPopularMovie;
    }

    @Override
    public void onBindViewHolder(ViewHolderPopularMovie holder, int position) {
        String posterPath = this.listMovie.get(position).getPoster_path();
        String posterFullPath = NetworkUtils.getMovieDBPosterStringPath(posterPath);

        //load image with picasso
        Picasso.with(context).load(posterFullPath).into(holder.iv_poster);
    }

    @Override
    public int getItemCount() {
        return listMovie.size();
    }

    public void updateListMovie(ArrayList<Movie> listMovie){
        this.listMovie = listMovie;
        notifyDataSetChanged();
    }

    class ViewHolderPopularMovie extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView iv_poster;
        FrameLayout frameLayout;
        public ViewHolderPopularMovie(View itemView) {
            super(itemView);
            this.frameLayout = itemView.findViewById(R.id.frame_layout);

            this.iv_poster = (ImageView) itemView.findViewById(R.id.iv_item_popularmovie);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int itemPos = getAdapterPosition();
            listener.onListItemPopularMovieClick(itemPos);
        }
    }

    public interface PopularMovieItemClickListener{
        void onListItemPopularMovieClick(int param);
    }
}

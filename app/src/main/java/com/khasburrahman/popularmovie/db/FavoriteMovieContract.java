package com.khasburrahman.popularmovie.db;

import android.provider.BaseColumns;

/**
 * Created by Kharis on 12-Feb-18.
 */

public class FavoriteMovieContract {
    private FavoriteMovieContract(){};
    public static final class FavoriteMovieEntry implements BaseColumns{
        public static final String TABLE_NAME = "favorite";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_VOTE_AVERAGE = "get_vote_average";
        public static final String COLUMN_ORIGINAL_LANGUAGE = "get_original_language";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_POSTER_PATH = "poster";
    }
}

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
    }
}

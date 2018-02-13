package com.khasburrahman.popularmovie.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.khasburrahman.popularmovie.model.Movie;

import java.util.ArrayList;

/**
 * Created by Kharis on 12-Feb-18.
 */

public class FavoriteMovieDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "movie.db";
    private static final int DB_VERSION = 1;

    public FavoriteMovieDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_TABLE = "CREATE TABLE " + FavoriteMovieContract.FavoriteMovieEntry.TABLE_NAME + " (" +
                FavoriteMovieContract.FavoriteMovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                FavoriteMovieContract.FavoriteMovieEntry.COLUMN_ID + " TEXT NOT NULL," +
                FavoriteMovieContract.FavoriteMovieEntry.COLUMN_ORIGINAL_LANGUAGE + " TEXT NOT NULL," +
                FavoriteMovieContract.FavoriteMovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL," +
                FavoriteMovieContract.FavoriteMovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL," +
                FavoriteMovieContract.FavoriteMovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL," +
                FavoriteMovieContract.FavoriteMovieEntry.COLUMN_VOTE_AVERAGE + " TEXT NOT NULL," +
                FavoriteMovieContract.FavoriteMovieEntry.COLUMN_TITLE + " TEXT NOT NULL" +
                ");";
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavoriteMovieContract.FavoriteMovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public long addFavoriteMovie(Movie movie){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_ID, movie.getId());
        cv.put(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_ORIGINAL_LANGUAGE, movie.getOriginal_language());
        cv.put(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_TITLE, movie.getTitle());
        cv.put(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_POSTER_PATH, movie.getPoster_path());
        cv.put(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_VOTE_AVERAGE, movie.getVote_average());
        cv.put(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_OVERVIEW, movie.getOverview());
        cv.put(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_RELEASE_DATE, movie.getRelease_date());
        long res = sqLiteDatabase.insert(FavoriteMovieContract.FavoriteMovieEntry.TABLE_NAME, null, cv);
        sqLiteDatabase.close();
        return res;
    }

    public boolean deleteFavoriteMovie(String movieId){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        boolean  res =  sqLiteDatabase.delete(FavoriteMovieContract.FavoriteMovieEntry.TABLE_NAME,
                FavoriteMovieContract.FavoriteMovieEntry.COLUMN_ID + "=" + movieId,
                null) > 0;
        sqLiteDatabase.close();
        return res;
    }

    public ArrayList<Movie> getAllMovie(){
        ArrayList<Movie> list = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(FavoriteMovieContract.FavoriteMovieEntry.TABLE_NAME,
                null, null,null,null,null,null);
        try {
            while (cursor.moveToNext()) {
                String title = cursor.getString(cursor.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_TITLE));
                String overview = cursor.getString(cursor.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_OVERVIEW));
                String ori_language = cursor.getString(cursor.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_ORIGINAL_LANGUAGE));
                String movieId = cursor.getString(cursor.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_ID));
                String posterPath = cursor.getString(cursor.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_POSTER_PATH));
                String voteavg = cursor.getString(cursor.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_VOTE_AVERAGE));
                String releasedate = cursor.getString(cursor.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_RELEASE_DATE));
                list.add(new Movie(movieId, title, ori_language,releasedate,posterPath, overview, voteavg));
            }
        } finally {
            cursor.close();
            sqLiteDatabase.close();
        }
        return list;
    }

    public boolean isFavorite(String movieId){
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(FavoriteMovieContract.FavoriteMovieEntry.TABLE_NAME,
                null, FavoriteMovieContract.FavoriteMovieEntry.COLUMN_ID+" = ?",new String[] {movieId},null,null,null);
        boolean result = false;
        if (cursor != null){
            if (cursor.getCount() > 0){
                result = true;
            }
        }
        cursor.close();
        sqLiteDatabase.close();
        return result;
    }

}

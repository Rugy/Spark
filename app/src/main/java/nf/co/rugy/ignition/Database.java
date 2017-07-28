package nf.co.rugy.ignition;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andreas on 06.07.2017.
 */

public class Database extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "song.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLENAME = "Song_Table";
    private static final String COL_ID = "Song_ID";
    private final static String COL_ARTIST = "Song_Artist";
    private static final String COL_TITLE = "Song_Title";
    private static final String COL_YEAR = "Song_Year";
    private static final String COL_RANK = "Song_Rank";
    private static final String COL_TEXT = "Song_Text";

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY, %s TEXT, %s " +
                        "TEXT, %s INTEGER, %s INTEGER, %s TEXT)", TABLENAME, COL_ID, COL_ARTIST,
                COL_TITLE, COL_YEAR, COL_RANK, COL_TEXT);
        db.execSQL(createTable);
    }

    public void storeSong(Song song) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_ARTIST, song.getArtist());
        contentValues.put(COL_TITLE, song.getTitle());
        contentValues.put(COL_YEAR, song.getYear());
        contentValues.put(COL_RANK, song.getPlace());
        contentValues.put(COL_TEXT, song.getStringText());

        long rowId = db.insert(TABLENAME, null, contentValues);
        Log.d(StartMenuActivity.DEBUGTAG, String.valueOf(rowId));
    }

    public Song getSongFromTitle(String title) {
        SQLiteDatabase db = getReadableDatabase();
        Song song = new Song();

        String[] projection = {
                COL_ID,
                COL_ARTIST,
                COL_TITLE,
                COL_YEAR,
                COL_RANK,
                COL_TEXT
        };

        String selection = COL_TITLE + " = ?";
        String[] selectionArgs = {title};

        Cursor cursor = db.query(
                TABLENAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor.moveToNext()) {
            song.setId(cursor.getInt(cursor.getColumnIndex(COL_ID)));
            song.setArtist(cursor.getString(cursor.getColumnIndex(COL_ARTIST)));
            song.setTitle(cursor.getString(cursor.getColumnIndex(COL_TITLE)));
            song.setYear(cursor.getInt(cursor.getColumnIndex(COL_YEAR)));
            song.setPlace(cursor.getInt(cursor.getColumnIndex(COL_RANK)));
            song.setStringText(cursor.getString(cursor.getColumnIndex(COL_TEXT)));
        } else {
            song = null;
        }

        return song;
    }

    public Song getSongFromId(int Id) {
        SQLiteDatabase db = getReadableDatabase();
        Song song = new Song();

        String[] projection = {
                COL_ID,
                COL_ARTIST,
                COL_TITLE,
                COL_YEAR,
                COL_RANK,
                COL_TEXT
        };

        String selection = COL_ID + " = ?";
        String[] selectionArgs = {String.valueOf(Id)};

        Cursor cursor = db.query(
                TABLENAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor.moveToNext()) {
            song.setId(cursor.getInt(cursor.getColumnIndex(COL_ID)));
            song.setArtist(cursor.getString(cursor.getColumnIndex(COL_ARTIST)));
            song.setTitle(cursor.getString(cursor.getColumnIndex(COL_TITLE)));
            song.setYear(cursor.getInt(cursor.getColumnIndex(COL_YEAR)));
            song.setPlace(cursor.getInt(cursor.getColumnIndex(COL_RANK)));
            song.setStringText(cursor.getString(cursor.getColumnIndex(COL_TEXT)));
        }

        return song;
    }

    public List<Song> getAllSongs() {
        List<Song> songs = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        String sql = String.format("SELECT %s, %s, %s, %s, %s, %s FROM %s", COL_ID, COL_ARTIST,
                COL_TITLE, COL_YEAR,
                COL_RANK, COL_TEXT, TABLENAME);
        Cursor cursor = db.rawQuery(sql, null);

        while (cursor.moveToNext()) {
            Song song = new Song();
            song.setId(cursor.getInt(0));
            song.setArtist(cursor.getString(1));
            song.setTitle(cursor.getString(2));
            song.setYear(cursor.getInt(3));
            song.setPlace(cursor.getInt(4));
            song.setStringText(cursor.getString(5));
            songs.add(song);
        }
        db.close();

        return songs;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}

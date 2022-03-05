package landau.sweb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import android.database.*;
import landau.sweb.utils.*;
import java.util.*;

public class PlacesDbHelper extends SQLiteOpenHelper {
    private static final int CURRENT_VERSION = 1;
	
	private static final String TABLE_NAME = "userscripts";

	private static final String COLUMN_ID = "_id";
	private static final String COLUMN_DATA = "data";
	private static final String COLUMN_ENABLED = "enabled";
	private static final String COLUMN_TITLE = "title";
	private static final int COLUMN_ID_INDEX = 0;
	private static final int COLUMN_DATA_INDEX = 1;
	private static final int COLUMN_ENABLED_INDEX = 2;
	
    PlacesDbHelper(Context context) {
        super(context, new File(context.getExternalFilesDir(null), "places.sqlite").getAbsolutePath(), null, CURRENT_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        execSql(db, "CREATE TABLE bookmarks (_id INTEGER PRIMARY KEY, title TEXT, url TEXT UNIQUE)");
		execSql(db, "CREATE TABLE history (_id INTEGER PRIMARY KEY, title TEXT, url TEXT, date_created datetime default CURRENT_TIMESTAMP, CONSTRAINT unidate UNIQUE (url, date_created))");
		execSql(db, "CREATE TABLE " + TABLE_NAME + " (" +
				COLUMN_ID + " INTEGER PRIMARY KEY" +
				", " + COLUMN_TITLE + " TEXT NOT NULL" +
				", " + COLUMN_DATA + " TEXT NOT NULL" +
				", " + COLUMN_ENABLED + " INTEGER DEFAULT 1" +
				")");
	}

	private void execSql(SQLiteDatabase db, String s) throws SQLException {
		ExceptionLogger.d("PlacesDbHelper", s);
		db.beginTransaction();
        try {
            db.execSQL(s);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
	}

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
    }
	
	
}

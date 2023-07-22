package net.gnu.common;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.io.File;

public class PlacesDbHelper extends SQLiteOpenHelper {
    public static final int CURRENT_VERSION = 3;

	private static final String TABLE_NAME = "userscripts";

	private static final String COLUMN_ID = "_id";
	private static final String COLUMN_DATA = "data";
	private static final String COLUMN_ENABLED = "enabled";
	private static final String COLUMN_TITLE = "title";
	private static final int COLUMN_ID_INDEX = 0;
	private static final int COLUMN_DATA_INDEX = 1;
	private static final int COLUMN_ENABLED_INDEX = 2;

    public PlacesDbHelper(final Context context) {
        super(context, new File(context.getExternalFilesDir(null), "places.sqlite").getAbsolutePath(), null, CURRENT_VERSION);
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        execSql(db, "CREATE TABLE bookmarks (_id INTEGER PRIMARY KEY, title TEXT, url TEXT UNIQUE)");
		execSql(db, "CREATE TABLE history (_id INTEGER PRIMARY KEY, title TEXT, url TEXT UNIQUE, date_created datetime default CURRENT_TIMESTAMP)");//, CONSTRAINT unidate UNIQUE (url, date_created))");
		execSql(db, "CREATE TABLE " + TABLE_NAME + " (" +
				COLUMN_ID + " INTEGER PRIMARY KEY" +
				", " + COLUMN_TITLE + " TEXT NOT NULL" +
				", " + COLUMN_DATA + " TEXT NOT NULL" +
				", " + COLUMN_ENABLED + " INTEGER DEFAULT 1" +
				")");
//		execSql(db, "CREATE TABLE reader_history (_id INTEGER PRIMARY KEY, url TEXT UNIQUE, title TEXT)");
	}

	private void execSql(final SQLiteDatabase db, final String s) throws SQLException {
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

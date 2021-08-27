package landau.sweb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;

public class PlacesDbHelper extends SQLiteOpenHelper {
    private static final int CURRENT_VERSION = 1;

    PlacesDbHelper(Context context) {
        super(context, new File(context.getExternalFilesDir(null), "places.sqlite").getAbsolutePath(), null, CURRENT_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE bookmarks (_id INTEGER PRIMARY KEY, title TEXT, url TEXT UNIQUE)");
		db.execSQL("CREATE TABLE history (_id INTEGER PRIMARY KEY, title TEXT, url TEXT, date_created datetime default CURRENT_TIMESTAMP, CONSTRAINT unidate UNIQUE (url, date_created))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
    }
}

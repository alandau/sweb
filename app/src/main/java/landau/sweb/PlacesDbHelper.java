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
        db.execSQL("CREATE TABLE bookmarks (id INTEGER PRIMARY KEY, title TEXT, url TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

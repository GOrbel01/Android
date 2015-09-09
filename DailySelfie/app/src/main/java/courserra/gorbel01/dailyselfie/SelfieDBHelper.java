package courserra.gorbel01.dailyselfie;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Cloud on 23/08/2015.
 */
public class SelfieDBHelper extends SQLiteOpenHelper {
    final static String TABLE_NAME = "image_data";
    final static String _ID = "id";
    final static String PATH = "imagePath";
    final static String NAME = "displayName";
    final static String[] columns = { _ID, PATH, NAME };

    final private static String CREATE_DB =
        "CREATE TABLE " + TABLE_NAME + "(" + _ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + PATH + " TEXT NOT NULL, "
            + NAME + " TEXT NOT NULL)";

    final private static String DB_NAME = "selfie_db";
    final private static Integer VERSION = 1;
    final private Context mContext;

    public SelfieDBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    void deleteDatabase() {
        mContext.deleteDatabase(DB_NAME);
    }
}

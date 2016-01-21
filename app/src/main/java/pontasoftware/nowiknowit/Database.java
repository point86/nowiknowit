package pontasoftware.nowiknowit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by paolo on 01/09/2015.
 */
public class Database extends SQLiteOpenHelper{
    private final String TAG = "Database";
    private Context context;
    public static final class History implements BaseColumns{
        private History() {}
        public static final String COLLEGIATE_TABLE = "COLLEGIATE";
        public static final String LEARNERS_TABLE = "LEARNERS";
        public static final String WORD = "WORD";
        public static final String DEF = "DEF";
        public static final String NUM_SEARCHED = "NSEARCH"; //searched times
        public static final String NUM_CORRECT = "NCORRECT"; //correct answers
        private static final String CREATE_COLLEGIATE_TABLE = "CREATE TABLE " +
                COLLEGIATE_TABLE + " (" +
                _ID  + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                WORD + " TEXT NOT NULL, " +
                DEF  + " TEXT NOT NULL, "+
                NUM_SEARCHED + " INT, "+
                NUM_CORRECT  + " INT)";
        private static final String CREATE_LEARNERS_TABLE = "CREATE TABLE " +
                LEARNERS_TABLE + " (" +
                _ID  + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                WORD + " TEXT NOT NULL, " +
                DEF  + " TEXT NOT NULL, "+
                NUM_SEARCHED + " INT, "+
                NUM_CORRECT  + " INT)";
        //private static final String SQL_DELETE_ENTRIES = "DROP TABLE " + HISTORY_TABLE;//FIXME correct?
    }

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "data.db";

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(History.CREATE_COLLEGIATE_TABLE);
        db.execSQL(History.CREATE_LEARNERS_TABLE);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        //db.execSQL(SQL_DELETE_ENTRIES); TODO
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void insertHst(String word, String def, String table){//FIXME return value??
        SQLiteDatabase db = this.getWritableDatabase();
        String selectString = "SELECT * FROM " + table + " WHERE " + History.WORD + " =?";
        Cursor cursor = db.rawQuery(selectString, new String[] {word});
        if (cursor.moveToFirst()){ //value already present, we have only to update some columns
            Log.d(TAG, "-" +word + "- update columns");
            Integer oldV = cursor.getInt(cursor.getColumnIndexOrThrow(History.NUM_SEARCHED));
            ContentValues cv = new ContentValues();
            cv.put(History.NUM_SEARCHED, oldV + 1);
            db.update(table, cv, History.WORD + " = '" + word +"'", null);
        }
        else {
            Log.d(TAG, "-" +word + "- inserting it");
            ContentValues values = new ContentValues();
            values.put(History.WORD, word);
            values.put(History.DEF, def);
            values.put(History.NUM_SEARCHED, 1);
            values.put(History.NUM_CORRECT, 0);
            // Insert the new row, returning the primary key value of the new row
            db.insert(table, null, values);
        }
        db.close();
    }

    public void removeHst(String[] words, String table){
        SQLiteDatabase db = this.getWritableDatabase();
        for (String word: words) {
            //FIXME wrap in String[] isn't overloading????
            db.delete(table, Database.History.WORD + " = ?", new String[] {word});
        }
        db.close();
    }
}

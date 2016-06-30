package pontasoftware.nowiknowit;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * Created by paolo on 01/09/2015.
 */
public class DatabaseOpenHelper extends SQLiteOpenHelper  {
    public static final Uri URI_DB = Uri.parse("sqlite://pontasoftware.nowiknowit/table");
    private final String TAG = "DatabaseOpenHelper";
    private Context context;
    public static DatabaseOpenHelper sInstance = null;

    public static final class History implements BaseColumns{
        private History() {}
        public static final String HISTORY_TABLE = "HISTORY";

        public static final String WORD = "WORD";
        public static final String DEF = "DEF";
        public static final String DICT_NAME = "DICT_NAME"; //dictionary name (type)
        public static final String NUM_SEARCHED = "NUM_SEARCHED"; //searched times
        public static final String NUM_CORRECT = "NUM_CORRECT"; //correct answers
        private static final String CREATE_HISTORY_TABLE = "CREATE TABLE " +
                HISTORY_TABLE + " (" +
                _ID  + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                WORD + " TEXT NOT NULL, " +
                DICT_NAME + " TEXT NOT NULL, " +
                DEF  + " TEXT NOT NULL, "+
                NUM_SEARCHED + " INT, "+
                NUM_CORRECT  + " INT)";

        //private static final String SQL_DELETE_ENTRIES = "DROP TABLE " + HISTORY_TABLE;//FIXME correct?
    }

    // If you change the databaseOpenHelper schema, you must increment the databaseOpenHelper version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "data.db";

    /**
            * constructor should be private to prevent direct instantiation.
            * make call to static factory method "getInstance()" instead.
    */
    private DatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(History.CREATE_HISTORY_TABLE);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This databaseOpenHelper is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        //db.execSQL(SQL_DELETE_ENTRIES); TODO
        db.execSQL("DROP TABLE IF EXISTS " + History.HISTORY_TABLE);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
    public static DatabaseOpenHelper getInstance(Context ctx) {
        /**
         * use the application context as suggested by CommonsWare.
         * this will ensure that you dont accidentally leak an Activitys
         * context (see this article for more information:
         * http://developer.android.com/resources/articles/avoiding-memory-leaks.html)
         */
        if (sInstance == null) {
            sInstance = new DatabaseOpenHelper(ctx.getApplicationContext());
        }
        return sInstance;
    }
    public void insertHst(String word, String def, String dictionary){//FIXME return value?? table?
        String selectString = "SELECT * FROM " + DatabaseOpenHelper.History.HISTORY_TABLE + " WHERE " + DatabaseOpenHelper.History.WORD + " =?";
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(selectString, new String[] {word});
        if (cursor.moveToFirst()){ //value already present, we have only to update some columns
            Log.d(TAG, "-" +word + "- update columns");
            Integer oldV = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseOpenHelper.History.NUM_SEARCHED));
            ContentValues cv = new ContentValues();
            cv.put(DatabaseOpenHelper.History.NUM_SEARCHED, oldV + 1);
            db.update(DatabaseOpenHelper.History.HISTORY_TABLE, cv, DatabaseOpenHelper.History.WORD + " = '" + word +"'", null);
        }
        else {
            Log.d(TAG, "-" +word + "- inserting it");
            ContentValues values = new ContentValues();
            values.put(DatabaseOpenHelper.History.WORD, word);
            values.put(DatabaseOpenHelper.History.DEF, def);
            values.put(DatabaseOpenHelper.History.NUM_SEARCHED, 1);
            values.put(DatabaseOpenHelper.History.NUM_CORRECT, 0);
            values.put(DatabaseOpenHelper.History.DICT_NAME, dictionary);
            // Insert the new row, returning the primary key value of the new row
            db.insert(DatabaseOpenHelper.History.HISTORY_TABLE, null, values);
        }
        db.close();
        sendBroadcastNotification();
    }

    public void removeHst(String[] words, String table){
        SQLiteDatabase db = getWritableDatabase();
        for (String word: words) {
            db.delete(table, DatabaseOpenHelper.History.WORD + " = ?", new String[] {word});
        }
        db.close();
        sendBroadcastNotification();
    }
    public void sendBroadcastNotification(){
        Log.d(TAG, "sendBroadcastNotification()");
        Intent intent = new Intent("databaseOpenHelper-modified");
        // You can also include some extra data.
        intent.putExtra("message", "maybe put some usefel object?"); //FIXME
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}

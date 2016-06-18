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

import java.util.Observable;

/**
 * Created by paolo on 01/09/2015.
 */
public class Database extends SQLiteOpenHelper  {
    public static final Uri URI_DB = Uri.parse("sqlite://pontasoftware.nowiknowit/table");
    private final String TAG = "Database";
    private Context context;
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

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "data.db";

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(History.CREATE_HISTORY_TABLE);
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

    //every time the user perform a search, if that term is not present in the local history, database
    //will be updated, but the column NUM_SEARCHED must be the same for all rows.
    public void insertHst(String word, String def, String dictionary){//FIXME return value?? table?
        SQLiteDatabase db = this.getWritableDatabase();
        String selectString = "SELECT * FROM " + History.HISTORY_TABLE + " WHERE " + History.WORD + " =?";
        Cursor cursor = db.rawQuery(selectString, new String[] {word});
        if (cursor.moveToFirst()){ //value already present, we have only to update some columns
            Log.d(TAG, "-" +word + "- update columns");
            Integer oldV = cursor.getInt(cursor.getColumnIndexOrThrow(History.NUM_SEARCHED));
            ContentValues cv = new ContentValues();
            cv.put(History.NUM_SEARCHED, oldV + 1);
            db.update(History.HISTORY_TABLE, cv, History.WORD + " = '" + word +"'", null);
        }
        else {
            Log.d(TAG, "-" +word + "- inserting it");
            ContentValues values = new ContentValues();
            values.put(History.WORD, word);
            values.put(History.DEF, def);
            values.put(History.NUM_SEARCHED, 1);
            values.put(History.NUM_CORRECT, 0);
            values.put(History.DICT_NAME, dictionary);
            // Insert the new row, returning the primary key value of the new row
            db.insert(History.HISTORY_TABLE, null, values);
        }
        db.close();
        sendBroadcastNotification();
    }

    public void removeHst(String[] words, String table){
        SQLiteDatabase db = this.getWritableDatabase();
        //FIXME is the for cycle really necessary? see String[] ;)
        for (String word: words) {
            db.delete(table, Database.History.WORD + " = ?", new String[] {word});
        }
        db.close(); //FIXME close it every time????
        sendBroadcastNotification();
    }

    public void sendBroadcastNotification(){
        Log.d(TAG, "sendBroadcastNotification()");
        Intent intent = new Intent("database-modified");
        // You can also include some extra data.
        intent.putExtra("message", "maybe put some usefel object?"); //FIXME
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

    }
}

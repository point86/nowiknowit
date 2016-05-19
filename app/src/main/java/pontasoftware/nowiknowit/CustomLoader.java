package pontasoftware.nowiknowit;

/**
 * Created by paolo on 27/03/2016.
 */

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

public class CustomLoader extends AsyncTaskLoader<Cursor> {
    private final String TAG = "CustomLoader";
    private Cursor data = null;
//    MyObserver obs = new MyObserver()

    public CustomLoader(Context ctx) {
        // Loaders may be used across multiple Activities (assuming they aren't
        // bound to the LoaderManager), so NEVER hold a reference to the context
        // directly. Doing so will cause you to leak an entire Activity's context.
        // The superclass constructor will store a reference to the Application
        // Context instead, and can be retrieved with a call to getContext().
        super(ctx);
    }
    @Override
    public Cursor loadInBackground() {
        Log.d(TAG, "loadInBackground()");
        // This method is called on a background thread and should generate a
        // new set of data to be delivered back to the client.
        Database database = new Database(getContext());

        //FIXME fix this mess with SQLiteOpenHelper vs my Database class
        //SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SQLiteDatabase db = database.getReadableDatabase();
        //same query that don't display thesaurus words: " WHERE (DICT_NAME <> "thesaurus") ORDER BY
        data = db.rawQuery("SELECT * FROM " + Database.History.HISTORY_TABLE + " ORDER BY " + Database.History._ID + " DESC;", null);
        //Log.d(TAG, "number of lines:" + data.getCount());
        //data.moveToFirst(); //FIXME it's really necessary?
        return data;
    }
    @Override public void deliverResult(Cursor c) {
        Log.d(TAG, "deliverResult()");
        data = c;
        super.deliverResult(data);
    }
    @Override protected void onStartLoading() {
        if (data != null)
            deliverResult(data);
        else {
            Log.d(TAG, "data is NULL!!!!!");
            forceLoad();
        }
        Log.d(TAG, "onStartLoading()");
    }
    @Override protected void onStopLoading() {
        Log.d(TAG, "onStopLoading()");
        //cancelLoad();
    }
    @Override public void onCanceled(Cursor c) {
        Log.d(TAG, "onCanceled()");
    }
    @Override protected void onReset() {
        Log.d(TAG, "onReset()");
    }
}

package pontasoftware.nowiknowit;

/**
 * Created by paolo on 27/03/2016.
 * I writed my own custom loader beacause CursorLoader (provided by the Android framework) need
 * a Uri to work with (a ContentProvider). However implement a ContentProvider is too much for this
 * app, it don't need to share date with other apps nor the internet.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.Observable;
import java.util.Observer;

public class CustomLoader extends AsyncTaskLoader<Cursor> {
    private final String TAG = "CustomLoader";
    private Cursor data = null;

    public CustomLoader(Context ctx) {
        // Loaders may be used across multiple Activities (assuming they aren't
        // bound to the LoaderManager), so NEVER hold a reference to the context
        // directly. Doing so will cause you to leak an entire Activity's context.
        // The superclass constructor will store a reference to the Application
        // Context instead, and can be retrieved with a call to getContext().
        super(ctx);
        LocalBroadcastManager.getInstance(ctx).registerReceiver(mMessageReceiver,
                new IntentFilter("database-modified"));
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");
            Log.d(TAG, "Got message: " + message);
            onContentChanged();
        }
    };

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
        //TODO register an observer http://www.androiddesignpatterns.com/2012/08/implementing-loaders.html

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

package pontasoftware.nowiknowit;

/**
 * Created by paolo on 27/03/2016.
 * I writed my own custom loader beacause CursorLoader (provided by the Android framework) need
 * a Uri to work with (a ContentProvider). However implement a ContentProvider is too much for this
 * app, it don't need to share date with other apps nor the internet.
 * Info to how to write  a loader:
 * http://www.androiddesignpatterns.com/2012/08/implementing-loaders.html
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
//        LocalBroadcastManager.getInstance(ctx).registerReceiver(observer,
//                new IntentFilter("databaseOpenHelper-modified"));
    }

    //this class is always present,
    private BroadcastReceiver observer = new BroadcastReceiver() {
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
        DatabaseOpenHelper databaseOpenHelper = new DatabaseOpenHelper(getContext());

        //FIXME fix this mess with SQLiteOpenHelper vs my DatabaseOpenHelper class
        //SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SQLiteDatabase db = databaseOpenHelper.getReadableDatabase();
        //same query that don't display thesaurus words: " WHERE (DICT_NAME <> "thesaurus") ORDER BY
        data = db.rawQuery("SELECT * FROM " + DatabaseOpenHelper.History.HISTORY_TABLE + " ORDER BY " + DatabaseOpenHelper.History._ID + " DESC;", null);

        //Log.d(TAG, "number of lines:" + data.getCount());
        //data.moveToFirst(); //FIXME it's really necessary?
        return data;
    }

    //delivers the result (data) to the listener. Will be passed to method onLoadFinished
    //in HistoryFragment
    @Override public void deliverResult(Cursor c) {
        Log.d(TAG, "deliverResult()");
        data = c;

        super.deliverResult(data);
    }
    @Override protected void onStartLoading() {
        if (data != null)
            //data is already present, we have only to deliver it.
            deliverResult(data);
        else {
            Log.d(TAG, "registerReceiver(observer....)");
            LocalBroadcastManager.getInstance(getContext()).registerReceiver(observer,
                    new IntentFilter("databaseOpenHelper-modified"));
            forceLoad();
        }
        Log.d(TAG, "onStartLoading()");
    }
    @Override protected void onStopLoading() {
        Log.d(TAG, "onStopLoading()");
        cancelLoad();
    }

    @Override public void onCanceled(Cursor c) {
        Log.d(TAG, "onCanceled()");
        super.onCanceled(c);
        c.close();
    }
//    Loaders in a reset state should not execute new loads, should not deliver new results, and should not monitor for changes
    @Override protected void onReset() {
        Log.d(TAG, "onReset()");
        onStopLoading(); //stop the loader
        //clear all resources
        if (data != null) {
            data.close();
            data = null;
        }
        //unregister observer
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(observer);
    }
}

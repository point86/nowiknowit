package pontasoftware.nowiknowit;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

public class HistoryFragment extends ListFragment {
    private String TAG = "HistoryFragment";
    HistoryCursorAdapter historyAdapter;
    Database database;// = new Database(getContext());
    SQLiteDatabase db;// = database.getReadableDatabase();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        database = new Database(getContext());
        Context context = getContext();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        db = database.getReadableDatabase();
        //same query that don't display thesaurus words: " WHERE (DICT_NAME <> "thesaurus") ORDER BY
        Cursor todoCursor = db.rawQuery("SELECT * FROM " + Database.History.HISTORY_TABLE + " ORDER BY " + Database.History._ID + " DESC;", null);
        historyAdapter = new HistoryCursorAdapter(getContext(), todoCursor, 0);

        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        getListView().setMultiChoiceModeListener((new ModeCallback()));
        setListAdapter(historyAdapter);
    } 

    public void onPause (){
        Log.d(TAG, "HistoryFragment is onPause()!");
        super.onPause();
    }
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent=new Intent(getActivity(), MainActivity.class);
        String tv_word = ((TextView) v.findViewById(R.id.tv_word)).getText().toString();
        intent.setAction(Intent.ACTION_SEARCH);
        intent.putExtra(SearchManager.QUERY, tv_word);
       // intent.putExtra(Definitions.QUERY_TYPE, Definitions.LOCAL_SEARCH);
        startActivity(intent);
    }


    private class ModeCallback implements ListView.MultiChoiceModeListener {
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.list_select_menu, menu);
                mode.setTitle("Select Items");
            Log.d(TAG, "onCreateActionMOde()");
            return true;
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return true;
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            Log.d(TAG, "onActionItemClicked");
            switch (item.getItemId()) {
                case R.id.delete:
                    //TODO add SELECT ALL button
                    SparseBooleanArray checked  = getListView().getCheckedItemPositions();
                    int checkedSize = checked.size();
                    String[] words = new String[checkedSize];
                    int words_counter = 0;
                    for (int i = 0; i < checkedSize; i++) {
                        if (checked.valueAt(i)) {
                            int pos = checked.keyAt(i);
                            Cursor c = (Cursor) historyAdapter.getItem(pos);
                            words[words_counter] =c.getString(c.getColumnIndex(Database.History.WORD));
                        }
                        words_counter++;
                    }
                    Log.d(TAG, "Deleting: " + Arrays.toString(words));
                    database.removeHst(words, Database.History.HISTORY_TABLE);
                    mode.finish();
                    break;
                default:
                    Toast.makeText(getActivity(), "Clicked " + item.getTitle(),
                            Toast.LENGTH_SHORT).show();
                    mode.finish();
                    break;
            }
            return true;
        }

        public void onDestroyActionMode(ActionMode mode) {        }

        public void onItemCheckedStateChanged(ActionMode mode,int position, long id, boolean checked) {
            final int checkedCount = getListView().getCheckedItemCount();
            switch (checkedCount) {
                case 0:
                    mode.setSubtitle(null);
                    break;
                /*case 1:
                    mode.setSubtitle("One item selected");
                    break;*/
                default:
                    mode.setSubtitle("" + checkedCount + " items selected to be deleted");
                    //Toast.makeText(getActivity(), getListView().getItemAtPosition(position).toString().trim(), Toast.LENGTH_SHORT).show();
                    break;
            }
        }

    }


}

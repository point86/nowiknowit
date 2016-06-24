package pontasoftware.nowiknowit;


import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class HistoryCursorAdapter extends CursorAdapter {
    public HistoryCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, 0);
    }

    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //Il significato della parola viene scritto solo se si clicca sul termine in esame!
        // Find fields to populate in inflated template
        TextView tv_word = (TextView) view.findViewById(R.id.tv_word);
        TextView tv_nsearch = (TextView) view.findViewById(R.id.tv_nsearch);
        TextView tv_ncorrect = (TextView) view.findViewById(R.id.tv_ncorrect);

        // Extract properties from cursor
        String word = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseOpenHelper.History.WORD));
        Integer num_searched = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseOpenHelper.History.NUM_SEARCHED));
        Integer num_correct = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseOpenHelper.History.NUM_CORRECT));
            // Populate fields with extracted properties
        tv_word.setText(word);
        tv_nsearch.setText(num_searched.toString() + " searches, ");
        tv_ncorrect.setText(num_correct.toString() + " errors");
    }

}
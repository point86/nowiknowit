package pontasoftware.nowiknowit;

import android.app.Activity;
import android.os.Bundle;

public class SettingsActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .add(android.R.id.content, new MainActivity.SettingsFragment())//.addToBackStack("ciaoo")
                .commit();
    }
}
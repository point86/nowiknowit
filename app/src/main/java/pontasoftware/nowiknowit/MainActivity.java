package pontasoftware.nowiknowit;

// PER migliorare look
// http://www.android4devs.com/2015/01/how-to-make-material-design-sliding-tabs.html

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

import pontasoftware.nowiknowit.view.SlidingTabLayout;


//public class MainActivity extends ActionBarActivity {
public class MainActivity extends AppCompatActivity  {

    final Context context = this;
    Toolbar toolbar;
    ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence Titles[]={"Quiz", "Dictionary", "History"};
    int Numboftabs =3;
    private final String TAG = "MainActivity";

    @Override
    protected void onNewIntent(Intent intent) { handleIntent(intent);}

    private void handleIntent(Intent intent){
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String search_query = intent.getStringExtra(SearchManager.QUERY);
            String search_type = intent.getStringExtra(Definitions.QUERY_TYPE);
            handleSearch(search_query, search_type);
        }
    }

    private void handleSearch(String search_query, String search_type) {
        WebView webView = (WebView) findViewById(R.id.tab_2_webview);
        String wrResponse;
        Definitions def = new Definitions(this);
        wrResponse = def.getDefinition(search_query);
        wrResponse = def.prettyPrint(wrResponse);
        webView.loadDataWithBaseURL("file:///android_asset/", wrResponse, "text/html", "UTF-8", null);
        pager.setCurrentItem(1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate(..)");
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        if (getIntent() != null) {
            handleIntent(getIntent());
        }
        // Creating The Toolbar and setting it as the Toolbar for the activity
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter =  new ViewPagerAdapter(getSupportFragmentManager(),Titles,Numboftabs);

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        // Assiging the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }

            // public int getDividerColor(int position){return 0;}; //FIXME l'ho messo io.
        });
        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);
        pager.setCurrentItem(1);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu); //FIXME
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setQueryHint("Type something here...");
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
/*        if (id == R.id.download) {
            Downloader dwn = new Downloader();
            String eseguite = dwn.eseguite.toString();
            Toast toast = Toast.makeText(context, "Scaricate "+eseguite+" definizioni", Toast.LENGTH_LONG);//FIXME non piu utile..
            toast.show();
        }*/
        if (id == R.id.action_info) {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.app_name)
                    .setMessage(R.string.info_message)
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);
        }
    /*    @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

          //  getView().setBackgroundColor(Color.BLUE);
          //  getView().setClickable(true);
        }*/
    }


}
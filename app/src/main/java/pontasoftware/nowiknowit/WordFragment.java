package pontasoftware.nowiknowit;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.SearchView;
import android.widget.Toast;


public class WordFragment extends Fragment {
    private final String TAG = "WordFragment";


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView()");
        View v = inflater.inflate(R.layout.tab_2, container, false);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        //SearchView searchView = (SearchView) v.findViewById(R.id.tab_2_searchView).getActionView();
        final SearchView searchView = (SearchView) v.findViewById(R.id.tab_2_searchView);
        searchView.setQueryHint("Search word...");
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        //all searchView area is clickable, not only the magnifiyng glass.
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            //onClick respond to "click" in all searchview area, not only the magnifiyng glass.
            public void onClick(View v) {searchView.setIconified(false); }
        });


        WebView webView = (WebView)v.findViewById(R.id.tab_2_webview);
        //<center> is deprecated in html5...

        webView.loadData("<center><br><br><br>Touch the bar to search something...</center>", "text/html", "UTF-8");
        return v;
    }



}
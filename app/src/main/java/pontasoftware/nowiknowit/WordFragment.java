package pontasoftware.nowiknowit;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

//import android.webkit.WebView;

public class WordFragment extends Fragment {
    private final String TAG = "WordFragment";

    //<meta charset="UTF-8">
    String parola ="<!DOCTYPE html>\n"+
            "<html>  <head> <style>"+
    "hw {"+
    "    color: black;"+
        "text-align: center;"+
        "font-weight: bold;"+
    "}"+
    "</style>"+
    "</head>"+
    "</body>"+
    "<entry id=\"hypocrite\"> <ew>hypocrite</ew> <hw>hyp*o*crite</hw> <sound> <wav>hypocr02.wav</wav> </sound> <pr>ˈhi-pə-ˌkrit</pr> <fl>noun</fl> <et>Middle English <it>ypocrite,</it> from Anglo-French, from Late Latin <it>hypocrita,</it> from Greek <it>hypokritēs</it> actor, hypocrite, from <it>hypokrinesthai</it> </et> <def> <date>13th century</date> <sn>1</sn> <dt>:a person who puts on a false appearance of <d_link>virtue</d_link> or religion</dt> <sn>2</sn> <dt>:a person who acts in contradiction to his or her stated beliefs or feelings</dt> </def> <uro> <ure>hypocrite</ure> <fl>adjective</fl> </uro> </entry>"+
    "</body>"+
    "</html>";
    public static String mwResponse = "Cerca una parola!";//TODO never used..

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView()");
        View v = inflater.inflate(R.layout.tab_2, container, false);
        WebView webView = (WebView)v.findViewById(R.id.tab_2_webview);
        //<center> is deprecated in html5...
        webView.loadData("<center><br><br><br>Please, search something...</center>", "text/html", "UTF-8");
        return v;
    }



}
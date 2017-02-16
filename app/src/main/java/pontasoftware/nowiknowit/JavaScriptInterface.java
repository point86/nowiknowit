package pontasoftware.nowiknowit;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

/**
 * Created by paolo on 18/08/2016.
 * This interface is necessary to let webview launch requests on the app, such as:
 *      - select another language
 */
public class JavaScriptInterface {
    static final String TAG = "JavaScriptInterface";
    private Activity activity;

    public JavaScriptInterface(Activity activiy) {
        this.activity = activiy;
    }

    public void changeLanguage(String prova){
        Log.d(TAG, "changeLanguage()");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(prova), "video/3gpp");
        activity.startActivity(intent);
    }
}
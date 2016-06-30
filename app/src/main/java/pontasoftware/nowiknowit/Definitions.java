package pontasoftware.nowiknowit;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by paolo on 06/10/2015.
 *
 * Class responsible for managing all definitions:
 *     local search (databaseOpenHelper)
 *     internet search
 */
public class Definitions  {
    static final String TAG = "Definitions";
    Context context;
    DatabaseOpenHelper databaseOpenHelper;

    public Definitions(Context context){
        this.context = context;
        this.databaseOpenHelper = DatabaseOpenHelper.getInstance(context);
    }

    private String getLocalDefinition(String term, String dictionary){
        SQLiteDatabase db = databaseOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM HISTORY WHERE WORD == \""+term+"\" AND DICT_NAME == \""+ dictionary+"\"", null);
        if (cursor.moveToFirst()){
            return cursor.getString(cursor.getColumnIndexOrThrow("DEF"));
        }
        db.close();
        return null;
    }
    public String getDefinition(String term) {
        if (term.equals("")){return "Empty string!";}

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String dictionary = sp.getString("dictionary_type", "english-learner");
        String mwResponse = getLocalDefinition(term, dictionary);
        if (mwResponse != null){
            //interested row have only to be updated
            databaseOpenHelper.insertHst(term, mwResponse, dictionary);
            return mwResponse;
        }

        try {
            String baseUrl = "https://api.collinsdictionary.com/api/v1/dictionaries/";
            String endUrl = "/search/first/?q=";
            //pay attention to extra ASCII characters!
            URL url = new URL(baseUrl + dictionary + endUrl + term.replace(" ","%20"));
            Log.d(TAG, url.toString());

            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestProperty("Accept", "application/json" );
            httpConnection.setRequestProperty("Accept-Encoding", "gzip" );
            httpConnection.setRequestProperty("accessKey", "InpHbu5lZS1uEBe9kdzTvLAKULMDjlVE8WYjodSZebTlBBxmwjgbnTEFs1Y4nbLG");
            httpConnection.setRequestMethod("GET");

            int responseCode = httpConnection.getResponseCode();

            if (responseCode == 200) {
                BufferedReader responseReader = new BufferedReader(new InputStreamReader(
                        httpConnection.getInputStream()));

                String responseLine;
                StringBuffer response = new StringBuffer();

                while ((responseLine = responseReader.readLine()) != null) {
                    response.append(responseLine + "\n");
                }
                responseReader.close();
                mwResponse = response.toString();
                //TODO do not add if word not found!
                //sistemando con un parser xml e crendo un oggetto apposito, si riesce a gestire qui
                //il pretty printing, eliminando così la funzione.
                //invariante: la definizione una parola inserita nel databaseOpenHelper non deve avere bisogno
                //di ulteriore pretty printing.

//                JSONArray js = new JSONArray(mwResponse);
                JSONObject ob = new JSONObject(mwResponse);
                mwResponse = (String) ob.get("entryContent");
                databaseOpenHelper.insertHst(term, mwResponse, dictionary);
                return mwResponse;
            }
            if(responseCode==404){
                //return "<center><br><br><br>There isn't a definition for the word \""+term+"\"!<br> Please check your word.</center>";
                return "<div class=\"pulse\">There isn't a definition for the word \"" + term + "\"<br>Please check your syntax.</div>";
            }
        } catch (Exception e ) { // IOException
//            e.printStackTrace();
            return "<div class=\"pulse\">Please check your internet connection :(</div>";
        }
        return "<div class=\"pulse\">Something went wrong :(</div>";
    }


    public String prettyPrint(String wrResponse){
        //TODO correggere con tipo di dati che mi ritorna il server. uso xsts?
        //return "<html><meta charset=\"UTF-8\"><link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\" />" + wrResponse +"</html>";
        return "<html><meta charset=\"UTF-8\"><link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\" />" + wrResponse +"</html>" ;
    }
}

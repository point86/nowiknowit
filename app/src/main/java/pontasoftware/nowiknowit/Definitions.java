package pontasoftware.nowiknowit;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

import android.content.Context;

/**
 * Created by paolo on 06/10/2015.
 */
public class Definitions {
    static final String TAG = "Definitions";
    Context context;
    Database database;

    public Definitions(Context context){
        this.context = context;
        this.database = new Database(context);
    }

    private String getLocalDefinition(String term, String dictionary){
        SQLiteDatabase db = database.getReadableDatabase();
        //FIXME completare con colonna giusta!
        //Cursor cursor = db.rawQuery("SELECT * FROM "+ Database.History.HISTORY_TABLE +" WHERE WORD == \""+term+"\";", null);
        Cursor cursor = db.rawQuery("SELECT * FROM "+ Database.History.HISTORY_TABLE +" WHERE " + Database.History.WORD + " == \""+term+"\"" +
                " AND " + Database.History.TYPE + " == \""+ dictionary+"\"", null);
        if (cursor.moveToFirst()){
            database.close();
            return cursor.getString(cursor.getColumnIndexOrThrow("DEF"));
        }
        database.close();
        return null;
    }
    public String getDefinition(String term) {
        if (term.equals("")){return "Empty string!";}

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String dictionary = sp.getString("dictionary_type", "english-learner");
        String mwResponse = getLocalDefinition(term, dictionary);
        if (mwResponse != null){
            database.insertHst(term, mwResponse, dictionary);
            return mwResponse;
        }

        try {
            String baseUrl = "https://api.collinsdictionary.com/api/v1/dictionaries/";
            String endUrl = "/search/first/?q=";
            URL url = new URL(baseUrl + dictionary + endUrl + term);
            Log.d(TAG, url.toString());

            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestProperty("Accept", "application/json" );
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
                //fixme do not add if word not found!
                //sistemando con un parser xml e crendo un oggetto apposito, si riesce a gestire qui
                //il pretty printing, eliminando così la funzione.
                //invariante: la definizione una parola inserita nel database non deve avere bisogno
                //di ulteriore pretty printing.
                database.insertHst(term, mwResponse, dictionary);
                database.close();

            //    JSONArray js = new JSONArray(mwResponse);
                JSONObject ob = new JSONObject(mwResponse);
                mwResponse = (String) ob.get("entryContent");
                database.close();
                return mwResponse;
            }
        } catch (Exception e ) { // IOException
            e.printStackTrace();
            database.close();
            return "<center><br><br><br>I'm sorry but i can't retrieve this definition, check your internet connection :(</center>";
        }
        database.close();
        return "<center><br><br><br>I'm sorry but i can't retrieve this definition, check your internet connection :(</center>";
    }

    public String prettyPrint(String wrResponse){
        return "<meta charset=\"UTF-8\"><link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\" />" + wrResponse;
    }
}

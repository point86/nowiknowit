package pontasoftware.nowiknowit;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by paolo on 06/10/2015.
 */
public class Definitions {
    static final String QUERY_TYPE = "pontasoftware.englishtrainer.QUERY_TYPE";
    static final String TAG = "Definitions";
    private  final String learnersBaseUrl = "http://www.dictionaryapi.com/api/v1/references/learners/xml/";
    private final String learnersEndUrl = "?key=e108e6ab-63f8-441b-be73-d480c8085d91";
    private final String collegiateBaseUrl = "http://www.dictionaryapi.com/api/v1/references/collegiate/xml/";
    private final String collegiateEndUrl = "?key=2ee02639-6b33-46d0-94d6-214736f39b79";
    Context context;
    Database database;

    public Definitions(Context context){
        this.context = context;
        this.database = new Database(context);
    }

    private String getLocalDefinition(String term, String table){
        SQLiteDatabase db = database.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+ table +" WHERE WORD == \""+term+"\";", null);
        if (cursor.moveToFirst()){
            database.close();
            return cursor.getString(cursor.getColumnIndexOrThrow("DEF"));
        }
        database.close();
        return null;
    }
    public String getDefinition(String term) {
        String table;
        if (term.equals("")){return "Empty string!";}

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        if(sp.getString(context.getString(R.string.dictionary_type), context.getString(R.string.learners_dict)).equals(context.getString(R.string.learners_dict)))
            table = Database.History.LEARNERS_TABLE;
        else
            table = Database.History.COLLEGIATE_TABLE;

        String mwResponse = getLocalDefinition(term, table);
        if (mwResponse != null){
            database.insertHst(term, mwResponse, table);
            return mwResponse;
        }
        try {
            URL learnersUrl = new URL(learnersBaseUrl + term + learnersEndUrl);//non serve gestire entry-id
            URL collegiateUrl = new URL(collegiateBaseUrl+term+collegiateEndUrl);
            URL url;
            if(table == Database.History.LEARNERS_TABLE)
                url = learnersUrl;
            else
                url = collegiateUrl;
            Log.d(TAG, url.toString());

            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
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
                database.insertHst(term, mwResponse, table);
                database.close();
                return mwResponse;
            }
        } catch ( IOException e ) {
            //e.printStackTrace();
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

package ch.hsr.mge.wordcount.domain;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import ch.hsr.mge.wordcount.data.FileHolder;
import ch.hsr.mge.wordcount.data.WordCount;
import ch.hsr.mge.wordcount.data.WordCountResult;
import ch.hsr.mge.wordcount.view.FileActivity;
import ch.hsr.mge.wordcount.view.WordListActivity;

/**
 * Service, um die Anzahl Worte zu ermitteln.
 *
 * @author Peter Buehler
 */
public class WordCountService extends IntentService {

    public WordCountService() {
        super("WordCountService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d(FileActivity.DEBUG_TAG, "onHandleIntent()");

        // Intent auslesen
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            Log.d(FileActivity.DEBUG_TAG, "service bundle is null");
        }

        FileHolder holder = (FileHolder) bundle.get(FileActivity.KEY_FILE_HOLDER);
        if (holder == null) {
            Log.d(FileActivity.DEBUG_TAG, "result is null");
        }

        // Resultat ermitteln
        String text = loadFile(holder.id);
        List<WordCount> counters = analyzeText(text);
        WordCountResult result = new WordCountResult(holder, counters);

        // Activity starten
        Intent showResultIntent = new Intent(this, WordListActivity.class);
        Bundle bundle2 = new Bundle();
        bundle2.putSerializable(FileActivity.KEY_WORD_RESULT, result);
        showResultIntent.putExtras(bundle2);

        startActivity(showResultIntent);
    }

    /**
     * Laedt die Datei und liefert den Inhalt als String.
     */
    private String loadFile(int id) {

        InputStream in = getResources().openRawResource(id);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String readLine = null;
        StringBuilder out = new StringBuilder();

        try {
            while ((readLine = br.readLine()) != null) {
                out.append(readLine);
            }
            in.close();
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String text = out.toString();

        Log.d(FileActivity.DEBUG_TAG, "File loaded size=" + text.length());

        return text;
    }

    /**
     * Trennt den Text und zaehlt die Anzahl Worte.
     *
     * @param text
     */
    private List<WordCount> analyzeText(String text) {
        List<WordCount> result = new WordCounter().countWords(text);
        Log.d(FileActivity.DEBUG_TAG, "File analyzed");
        return result;
    }
}

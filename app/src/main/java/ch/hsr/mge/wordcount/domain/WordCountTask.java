package ch.hsr.mge.wordcount.domain;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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
 * Asynchroner Task, um Worte zu zaehlen.
 *
 * @author Peter Buehler
 */
public class WordCountTask extends AsyncTask<Void, Void, List<WordCount>> {

    private Context context;
    private FileHolder fileHolder;

    public WordCountTask(Context context, FileHolder fileHolder) {
        this.context = context;
        this.fileHolder = fileHolder;
    }

    @Override
    protected List<WordCount> doInBackground(Void... params) {
        String text = loadFile(fileHolder.id);
        return analyzeText(text);
    }

    @Override
    protected void onPostExecute(List<WordCount> counters) {

        WordCountResult result = new WordCountResult(fileHolder, counters);

        Intent intent = new Intent(context, WordListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(FileActivity.KEY_WORD_RESULT, result);
        intent.putExtras(bundle);

        context.startActivity(intent);
    }

    /**
     * Laedt die Datei und liefert den Inhalt als String.
     */
    private String loadFile(int id) {

        InputStream in = context.getResources().openRawResource(id);
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

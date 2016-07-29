package com.example.android.booklistingapp;

import android.app.usage.UsageEvents;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    /**
     * URL to query the USGS dataset for earthquake information
     */
    private static String USGS_REQUEST_URL =
            "https://www.googleapis.com/books/v1/volumes?q=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Search field
        final EditText editTextSearch = (EditText) findViewById(R.id.search_edit_text);

        //Search button
        Button buttonSearch = (Button) findViewById(R.id.search_button);
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                USGS_REQUEST_URL += editTextSearch.getText().toString();
                USGS_REQUEST_URL = USGS_REQUEST_URL.replace(" ", "+");
                BooksAsyncTask task = new BooksAsyncTask();
                task.execute();
            }
        });


    }

    /***
     * This function update Ui with the books list
     * @param books
     */
    private void updateUi(ArrayList<Book> books) {
        BooksAdapter adapter = new BooksAdapter(getApplicationContext(), books);
        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(adapter);
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            Log.e(LOG_TAG, "Error with creating URL", exception);
            return null;
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        //If the URL is null, then return early
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();

            //Success
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                //Failure
                Log.e(LOG_TAG, "Error responce Code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            String str = e.getLocalizedMessage();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // function must handle java.io.IOException here
                inputStream.close();
            }

        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return an ArrayList<Book> list object by parsing out information
     * about the first book from the input BookListJSON string.
     */
    private ArrayList<Book> extractFeatureFromJson(String BookListJSON) {
        ArrayList<Book> results = new ArrayList<Book>();
        //If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(BookListJSON)) {
            return null;
        }

        try {
            JSONObject baseJsonResponse = new JSONObject(BookListJSON);
            JSONArray itemsArray = baseJsonResponse.getJSONArray("items");

            for (int i = 0; i < itemsArray.length(); i++) {
                JSONObject bookItem = itemsArray.getJSONObject(i);
                JSONObject volumeInfo = bookItem.getJSONObject("volumeInfo");
                String title = volumeInfo.getString("title");
                String author = volumeInfo.getString("authors");
                JSONArray bookAuthorArray = volumeInfo.getJSONArray("authors");

                Book book = new Book(author, title);
                results.add(book);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return results;


    }

    private class BooksAsyncTask extends AsyncTask<URL, Void, ArrayList<Book>> {

        @Override
        protected ArrayList<Book> doInBackground(URL... urls) {
            // Create URL object

            URL url = createUrl(USGS_REQUEST_URL);
            // Perform HTTP request to the URL and receive a JSON response back
            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                // TODO Handle the IOException
            }

            //  Extract relevant fields from the JSON response and create an {@link Event} object
            ArrayList<Book> books = extractFeatureFromJson(jsonResponse);

            // Return the {@link Event} object as the result fo the {@link TsunamiAsyncTask}
            return books;
        }

        @Override
        protected void onPostExecute(ArrayList<Book> books) {
            if (books == null) {
                return;
            }

            updateUi(books);
        }
    }
}

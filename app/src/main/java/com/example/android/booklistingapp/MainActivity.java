package com.example.android.booklistingapp;

import android.app.ProgressDialog;
import android.app.usage.UsageEvents;
import android.content.res.Configuration;
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

/***
 * http://stackoverflow.com/questions/19043243/error-org-json-jsonexception-no-value-for-project-name-this-is-my-json
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = MainActivity.class.getSimpleName();
    private ProgressDialog progDailog;
    private ArrayList<Book> data = new ArrayList<>();
    BooksAdapter adapter;
    ListView listView;
    /**
     * URL to query the USGS dataset for earthquake information
     */
    private static String USGS_REQUEST_URL =
            "https://www.googleapis.com/books/v1/volumes?q=";
    private static String DEFAULT_USGS_REQUEST_URL =
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
                USGS_REQUEST_URL = DEFAULT_USGS_REQUEST_URL;
                USGS_REQUEST_URL += editTextSearch.getText().toString();
                USGS_REQUEST_URL = USGS_REQUEST_URL.replace(" ", "+");
                new BooksAsyncTask().execute();
            }
        });


    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_main);

    }

    //Save list when state changes
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("key", data);
        super.onSaveInstanceState(outState);
    }

    //restore list when state changes
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            data = (ArrayList<Book>) savedInstanceState.getSerializable("key");
            adapter = new BooksAdapter(getApplicationContext(), data);
            listView = (ListView) findViewById(R.id.list);
            listView.setAdapter(adapter);
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    /***
     * This function update Ui with the books list
     *
     * @param books
     */
    private void updateUi(ArrayList<Book> books) {
        data = books;
        adapter = new BooksAdapter(getApplicationContext(), books);
        listView = (ListView) findViewById(R.id.list);
        listView.setEmptyView(findViewById(R.id.empty_list_item));
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
                String title = volumeInfo.optString("title");
                String author = volumeInfo.optString("authors");
                JSONArray bookAuthorArray = volumeInfo.optJSONArray("authors");

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
        protected void onPreExecute() {
            super.onPreExecute();
            progDailog = new ProgressDialog(MainActivity.this);
            progDailog.setMessage("Loading...");
            progDailog.setIndeterminate(false);
            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDailog.setCancelable(true);
            progDailog.show();
        }

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
            progDailog.dismiss();
        }
    }
}

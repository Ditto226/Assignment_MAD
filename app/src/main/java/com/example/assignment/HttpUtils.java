package com.example.assignment;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

// ...

public class HttpUtils {

    private static Context context;

    public HttpUtils(Context context) {
        this.context = context;
    }
    public interface OnHttpResponseListener {
        void onHttpResponse(Context context,String response);
    }

    public static void getRequest(String url, OnHttpResponseListener listener) {
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... urls) {
                return performGetRequest(urls[0]);
            }

            @Override
            protected void onPostExecute(String result) {
                if (listener != null) {
                    listener.onHttpResponse(context,result);
                }
            }
        }.execute(url);
    }

    private static String performGetRequest(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            try {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                return convertStreamToString(in);
            } finally {
                urlConnection.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String convertStreamToString(InputStream is) {
        // Convert InputStream to String
        // Implementation of this method is easily available online
        return null;
    }

    public static void parseAndDisplayAQI(Context context, String response) {
        try {
            JSONObject jsonResponse = new JSONObject(response);

            // Extract AQI information from the JSON response
            JSONObject mainObject = jsonResponse.getJSONObject("main");
            int aqiValue = mainObject.getInt("aqi");

            // Display the AQI information using the provided context
            String aqiMessage = "Air Quality Index: " + aqiValue;
            Toast.makeText(context, aqiMessage, Toast.LENGTH_SHORT).show();

        } catch (JSONException e) {
            e.printStackTrace();
            // Handle JSON parsing error
            Toast.makeText(context, "Error parsing AQI data", Toast.LENGTH_SHORT).show();
        }
    }
}


package com.example.assignment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Alert_List extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NotificationManager notificationManager;
    private AlertAdapter alertAdapter;
    private List<Alert.WeatherEntry> weatherEntries;
    private static final int NOTIFICATION_ID = 1;
    TextView time;
    TextView message;
    ImageView weather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recyclerview);

        recyclerView = findViewById(R.id.recycleview);

//        List<Alert.WeatherEntry> alertList = new ArrayList<>(); // Replace with your actual data
//        AlertAdapter alertAdapter = new AlertAdapter(alertList);

        setAdapter();
        setAlert();
    }

    void setAlert(){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            @SuppressLint("MissingPermission")
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (lastKnownLocation != null) {
                double lat = lastKnownLocation.getLatitude();
                double lon = lastKnownLocation.getLongitude();

                String apiKey = "bfae835a587c463187d4178050f47717";
                String apiUrl = "https://api.weatherbit.io/v2.0/forecast/hourly?lat="+lat+"&lon="+lon+"&key=" + apiKey+"&hours=72";

                RequestQueue requestQueue = Volley.newRequestQueue(this);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                        Request.Method.GET,  // Adjust the method based on your API requirements
                        apiUrl,
                        null,
                        new Response.Listener<JSONObject>() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    List<Alert.WeatherEntry> weatherEntries = new ArrayList<>();
                                    List<Alert.WeatherEntry> Alert = new ArrayList<>();

                                    JSONObject jsonRootObject = new JSONObject(response.toString());
                                    JSONArray weatherList = jsonRootObject.getJSONArray("data");

                                    for (int i = 0; i < weatherList.length(); i++) {
                                        JSONObject weatherObject = weatherList.getJSONObject(i);

                                        double temperature = weatherObject.getDouble("temp");
                                        int id = weatherObject.getJSONObject("weather").getInt("code");
                                        String time = weatherObject.getString("timestamp_local");

                                        // Create a WeatherEntry object and add it to the list
                                        Alert.WeatherEntry weatherEntry = new Alert.WeatherEntry(temperature, id, time);
                                        weatherEntries.add(weatherEntry);
                                    }

                                    for(Alert.WeatherEntry entry:weatherEntries){
                                        if(entry.temperature >=30){
                                            Alert.add(entry);
                                        }
                                        switch (entry.id){
                                            case(501): case(502): case(503): case(504):
                                            case(200): case(201): case(202):
                                            case(210): case(211): case(212):
                                            case(221): case(230): case(231): case(232):
                                            case(701): case(711): case(721): case(741):
                                                Alert.add(entry);
                                                break;
                                        }
                                    }
//                                    for(int i =0;i<Alert.size();i++){
                                        getDuration(Alert);
//                                    }

                                    alertAdapter.setAlertList(Alert);
                                    alertAdapter.notifyDataSetChanged();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
//                                Toast.makeText(this,"efed",TOAST_LENGTH_SHORT);
                            }
                        }
                );
                requestQueue.add(jsonObjectRequest);
            } else {
                // Handle the case where lastKnownLocation is null
//                Toast.makeText(this,)
            }
        } else {
            // Handle the case where GPS is not enabled
        }

    }
    void setAdapter() {
        recyclerView = findViewById(R.id.recycleview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // Initialize the adapter with an empty list
        alertAdapter = new AlertAdapter(new ArrayList<>());

        recyclerView.setAdapter(alertAdapter);
    }

    void getDuration(List<Alert.WeatherEntry> alertlist) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE,ha dd-MM-yyyy", Locale.ENGLISH);
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MM-yyyy");
        int hours = 0;

        for (int i = 0; i < alertlist.size() - 1; i++) {
            String d1 = alertlist.get(i).getTime();
            String d2 = alertlist.get(i + 1).getTime();

            try {
                Date date1 = sdf.parse(d1);
                Date date2 = sdf.parse(d2);

                if (sdf2.format(date1).equals(sdf2.format(date2))) {
                    hours++;
                    alertlist.get(i).setDuration(hours);
                    alertlist.remove(i + 1);
                    i--;
                } else {
                    hours = 0; // Reset the duration when dates are different
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }

        // Set duration for the last entry (if any)
        if (!alertlist.isEmpty()) {
            alertlist.get(alertlist.size() - 1).setDuration(hours);
        }
    }

}



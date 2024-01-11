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

import java.util.ArrayList;
import java.util.List;

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

                String apiKey = "f6b0e9e985d5c35e9e2834c0546415e1";
                String apiUrl = "https://api.openweathermap.org/data/2.5/forecast?lat="+lat+"&lon="+lon+"&appid=" + apiKey;

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
                                    JSONArray weatherList = jsonRootObject.getJSONArray("list");

                                    for (int i = 0; i < weatherList.length(); i++) {
                                        JSONObject weatherObject = weatherList.getJSONObject(i);

                                        double temperature = weatherObject.getJSONObject("main").getDouble("feels_like");
                                        int id = weatherObject.getJSONArray("weather").getJSONObject(0).getInt("id");
                                        String time = weatherObject.getString("dt_txt");

                                        // Create a WeatherEntry object and add it to the list
                                        Alert.WeatherEntry weatherEntry = new Alert.WeatherEntry(temperature, id, time);
                                        weatherEntries.add(weatherEntry);
                                    }

                                    for(Alert.WeatherEntry entry:weatherEntries){
                                        if(entry.temperature >310){
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


}



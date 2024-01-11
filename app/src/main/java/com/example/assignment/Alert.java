package com.example.assignment;

import static java.security.AccessController.getContext;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;


public class Alert extends AppCompatActivity implements IBaseGpsListener{
    TextView time1;
    TextView location;
    TextView message;
    ImageView weather;
    TextView advice;
    Button alert;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);

        time1 = findViewById(R.id.Alert_Time);
        location = findViewById(R.id.Alert_Location);
        message = findViewById(R.id.Message);
        weather = findViewById(R.id.Weather);
        alert = findViewById(R.id.Alert);
        advice = findViewById(R.id.Advice);

        Location();
        alert();

        alert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Alert.this,Alert_List.class);
                startActivity(intent);
            }
        });
    }

    public void alert() {
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
                                    List<Alert.WeatherEntry> alertlist = new ArrayList<>();

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
                                        if(entry.temperature>=310){
                                            alertlist.add(entry);
                                        }
                                        switch (entry.id){
                                            case(501): case(502): case(503): case(504):
                                            case(200): case(201): case(202):
                                            case(210): case(211): case(212):
                                            case(221): case(230): case(231): case(232):
                                            case(701): case(711): case(721): case(741):
                                                alertlist.add(entry);
                                                break;
                                        }
                                    }
                                    if(!alertlist.isEmpty()) {
                                        time1.setText(alertlist.get(0).getTime());
                                        message.setText(alertlist.get(0).getMessage());
                                        Context context = com.example.assignment.Alert.this;
                                        weather.setImageDrawable(alertlist.get(0).getIconDrawable(context));
                                    }else{
                                        time1.setText("");
                                        message.setText("Rest assure! Your place has no extreme weather for the next 5 days");
                                        weather.setImageResource(R.drawable.clear_day);
                                    }
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


    public static class WeatherEntry {
        public double temperature;
        public int id;
        public String time;
        public WeatherEntry(double temperature, int id,String time) {
            this.temperature = temperature;
            this.id = id;//502-504 heavyrain, 200-202,210-212,221,230-232 thunderstorm, 701,711,721,741 fog etc.
            this.time = time;
        }

        String getType(){
            if(temperature>=310){
                return  "blazing hot";
            }
            switch (id) {
                case 501:case 502: case 503: case 504:
                    return "heavy rain";
                case 200: case 201: case 202:
                case 210: case 211: case 212:
                case 221: case 230: case 231:
                case 232:
                    return "thunderstorm";
                case 701: case 711:
                case 721: case 741:
                    return "foggy";
                default:
                    // You can throw an exception or return a default drawable
                    // For example, returning R.drawable.default_icon
                    return "";
            }
        }

        String getMessage(){
            if(temperature>=310){
                return  "Its going to be scorching outside! Drink plenty of water and stay hydrated!";
            }
            switch (id) {
                case 501: case 502: case 503: case 504:
                    return "Beware! Anticipate substantial rainfall at your location. Please prepare an umbrella or raincoat before going outside!";
                case 200: case 201: case 202:
                case 210: case 211: case 212:
                case 221: case 230: case 231:
                case 232:
                    return "Uh oh! thunderstorm gonna bombarding your place. Avoid going outside for your own safety.";
                case 701: case 711:
                case 721: case 741:
                    return "Its gonna be hard to walk around outside. Watch out for your surrounding!";
                default:
                    // You can throw an exception or return a default drawable
                    // For example, returning R.drawable.default_icon
                    return "Rest assure! Your place has no extreme weather for the next 5 days";
            }
        }

        String getTime(){
            try{
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = inputFormat.parse(time);

                // Format the Date object into the desired output format
                SimpleDateFormat outputFormat = new SimpleDateFormat("EEEE, ha dd-MM-yyyy");
                String outputDate = outputFormat.format(date);
                return outputDate;
            }catch (ParseException e){
                e.printStackTrace();
            }
            return null;
        }

        Drawable getIconDrawable(Context context) {
            Resources resources = context.getResources(); // Assuming this method is within an Activity or Context
            if(temperature>=310){
                return  resources.getDrawable(R.drawable.hot);
            }
            switch (id) {
                case 501: case 502: case 503: case 504:
                    return resources.getDrawable(R.drawable.heavyrain);
                case 200: case 201: case 202:
                case 210: case 211: case 212:
                case 221: case 230: case 231:
                case 232:
                    return resources.getDrawable(R.drawable.thunderstorm);
                case 701: case 711:
                case 721: case 741:
                    return resources.getDrawable(R.drawable.foggy);
                default:
                    // You can throw an exception or return a default drawable
                    // For example, returning R.drawable.default_icon
                    return resources.getDrawable(R.drawable.border);
            }
        }
        double getTemperature(){
            return temperature;
        }

        // Add getters if needed
    }

    @SuppressLint("MissingPermission")
    private void Location(){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            location.setText("Loading location...");
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
        }else{
            Toast.makeText(this,"Enable GPS", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
    }
    //show location as string
    private String hereLocation(Location location) {
        try {
            if (Geocoder.isPresent()) {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                if (addresses != null && addresses.size() > 0) {
                    Address address = addresses.get(0);
                    return address.getAddressLine(0);
                }
            } else {
                return "Geocoder is not available";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Error retrieving location";
        }
        return "Lat: " + location.getLatitude() + "\nLong: " + location.getLongitude();
    }
    @Override
    public void onLocationChanged(Location locations) {
        location.setText(hereLocation(locations));
    }


    @Override
    public void onProviderDisable(String provider) {

    }

    @Override
    public void onProviderEnable(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }
    @Override
    public void onGnssStatusChanged(int event) {

    }

}
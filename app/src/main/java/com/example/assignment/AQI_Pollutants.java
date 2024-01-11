package com.example.assignment;



import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.location.Address;
import android.location.Geocoder;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import java.util.List;
import java.util.Locale;
import java.text.SimpleDateFormat;
import java.util.Date;
public class AQI_Pollutants extends AppCompatActivity implements IBaseGpsListener{

    private static final int PERMISSION_LOCATION = 1000;
    private TextView date;
    TextView AQI_location;

    TextView PM10;
    TextView PM2_5;
    TextView O3;
    TextView NO2;
    TextView SO2;
    TextView CO;
    Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aqi_pollutants);

        AQI_location = findViewById(R.id.AQI_Location);
        date = findViewById(R.id.AQI_Time);
        PM10 =findViewById(R.id.AQI_PM10V);
        PM2_5 = findViewById(R.id.AQI_PM25V);
        O3 = findViewById(R.id.AQI_O3V);
        NO2 =findViewById(R.id.AQI_NO2V);
        SO2 = findViewById(R.id.AQI_SO2V);
        CO = findViewById(R.id.AQI_COV);

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd-MM-yyyy");
        String currentDateandTime = sdf.format(new Date());

        Location();
        date.setText(currentDateandTime);
        AQIPollutants();

    }


    public void AQIPollutants() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            @SuppressLint("MissingPermission")
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (lastKnownLocation != null) {
                double lat = lastKnownLocation.getLatitude();
                double lon = lastKnownLocation.getLongitude();

                String apiKey = "bfae835a587c463187d4178050f47717";
                String apiUrl = "https://api.weatherbit.io/v2.0/current/airquality?lat=" + lat + "&lon=" + lon +"&key="+ apiKey;

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
                                JSONObject pollutants = response.getJSONArray("data").getJSONObject(0);

                                PM10.setText("" + pollutants.getDouble("pm10"));
                                PM2_5.setText("" + pollutants.getDouble("pm25"));
                                O3.setText("" + pollutants.getDouble("o3"));
                                NO2.setText("" + pollutants.getDouble("no2"));
                                SO2.setText("" + pollutants.getDouble("so2"));
                                CO.setText("" + pollutants.getDouble("co"));

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // Handle errors
                            }
                        }
                );
                requestQueue.add(jsonObjectRequest);
            } else {
                // Handle the case where lastKnownLocation is null
            }
        } else {
            // Handle the case where GPS is not enabled
        }
    }


    public void onRequestPermissionResult(int requestCode,@NonNull String[] permissions,@NonNull int[] grantResults){
        if(requestCode == PERMISSION_LOCATION){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Location();
            }else{
                Toast.makeText(this,"Permission not granted", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }


    @SuppressLint("MissingPermission")
    private void Location(){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            AQI_location.setText("Loading location...");
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
    public void onLocationChanged(Location location) {
        this.location = location;
        AQI_location.setText(hereLocation(location));
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

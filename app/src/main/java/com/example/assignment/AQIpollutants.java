package com.example.assignment;



import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.text.SimpleDateFormat;
import java.util.Date;
public class AQIpollutants extends AppCompatActivity implements IBaseGpsListener{

    private static final int PERMISSION_LOCATION = 1000;
    private TextView date;
    TextView AQI_location;

    TextView PM10;
    TextView PM2_5;
    TextView O3;
    TextView NO2;
    TextView SO2;
    TextView CO;
    Button Btn_location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aqipollutants);

        AQI_location = findViewById(R.id.AQI_Location);
        Btn_location = findViewById(R.id.Btn_Location);
        date = findViewById(R.id.AQI_Time);
        PM10 =findViewById(R.id.AQI_PM10V);
        PM2_5 = findViewById(R.id.AQI_PM25V);
        O3 = findViewById(R.id.AQI_O3V);
        NO2 =findViewById(R.id.AQI_NO2V);
        SO2 = findViewById(R.id.AQI_SO2V);
        CO = findViewById(R.id.AQI_COV);

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd-MM-yyyy");
        String currentDateandTime = sdf.format(new Date());

        showLocation();
        date.setText(currentDateandTime);
        AQIPollutants();
        Btn_location.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                        checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) !=
                                PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_LOCATION);
                } else {
                    showLocation();
                    date.setText(currentDateandTime);
                    AQIPollutants();
                }
            }
        });
    }


    public void AQIPollutants(){

        String apiKey = "f6b0e9e985d5c35e9e2834c0546415e1";
        String apiUrl = "https://api.openweathermap.org/data/2.5/air_pollution?lat=37.7749&lon=-122.4194&appid="+apiKey;      //must implement com.android.volley:volley:1.2.1
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(apiUrl, new Response.Listener<JSONObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject mainObject = response.getJSONArray("list").getJSONObject(0).getJSONObject("main");
                    int aqiValue = mainObject.getInt("aqi");

                    // Display the AQI information using the provided context
                    String aqiMessage = "Air Quality Index: " + aqiValue;
                    Toast.makeText(getApplicationContext(), aqiMessage, Toast.LENGTH_SHORT).show();

                    JSONObject componentObject = response.getJSONArray("list").getJSONObject(0).getJSONObject("components");

                    PM10.setText(""+componentObject.getDouble("pm10"));
                    PM2_5.setText(""+componentObject.getDouble("pm2_5"));
                    O3.setText(""+componentObject.getDouble("o3"));
                    NO2.setText(""+componentObject.getDouble("no2"));
                    SO2.setText(""+componentObject.getDouble("so2"));
                    CO.setText(""+componentObject.getDouble("co"));

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        RequestQueue referenceQueue = Volley.newRequestQueue(this);
        referenceQueue.add(jsonObjectRequest);
    }

    public void onRequestPermissionResult(int requestCode,@NonNull String[] permissions,@NonNull int[] grantResults){
        if(requestCode == PERMISSION_LOCATION){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                showLocation();
            }else{
                Toast.makeText(this,"Permission not granted", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }


    @SuppressLint("MissingPermission")
    private void showLocation(){
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
                    return address.getAdminArea() + ", " + address.getCountryName();
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

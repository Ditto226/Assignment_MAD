package com.example.assignment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AQI_Light extends AppCompatActivity implements IBaseGpsListener{

    private static final int PERMISSION_LOCATION = 1000;
    TextView date;
    TextView time;
    TextView location;
    TextView lvl;
    TextView today;
    TextView tmrw;
    TextView tmrw2;
    TextView tmrw3;
    TextView todayv;
    TextView tmrwv;
    TextView tmrw2v;
    TextView tmrw3v;
    Button detail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aqi_light);

        time = findViewById(R.id.AQI_Time);
        location = findViewById(R.id.AQI_Location);
        lvl = findViewById(R.id.AQI_Lvl);
        today = findViewById(R.id.AQI_Date);
        tmrw = findViewById(R.id.AQI_DateTmrw0);
        tmrw2 = findViewById(R.id.AQI_DateTmrw);
        tmrw3 = findViewById(R.id.AQI_DateTmrw2);
        todayv = findViewById(R.id.AQI_Value);
        tmrwv = findViewById(R.id.AQI_ValueTmrw0);
        tmrw2v = findViewById(R.id.AQI_ValueTmrw);
        tmrw3v = findViewById(R.id.AQI_ValueTmrw2);
        detail = findViewById(R.id.Btn_details);

        Date();
        Location();
        AQI();

        detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AQI_Light.this, AQIpollutants.class);
                startActivity(intent);
            }
        });
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
//        tv_location.setText(hereLocation(location));
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

    public void AQI(){
        String apiKey = "f6b0e9e985d5c35e9e2834c0546415e1";
        String apiUrl = "https://api.openweathermap.org/data/2.5/air_pollution/forecast?lat=37.7749&lon=-122.4194&appid="+apiKey;
        String jsonData = getJSONFromURL(apiUrl);

        try {
            JSONObject json = new JSONObject(jsonData);
            JSONArray dataList = json.getJSONArray("list");

            // Calculate daily average AQI for today and the next 3 days
            int sumAQI = 0;
            int count = 0;

            for (int i = 0; i < dataList.length(); i++) {
                JSONObject data = dataList.getJSONObject(i);
                int aqi = data.getJSONObject("main").getInt("aqi");
                int day = i / 24;  // Assuming data is available every 3 hours, so 8 data points per day
                sumAQI += aqi;
                count++;

                if(count%24==0 && count!=0){
                    int averageAQI = sumAQI/24;
                    switch (day){
                        case(0):
                            todayv.setText(""+averageAQI);
                            break;
                        case(1):
                            tmrwv.setText(""+averageAQI);
                            break;
                        case(2):
                            tmrw2v.setText(""+averageAQI);
                            break;
                        case(3):
                            tmrw3v.setText(""+averageAQI);
                    }
                    sumAQI = 0;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String getJSONFromURL(String url){
        String jsontext="";
        try{
            URL url1 =new URL(url);
            InputStream is = url1.openStream();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));

            String line;
            while((line= bufferedReader.readLine()) != null){
                jsontext +=line;
            }
            is.close();
            bufferedReader.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return jsontext;
    }

    public void Date(){
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd-MM-yyyy");
        String todayS = sdf.format(date);

        calendar.add(Calendar.DAY_OF_MONTH,1);
        Date tomorrowDate2 = calendar.getTime();
        SimpleDateFormat sdf2 = new SimpleDateFormat("EEEE, dd-MM-yyyy");
        String tmrw2S = sdf2.format(tomorrowDate2);

        calendar.add(Calendar.DAY_OF_MONTH,1);
        Date tomorrowDate3 = calendar.getTime();
        SimpleDateFormat sdf3 = new SimpleDateFormat("EEEE, dd-MM-yyyy");
        String tmrw3S = sdf3.format(tomorrowDate3);

        calendar.add(Calendar.DAY_OF_MONTH,1);
        Date tomorrowDate4 = calendar.getTime();
        SimpleDateFormat sdf4 = new SimpleDateFormat("EEEE, dd-MM-yyyy");
        String tmrw4S = sdf4.format(tomorrowDate4);

        today.setText(todayS);
        tmrw.setText(tmrw2S);
        tmrw2.setText(tmrw3S);
        tmrw3.setText(tmrw4S);
    }

}

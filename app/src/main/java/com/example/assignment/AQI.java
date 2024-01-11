package com.example.assignment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;
import org.json.JSONArray;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AQI extends AppCompatActivity implements IBaseGpsListener{

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
                Intent intent = new Intent(AQI.this, AQI_Pollutants.class);
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

    public void AQI() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            @SuppressLint("MissingPermission")
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (lastKnownLocation != null) {
                double lat = lastKnownLocation.getLatitude();
                double lon = lastKnownLocation.getLongitude();

                String apiKey = "bfae835a587c463187d4178050f47717";
                String apiUrl = "https://api.weatherbit.io/v2.0/forecast/airquality?lat=" + lat + "&lon=" + lon +"&start_date="+ firstdate()+"&end_date="+lastDate()+"tz=local&key=" + apiKey;

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
                                    //JSONObject json = new JSONObject(jsonData);
                                    JSONArray dataList = response.getJSONArray("data");

                                    // Calculate daily average AQI for today and the next 3 days
                                    int sumAQI = 0;
                                    int count = 0;

                                    for (int i = 0; i < dataList.length(); i++) {
                                        JSONObject data = dataList.getJSONObject(i);
                                        int aqi = data.getInt("aqi");
                                        int day = (i) / 24;  // Assuming data is available every 1 hours, so 24 data points per day
                                        sumAQI += aqi;
                                        count++;

                                        if (count % 24 == 0 && count != 0) {
                                            int averageAQI = sumAQI / 24;
                                            switch (day){
                                                case 0:
                                                    todayv.setText(String.valueOf(averageAQI));
                                                    setTextViewWithColor(todayv,averageAQI);
                                                    setTextViewWithColor(lvl, averageAQI);
                                                    int color = lvl.getCurrentTextColor();
                                                    if (color == ContextCompat.getColor(AQI.this, R.color.GOOD)) {
                                                        lvl.setText("GOOD");
                                                    } else if (color == ContextCompat.getColor(AQI.this, R.color.MODERATE)) {
                                                        lvl.setText("MODERATE");
                                                    } else if (color == ContextCompat.getColor(AQI.this, R.color.POOR)) {
                                                        lvl.setText("POOR");
                                                    } else if (color == ContextCompat.getColor(AQI.this, R.color.VERYPOOR)) {
                                                        lvl.setText("VERY POOR");
                                                    } else if (color == ContextCompat.getColor(AQI.this, R.color.black)) {
                                                        lvl.setText("HAZARDOUS");
                                                    }
                                                    break;
                                                case 1:
                                                    tmrwv.setText(String.valueOf(averageAQI));
                                                    setTextViewWithColor(tmrwv,averageAQI);
                                                    break;
                                                case 2:
                                                    tmrw2v.setText(String.valueOf(averageAQI));
                                                    setTextViewWithColor(tmrw2v,averageAQI);
                                                    break;
                                                case 3:
                                                    tmrw3v.setText(String.valueOf(averageAQI));
                                                    setTextViewWithColor(tmrw3v,averageAQI);
                                                    break;
                                            }
                                            sumAQI = 0;
                                        }
                                    }
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


    public void setTextViewWithColor(TextView textView, int averageAQI) {
        if(averageAQI<51) {
            textView.setTextColor(ContextCompat.getColor(AQI.this, R.color.GOOD));}
        else if(averageAQI<101){
            textView.setTextColor(ContextCompat.getColor(AQI.this, R.color.GOOD));}
        else if(averageAQI<151){
            textView.setTextColor(ContextCompat.getColor(AQI.this, R.color.GOOD));}
        else if(averageAQI<201){
            textView.setTextColor(ContextCompat.getColor(AQI.this, R.color.GOOD));}
        else{
            textView.setText(String.valueOf(averageAQI));}
    }
//
//    public static String getJSONFromURL(String url){
//        String jsontext="";
//        try{
//            URL url1 =new URL(url);
//            InputStream is = url1.openStream();
//
//            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
//
//            String line;
//            while((line= bufferedReader.readLine()) != null){
//                jsontext +=line;
//            }
//            is.close();
//            bufferedReader.close();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        return jsontext;
//    }

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
    public String firstdate(){
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String todayS = calendar.toString();
        return todayS;
    }
    public String lastDate(){
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String todayS = calendar.toString();

        calendar.add(Calendar.DAY_OF_MONTH,3);
        Date tomorrowDate2 = calendar.getTime();
        String tmrw2S = calendar.toString();
        return tmrw2S;
    }
}


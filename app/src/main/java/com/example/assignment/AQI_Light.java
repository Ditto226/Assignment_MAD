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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class AQI_Light extends AppCompatActivity implements IBaseGpsListener{

    private static final int PERMISSION_LOCATION = 1000;
    private TextView textViewAqiValue;
    TextView tv_location;
    Button b_location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aqi_light);

        tv_location = findViewById(R.id.AQI_Location);
        b_location = findViewById(R.id.Btn_Location);

        b_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                        checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) !=
                                PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_LOCATION);
                } else {
                    showLocation();
                }
            }
        });
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
            tv_location.setText("Loading location...");
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
        }else{
            Toast.makeText(this,"Enable GPS", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
    }
    //show location as string
    private  String hereLocation(Location location){
        return "Lat: "+location.getLatitude()+"\nLong: "+location.getLongitude();
    }
    @Override
    public void onLocationChanged(Location location) {
        tv_location.setText(hereLocation(location));
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

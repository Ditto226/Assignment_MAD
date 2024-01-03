package com.example.assignment;

import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public interface IBaseGpsListener extends LocationListener {

    public void onLocationChanged(Location location);
    public void onProviderDisable(String provider);
    public void onProviderEnable(String provider);
    public void onStatusChanged(String provider, int status, Bundle extras);
    public void onGnssStatusChanged(int event);
}

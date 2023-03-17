package com.example.inventairelol.Service;

import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public interface BaseGpsListener extends LocationListener, GpsStatus.Listener {

    public void onLocationChanged(Location location);

    public void onProviderDisabled(String provider);

    public void onProviderEnabled(String probider);

    public void onStatusChanged(String provider, int status, Bundle extras);

    public void onGpsStatusChanged(int event);

}

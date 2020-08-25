package com.example.campusgeoquiz;
import android.app.MediaRouteActionProvider;

import com.google.android.gms.maps.model.LatLng;

public class MyLatLng {
    private double latitude;
    private double longitude;
    private LatLng geofence;

    public MyLatLng(){

    }

    public LatLng getGeofence() {

        return geofence;
    }

    public void setGeofence(LatLng geofence) {
        this.geofence = geofence;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}

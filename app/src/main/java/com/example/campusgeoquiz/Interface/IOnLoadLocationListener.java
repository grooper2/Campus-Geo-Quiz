package com.example.campusgeoquiz.Interface;
import com.example.campusgeoquiz.PicassoMarker;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.model.Marker;


import java.util.ArrayList;
import java.util.List;

import model.Quiz;

public interface IOnLoadLocationListener {
    void onLoadLocationSuccess(List<Quiz> latLngs, List<Quiz> images);
    void onLoadLocationFailed(String message);
}

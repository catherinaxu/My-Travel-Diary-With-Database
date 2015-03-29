package com.example.catherinaxu.mycityfinder;

import android.location.Address;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by catherinaxu on 3/26/15.
 */
public class Loc {

    public int id;
    public String feature_name;
    public double lat;
    public double lng;
    public String description;

    //initializer
    public Loc (int id, String feature_name, double lat, double lng, String description) {
        this.id = id;
        this.feature_name = feature_name;
        this.lat = lat;
        this.lng = lng;
        this.description = description;
    }

    //getter and setter methods for each of the fields
    public int getId() {return this.id; }

    public String getFeatureName() {
        return this.feature_name;
    }

    public double getLatitude() {
        return this.lat;
    }

    public double getLongitude() {
        return this.lng;
    }

    public String getDescription() {
        return this.description;
    }
}


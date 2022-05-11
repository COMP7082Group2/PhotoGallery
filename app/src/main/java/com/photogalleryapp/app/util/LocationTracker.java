package com.photogalleryapp.app.util;

import android.location.Location;
import android.location.LocationListener;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

public class LocationTracker implements LocationListener
{
    private static LocationTracker instance;

    private Location lastLoc;

    public Location getLocation() {
        return lastLoc;
    }

    private LocationTracker() {

    }

    public static LocationTracker getInstance() {
        if(instance == null)
            instance = new LocationTracker();

        return instance;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        lastLoc = location;
    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {
        LocationListener.super.onLocationChanged(locations);

        if(locations.size() > 0)
            lastLoc = locations.get(locations.size() - 1);
    }
}

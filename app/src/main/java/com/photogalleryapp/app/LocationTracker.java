package com.photogalleryapp.app;

import android.location.Location;
import android.location.LocationListener;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

public class LocationTracker implements LocationListener
{
    private Location lastLoc;

    public Location getLocation() {
        return lastLoc;
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

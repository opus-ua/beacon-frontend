package org.opus.beacon;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.model.LatLng;

abstract public class LocalizingMap extends ConstrainedCameraMap
    implements LocationListener,
    ActivityCompat.OnRequestPermissionsResultCallback {

    private LocationManager mLocationManager;
    private float GPS_ACCURACY = 50.0f; //meters
    private long GPS_WAIT = 4000; //milliseconds
    private int ACCESS_FINE_LOCATION_TAG = 125;

    @Override
    protected void setUpMap() {
        super.setUpMap();
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        localize();
    }

    protected void localize() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION_TAG);
        } else {
            mLocationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, this, null);
            mLocationManager.requestLocationUpdates(mLocationManager.GPS_PROVIDER, 1000, 1, this);
        }
    }

    private boolean shouldUseLocation(Location location) {
        return location.getAccuracy() < GPS_ACCURACY;
    }

    private void performInitialZoom(Location location) {
        zoomToLocation(new LatLng(location.getLatitude(), location.getLongitude()));
        mLocationManager.removeUpdates(this);
        execute(location);
    }

    public abstract void execute(Location location);

    private Handler mLocalizationTimeoutHandler = null;

    @Override
    public void onLocationChanged(Location location) {
        final Location loc = location;
        if (mLocalizationTimeoutHandler == null) {
            mLocalizationTimeoutHandler = new Handler();
            mLocalizationTimeoutHandler.postDelayed( new Runnable() {
                @Override
                public void run() {
                    performInitialZoom(loc);
                }
            }, GPS_WAIT);
        }

        if (!shouldUseLocation(location))
            return;

        performInitialZoom(location);
        mLocalizationTimeoutHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode != ACCESS_FINE_LOCATION_TAG)
            return;

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            localize();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}

}

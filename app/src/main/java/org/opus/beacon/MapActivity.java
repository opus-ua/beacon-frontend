package org.opus.beacon;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import android.location.LocationManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class MapActivity extends ConstrainedCameraMap
    implements LocationListener,
    ActivityCompat.OnRequestPermissionsResultCallback,
    GoogleMap.OnMarkerClickListener {

    private LocationManager mLocationManager;
    private Context context;

    private float GPS_ACCURACY = 50.0f; //meters
    private long GPS_WAIT = 4000; //milliseconds

    private int ACCESS_FINE_LOCATION_TAG = 125;
    private int BEACON_SUBMISSION = 37;

    private BeaconRestClient mClient;
    private Auth mAuth;

    private HashMap<Marker, BeaconThumb> mMarkerHash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        mMarkerHash = new HashMap<Marker, BeaconThumb>();

        try {
            mAuth = new Auth(this);
            mClient = new BeaconRestClient(mAuth.getId(), mAuth.getSecret());
        } catch (Exception e) {
            toastError(e.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BEACON_SUBMISSION && resultCode == RESULT_OK) {
           localize();
        }
    }

    @Override
    protected void setUpMap() {
        super.setUpMap();
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mMap.setOnMarkerClickListener(this);
        localize();
    }

    private void localize() {
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

    public void toastError(String err) {
        Toast toast = Toast.makeText(context, err, Toast.LENGTH_SHORT);
        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
        v.setTextColor(Color.WHITE);
        v.setBackgroundColor(0x00000000);
        toast.show();
    }

    private boolean shouldUseLocation(Location location) {
        return location.getAccuracy() < GPS_ACCURACY;
    }

    private void performInitialZoom(Location location) {
        zoomToLocation(location);
        mLocationManager.removeUpdates(this);
        new GetLocalBeacons().execute(location);
    }

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
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode != ACCESS_FINE_LOCATION_TAG)
            return;

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            localize();
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        BeaconThumb thumb = mMarkerHash.get(marker);
        Intent launchThreadView = new Intent(this, ThreadView.class);
        launchThreadView.putExtra("beaconID", thumb.getId());
        startActivity(launchThreadView);
        return false;
    }

    private Location mLastLoad = null;
    private void loadNewBeacons(CameraPosition position) {
        if (mLastLoad == null)
            return;

        float dist = distanceBetween(mLastLoad, position.target);

        // distance greater than a mile
        Location newLocation = new Location("");
        newLocation.setLatitude(position.target.latitude);
        newLocation.setLongitude(position.target.longitude);

        float zoom = getZoomRatio(position);
        if (dist > 1609.0f && zoom > 0.5f) {
            new GetLocalBeacons().execute(newLocation);
        }
    }

    @Override
    public void onCameraChange(CameraPosition position) {
        super.onCameraChange(position);
        loadNewBeacons(position);
    }

    private boolean thumbOnMap(BeaconThumb thumb) {
        for(BeaconThumb thumbOnMap : mMarkerHash.values()) {
            if (thumbOnMap.getId() == thumb.getId())
                return true;
        }

        return false;
    }

    private class GetLocalBeacons extends AsyncTask<Location, Void, RestException> {
        @Override
        protected RestException doInBackground(Location... params) {
            try {
                final ArrayList<BeaconThumb> thumbs = mClient.getLocalBeacons(params[0]);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (BeaconThumb thumb : thumbs) {
                            if (!thumbOnMap(thumb)) {
                                Marker marker = mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(thumb.getLatitude(), thumb.getLongitude()))
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.beacon_marker)));
                                mMarkerHash.put(marker, thumb);
                            }
                        }
                    }
                });
                mLastLoad = params[0];
                return null;
            } catch (RestException e) {
                return e;
            }
        }

        @Override
        protected void onPostExecute(RestException err) {
            if(err != null && err.shouldInformUser()) {
                toastError(err.getMessage());
            }
        }
    }

    public void launchCameraView(View view) {
        Intent launchCamera  = new Intent(this, BeaconSubmissionView.class);
        startActivityForResult(launchCamera, BEACON_SUBMISSION);
    }
}

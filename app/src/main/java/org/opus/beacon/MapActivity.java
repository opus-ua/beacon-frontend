package org.opus.beacon;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MapActivity extends LocalizingMap
    implements GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener {

    private Context context;
    private int BEACON_SUBMISSION = 37;

    private BeaconRestClient mClient;
    private Auth mAuth;
    private HashMap<Marker, BeaconThumb> mMarkerHash;
    private Bitmap mScaledMarker = null;
    private int BEACON_MARKER_WIDTH = 100;

    private BeaconInfoWindowAdapter mInfoWindowAdapter;

    public MapActivity() {
        context = this;
        mMarkerHash = new HashMap<Marker, BeaconThumb>();
        mInfoWindowAdapter = new BeaconInfoWindowAdapter(mMarkerHash, context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bitmap markerBmp = BitmapFactory.decodeResource(getResources(), R.drawable.beacon_marker);
        float aspect = (float) markerBmp.getWidth() / (float) markerBmp.getHeight();
        int markerHeight = (int) (BEACON_MARKER_WIDTH / aspect);
        mScaledMarker = Bitmap.createScaledBitmap(markerBmp, BEACON_MARKER_WIDTH, markerHeight, false);

        try {
            mAuth = new Auth(this);
            mClient = new BeaconRestClient(mAuth.getId(), mAuth.getSecret());
        } catch (Exception e) {
            toastError(e.getMessage());
        }
    }

    @Override
    protected void setUpMap() {
        super.setUpMap();
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setInfoWindowAdapter(mInfoWindowAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BEACON_SUBMISSION && resultCode == RESULT_OK) {
            localize();
        }
    }

    public void toastError(String err) {
        Toast toast = Toast.makeText(context, err, Toast.LENGTH_SHORT);
        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
        v.setTextColor(Color.WHITE);
        v.setBackgroundColor(0x00000000);
        toast.show();
    }

    public void execute(Location location) {
        new GetLocalBeacons().execute(location);
    }

    private void launchThreadViewForMarker(Marker marker) {
        BeaconThumb thumb = mMarkerHash.get(marker);
        Intent launchThreadView = new Intent(this, ThreadView.class);
        launchThreadView.putExtra("beaconID", thumb.getId());
        startActivity(launchThreadView);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        launchThreadViewForMarker(marker);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (!marker.isInfoWindowShown()) {
            marker.showInfoWindow();
        } else {
            launchThreadViewForMarker(marker);
        }
        return true;
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
        openBeaconPreview(position);
    }

    private void closeInfoWindow() {
        for (Marker marker : mMarkerHash.keySet()) {
            if (marker.isInfoWindowShown()) {
                marker.hideInfoWindow();
            }
        }
    }

    private float squaredDistFromSelectionCenter(LatLng loc) {
        Projection projection = mMap.getProjection();
        Point p = projection.toScreenLocation(loc);

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        // center circle at 1/2 width and 2/3 height (1/3 from bottom)
        Point center = new Point(width / 2, 2 * height / 3);
        Point dp = new Point(p.x - center.x, p.y - center.y);
        return dp.x * dp.x + dp.y * dp.y;
    }

    private boolean inSelectionCircle(LatLng loc) {
        float radius = BEACON_MARKER_WIDTH * 3.0f;
        return squaredDistFromSelectionCenter(loc) < (radius * radius);
    }

    private void openBeaconPreview(CameraPosition position) {
        if (mMarkerHash.size() == 0) {
            return;
        }

        if (getZoomRatio(position) < 0.6f) {
            closeInfoWindow();
            return;
        }

        HashMap<Marker, Float> distances = new HashMap<Marker, Float>();
        for (Map.Entry<Marker, BeaconThumb> entry : mMarkerHash.entrySet()) {
            Marker marker = entry.getKey();
            BeaconThumb thumb = entry.getValue();
            LatLng beaconLoc = new LatLng(thumb.getLatitude(), thumb.getLongitude());
            // float distance = distanceBetween(beaconLoc, position.target);
            float distance = squaredDistFromSelectionCenter(beaconLoc);
            distances.put(marker, distance);
        }

        ValueSortedMap<Marker, Float> sortedMap = new ValueSortedMap<>(distances);
        Marker nearestMarker = sortedMap.firstKey();
        if (nearestMarker != null && inSelectionCircle(nearestMarker.getPosition())) {
            if (!nearestMarker.isInfoWindowShown()) {
                nearestMarker.showInfoWindow();
            }
        } else {
            closeInfoWindow();
        }
    }

    private boolean thumbOnMap(BeaconThumb thumb) {
        for(BeaconThumb thumbOnMap : mMarkerHash.values()) {
            if (thumbOnMap.getId() == thumb.getId())
                return true;
        }

        return false;
    }

    private void putBeaconMarker(BeaconThumb thumb) {
        Marker marker = mMap.addMarker(new MarkerOptions()
            .position(new LatLng(thumb.getLatitude(), thumb.getLongitude()))
            .icon(BitmapDescriptorFactory.fromBitmap(mScaledMarker)));
        mMarkerHash.put(marker, thumb);
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
                                putBeaconMarker(thumb);
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

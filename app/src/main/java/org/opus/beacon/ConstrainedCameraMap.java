package org.opus.beacon;

import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.VisibleRegion;

public class ConstrainedCameraMap extends FragmentActivity
    implements GoogleMap.OnCameraChangeListener {

    private float MAX_ZOOM = 18.0f;
    private float MIN_ZOOM = 3.0f;

    private float MAX_TILT = 60.0f;
    private float MIN_TILT = 0.0f;

    protected GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    protected void setUpMap() {
        mMap.setOnCameraChangeListener(this);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.getUiSettings().setTiltGesturesEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setIndoorLevelPickerEnabled(false);

        // start at center of contiguous United States
        // this location should probably be a project-level property
        CameraUpdate center = CameraUpdateFactory.newLatLngZoom(new LatLng(39.8282f, -98.5795f), MIN_ZOOM);

        mMap.moveCamera(center);
    }

    protected void putLatLngAtScreenCoords(LatLng loc, Point target) {
        LatLng curTarget = mMap.getCameraPosition().target;
        Projection projection = mMap.getProjection();
        double selectLat = projection.fromScreenLocation(target).latitude;

        double targetLongitude = loc.longitude;
        double targetLatitude = loc.latitude + (curTarget.latitude - selectLat);

        LatLng latLngTarget = new LatLng(targetLatitude, targetLongitude);
        float zoom = mMap.getCameraPosition().zoom;

        CameraPosition movement = new CameraPosition.Builder()
                .target(latLngTarget)
                .zoom(zoom)
                .tilt(zoomToTilt(zoom))
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(movement), 500, null);
    }

    protected void zoomToLocation(LatLng location) {
        float zoom;
        if (mCurrentCamera == null || mCurrentCamera.zoom < (MAX_ZOOM + MIN_ZOOM) / 2) {
            zoom = MAX_ZOOM;
        } else {
            zoom = mCurrentCamera.zoom;
        }

        LatLng newLocation = location;
        if (mCurrentCamera != null) {
            LatLng lastLocation = mCurrentCamera.target;
            float dist = distanceBetween(location, lastLocation);

            if (dist < 30.0f) {
                newLocation = lastLocation;
            }
        }

        CameraPosition movement = new CameraPosition.Builder()
                .target(newLocation)
                .zoom(zoom)
                .tilt(zoomToTilt(zoom))
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(movement), 2000, null);
    }

    @Override
    public void onCameraChange(CameraPosition position) {
        mCurrentCamera = position;
        mCameraHandler.removeCallbacks(mCameraCheckRunnable);
        checkCameraAnimating();
    }

    private float zoomToTilt(float zoom) {
        if (zoom > MAX_ZOOM) {
            return MAX_TILT;
        } else if (zoom < MIN_ZOOM) {
            return MIN_TILT;
        } else {
            float m = (MAX_TILT - MIN_TILT) / (MAX_ZOOM - MIN_ZOOM);
            return m * (zoom - MIN_ZOOM) + MIN_TILT;
        }
    }

    public void onCameraAnimationEnd() {
        float curZoom = mCurrentCamera.zoom;
        float curTilt = zoomToTilt(mCurrentCamera.zoom);
        LatLng location = mCurrentCamera.target;
        final CameraPosition movement = new CameraPosition.Builder()
                .target(location)
                .zoom(curZoom)
                .tilt(curTilt)
                .build();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(movement));
            }
        });
    }

    private CameraPosition mPreviousCamera = null;
    private CameraPosition mCurrentCamera = null;
    private Handler mCameraHandler = new Handler();
    private boolean mAnimationEndHandled = false;
    private Runnable mCameraCheckRunnable = new Runnable() {
        @Override
        public void run() {
            checkCameraAnimating();
        }
    };

    public void checkCameraAnimating() {
        if (mPreviousCamera == null || animationInProgress(mCurrentCamera)) {
            mPreviousCamera = mCurrentCamera;
            if (mAnimationEndHandled)
            mAnimationEndHandled = false;
            mCameraHandler.postDelayed(mCameraCheckRunnable, 30);
        } else if (!mAnimationEndHandled) {
            mAnimationEndHandled = true;
            onCameraAnimationEnd();
        }
    }

    private boolean animationInProgress(CameraPosition pos) {
        if (mPreviousCamera.zoom != pos.zoom)
            return true;

        if (!mPreviousCamera.target.equals(pos.target))
            return true;

        return false;
    }

    protected float distanceBetween(LatLng a, LatLng b) {
        float[] results = new float[1];
        Location.distanceBetween(a.latitude,
                a.longitude,
                b.latitude,
                b.longitude,
                results);
        return results[0];
    }

    protected float distanceBetween(Location a, LatLng b) {
        return distanceBetween(new LatLng(a.getLatitude(), a.getLongitude()), b);
    }

    // returns how zoomed in the camera currently is
    // returns 1.0 for max zoom
    // return 0.0 for min zoom
    protected float getZoomRatio(CameraPosition position) {
       return position.zoom / (MAX_ZOOM + MIN_ZOOM);
    }
}
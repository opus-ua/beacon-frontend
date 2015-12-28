package org.opus.beacon;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;

public class BeaconInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private HashMap<Marker, BeaconThumb> mMarkerHash;
    private Context mContext;

    public BeaconInfoWindowAdapter(HashMap<Marker, BeaconThumb> markerHash, Context context) {
        mMarkerHash = markerHash;
        mContext = context;
    }

    public View getInfoContents(Marker marker) {
        // check when you take a marker out, bro
        BeaconThumb thumb = mMarkerHash.get(marker);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View window = inflater.inflate(R.layout.beacon_info_window, null);
        ImageView thumbImage = (ImageView) window.findViewById(R.id.thumbnailImage);
        float aspect = (float) thumb.img.getWidth() / (float) thumb.img.getHeight();
        float windowWidth = mContext.getResources().getDimension(R.dimen.info_window_width);
        int imgHeight = (int)(windowWidth / aspect);
        Bitmap scaledBmp = Bitmap.createScaledBitmap(thumb.img, (int)windowWidth, imgHeight, false);
        thumbImage.setImageBitmap(scaledBmp);
        return window;
    }

    public View getInfoWindow(Marker marker) {
        return null;
    }
}

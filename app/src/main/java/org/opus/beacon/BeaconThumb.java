package org.opus.beacon;

import android.graphics.Bitmap;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class BeaconThumb {
    public BeaconThumb() {}

    public int _id;
    public int _userid;
    public String _text;
    public float _latitude;
    public float _longitude;

    @JsonIgnore
    public Bitmap img;

    public int getId() { return _id; }
    public void setId(int id) { _id = id; }
    public int getUserid() { return _userid; }
    public void setUserid(int userid) { _userid = userid; }
    public String getText() { return _text; }
    public void setText(String text) { _text = text; }
    public float getLatitude() { return _latitude; }
    public void setLatitude(float latitude) { _latitude = latitude; }
    public float getLongitude() { return _longitude; }
    public void setLongitude(float longitude) { _longitude = longitude; }
}

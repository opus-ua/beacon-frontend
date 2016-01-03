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

    public int _hearts;
    public long _time;
    public String _username;
    public boolean _hearted;
    public int _comments;

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
    public int getHearts() { return _hearts; }
    public void setHearts(int hearts) { _hearts = hearts; }
    public long getTime() { return _time; }
    public void setTime(long time) { _time = time; }
    public String getUsername() { return _username; }
    public void setUsername(String username) { _username = username; }
    public boolean getHearted() { return _hearted; }
    public void setHearted(boolean hearted) { _hearted = hearted; }
    public int getComments() { return _comments; }
    public void setComments(int comments) { _comments = comments; }
}

package org.opus.beacon;


public class JsonMsg {
    public static class CreateAccountRequest {
        public CreateAccountRequest(String username, String token) {
            setUsername(username);
            setToken(token);
        }

        private String _username;
        private String _token;
        public String getUsername() {return _username;}
        public void setUsername(String u) {_username = u;}
        public String getToken() {return _token;}
        public void setToken(String t) {_token = t;}
    }

    public static class CreateAccountResponse {
        public CreateAccountResponse() {}

        private int _id;
        private String _secret;
        public int getId() {return _id;}
        public void setId(int id) {_id = id;}
        public String getSecret() {return _secret;}
        public void setSecret(String secret) {_secret = secret;}
    }

    public static class LocalBeaconRequest {
        public LocalBeaconRequest(float latitude, float longitude, float radius) {
            _latitude = latitude;
            _longitude = longitude;
            _radius = radius;
        }

        private float _latitude;
        private float _longitude;
        private float _radius;

        public float getLatitude() { return _latitude; }
        public void setLatitude(float latitude) { _latitude = latitude; }
        public float getLongitude() { return _longitude; }
        public void setLongitude(float longitude) { _longitude = longitude; }
        public float getRadius() { return _radius; }
        public void setRadius(float radius) { _radius = radius; }
    }



    public static class LocalBeaconResponse {
        public LocalBeaconResponse() {}

        public BeaconThumb[] _beacons;
        public BeaconThumb[] getBeacons() { return _beacons; }
        public void setBeacons(BeaconThumb[] beacons) { _beacons = beacons; }
    }

    public static class PostCommentRequest {
        public PostCommentRequest(int beaconID, String text) {
            _beaconid = beaconID;
            _text = text;
        }

        private int _beaconid;
        private String _text;

        public int getBeaconid() { return _beaconid; }
        public void setBeaconid(int beaconid) { _beaconid = beaconid; }
        public String getText() { return _text; }
        public void setText(String text) { _text = text; }
    }

    public static class ErrorResp {
        public ErrorResp() {}

        private String _error;
        private int _code;

        public String getError() {return _error;}
        public void setError(String error) {_error = error;}
        public int getCode() { return _code; }
        public void setCode(int code) {_code = code;}
    }

}

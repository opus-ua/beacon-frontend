package org.opus.beacon;

import android.graphics.Bitmap;
import android.location.Location;

import java.util.ArrayList;

public class BeaconRestClient extends RestClient {
    public BeaconRestClient() {
        super();
        setBackend(BuildConfig.SERVER_URL);
    }

    public BeaconRestClient(String userId, String userSecret) {
        super();
        setBackend(BuildConfig.SERVER_URL);
        setAuth(userId, userSecret);
    }

    public Thread getThread(String postID) throws RestException {
        RestResponse resp = get(URI("beacon", postID));
        Thread thread = resp.getPartJson(Thread.class);
        Bitmap img = resp.getPartImage();
        thread.setImage(img);
        return thread;
    }

    public int postBeacon(JsonMsg.PostBeaconRequest beaconRequest, Bitmap image) throws RestException {
        RestRequest request = post(URI("beacon"));
        request.addPartJson(beaconRequest);
        request.addPartImage(image);
        request.finalizeMultipart();
        RestResponse response = send(request);
        JsonMsg.BeaconResponse result = response.getJson(JsonMsg.BeaconResponse.class);
        return result.getId();

    }

    public void heartPost(String postID) throws RestException {
        send(post(URI("heart", postID)));
    }

    public void unheartPost(String postID) throws RestException {
        send(post(URI("unheart", postID)));
    }

    public ArrayList<BeaconThumb> getLocalBeacons(Location location) throws RestException {
        JsonMsg.LocalBeaconRequest msg = new JsonMsg.LocalBeaconRequest((float) location.getLatitude(),
                (float) location.getLongitude(),
                1.0f);
        RestRequest req = post(URI("local"));
        req.writeJson(msg);
        RestResponse resp = send(req);
        JsonMsg.LocalBeaconResponse respJson = resp.getPartJson(JsonMsg.LocalBeaconResponse.class);
        ArrayList<BeaconThumb> thumbs = new ArrayList<BeaconThumb>();
        for (BeaconThumb thumb : respJson.getBeacons()) {
            thumb.img = resp.getPartImage();
            thumbs.add(thumb);
        }
        return thumbs;
    }

    public JsonMsg.CreateAccountResponse createAccount(String username, String token) throws RestException {
        RestRequest req = post(URI("createaccount"));
        req.writeJson(new JsonMsg.CreateAccountRequest(username, token));
        RestResponse resp = send(req);
        return resp.getJson(JsonMsg.CreateAccountResponse.class);
    }

    public void postComment(JsonMsg.PostCommentRequest comment) throws RestException {
        RestRequest req = post(URI("comment"));
        req.writeJson(comment);
        send(req);
    }
}



package org.opus.beacon;

import android.graphics.Bitmap;

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

    public void heartPost(String postID) throws RestException {
        send(post(URI("heart", postID)));
    }

    public void unheartPost(String postID) throws RestException {
        send(post(URI("unheart", postID)));
    }

    public JsonMsg.CreateAccountResponse createAccount(String username, String token) throws RestException {
        RestRequest req = post(URI("createaccount"));
        req.writeJson(new JsonMsg.CreateAccountRequest(username, token));
        RestResponse resp = send(req);
        return resp.getJson(JsonMsg.CreateAccountResponse.class);
    }
}



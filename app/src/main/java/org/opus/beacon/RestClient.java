package org.opus.beacon;

import android.util.Base64;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

public class RestClient {
    private String backendUrl;
    private String authId;
    private String authSecret;

    public RestClient() {
        authId = null;
        authSecret = null;
    }

    protected void setBackend(String url) {
        backendUrl = url;
    }

    protected void setAuth(String _authId, String _authSecret) {
        authId = _authId;
        authSecret = _authSecret;
    }

    protected void addAuth(RestRequest req) {
        if (authId == null || authSecret == null)
            return;

        String authValue = authId + ":" + authSecret;
        String basicAuthStr = Base64.encodeToString(authValue.getBytes(), Base64.DEFAULT);
        req.setHeader("Authorization", "Basic " + basicAuthStr.trim());
    }

    protected void addUserAgent(RestRequest req) {
        String version = BuildConfig.CLIENT_VERSION;
        String userAgent = "Beacon Android Client " + version;
        req.setHeader("User-Agent", userAgent.trim());
    }

    protected RestResponse get(String url) throws RestException {
        try {
            HttpClient client = new DefaultHttpClient();
            RestRequest req = new RestRequest(new HttpGet(url));
            addAuth(req);
            addUserAgent(req);
            HttpResponse rawResp = client.execute(req.getRawReq());
            RestResponse resp = new RestResponse(rawResp);
            processErrorMsg(resp);
            return resp;
        } catch (IOException e) {
            throw new RestException(RestException.ConnectionError, e.getMessage());
        }
    }

    protected RestRequest post(String url) throws RestException {
        try {
            RestRequest req = new RestRequest(new HttpPost(url));
            addAuth(req);
            addUserAgent(req);
            return req;
        } catch (Exception e) {
            throw new RestException(RestException.ProtocolError, e.getMessage());
        }
    }

    protected RestResponse send(RestRequest req) throws RestException {
       try {
           HttpClient client = new DefaultHttpClient();
           RestResponse resp = new RestResponse(client.execute(req.getRawReq()));
           processErrorMsg(resp);
           return resp;
       } catch(IOException e) {
           throw new RestException(RestException.ConnectionError, e.getMessage());
       }
    }

    private void processErrorMsg(RestResponse resp) throws RestException {
        try {
            if (resp.getStatusCode() != 200) {
                JsonMsg.ErrorResp err = resp.getJson(JsonMsg.ErrorResp.class);
                throw new RestException(err.getCode(), err.getError());
            }
        } catch (RestException e) {
            throw e;
        }
    }

    protected String URI(String... elements) {
        String uri = backendUrl;
        for (int i = 0; i < elements.length; i++) {
            uri += "/" + elements[i];
        }
        return uri;
    }
}
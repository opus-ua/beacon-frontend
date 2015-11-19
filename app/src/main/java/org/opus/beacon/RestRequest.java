package org.opus.beacon;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ByteArrayEntity;

import java.net.HttpRetryException;

public class RestRequest {
    public RestRequest(HttpRequestBase _req) {
        req = _req;
    }

    public void setHeader(String key, String value) {
        req.setHeader(key, value);
    }

    public <T> void writeJson(T obj) throws RestException {
        if (!(req instanceof HttpPost))
            throw new RestException(RestException.ProtocolError, "Can't write to non-POST request.");

        HttpPost request = (HttpPost) req;
        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonStr = mapper.writeValueAsString(obj);
            HttpEntity entity = new ByteArrayEntity(jsonStr.getBytes("UTF-8"));
            request.setEntity(entity);
        } catch (Exception e) {
            throw new RestException(RestException.JsonError, e.getMessage());
        }
    }

    public HttpRequestBase getRawReq() {
        return req;
    }

    private HttpRequestBase req;
}

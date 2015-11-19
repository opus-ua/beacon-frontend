package org.opus.beacon;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;

public class RestRequest {
    public RestRequest(HttpPost _req) {
        req = _req;
    }

    public void setHeader(String key, String value) {
        req.setHeader(key, value);
    }

    public <T> void writeJson(T obj) throws RestException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonStr = mapper.writeValueAsString(obj);
            HttpEntity entity = new ByteArrayEntity(jsonStr.getBytes("UTF-8"));
            req.setEntity(entity);
        } catch (Exception e) {
            throw new RestException(RestException.JsonError, e.getMessage());
        }
    }

    public HttpPost getRawReq() {
        return req;
    }

    private HttpPost req;
}

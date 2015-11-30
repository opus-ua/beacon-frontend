package org.opus.beacon;

import android.graphics.Bitmap;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import java.io.ByteArrayOutputStream;

public class RestRequest {
    public RestRequest(HttpRequestBase _req) {
        req = _req;
        multipartRequest = null;
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

    private MultipartEntityBuilder multipartRequest;
    public MultipartEntityBuilder getBuilder() {
        if (multipartRequest == null) {
            multipartRequest = MultipartEntityBuilder.create();
        }

        return multipartRequest;
    }

    public <T> void addPartJson(T obj) throws RestException {
        if (!(req instanceof HttpPost))
            throw new RestException(RestException.ProtocolError, "Can't write to non-POST request.");

        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonStr = mapper.writeValueAsString(obj);
            getBuilder().addTextBody("beacon-info", jsonStr, ContentType.APPLICATION_JSON);
        }
        catch (Exception e) {
            throw new RestException(RestException.JsonError, e.getMessage());
        }

    }

    public void addPartImage (Bitmap image) throws RestException{
        if (!(req instanceof HttpPost))
            throw new RestException(RestException.ProtocolError, "Can't write to non-POST request.");

        try {
            ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayBitmapStream);
            byte[] imageBytes = byteArrayBitmapStream.toByteArray();
            getBuilder().addBinaryBody("image", imageBytes, ContentType.create("image/jpeg"), "image.jpg");
        } catch (Exception e) {
            throw new RestException(RestException.ProtocolError, e.getMessage());
        }
    }

    public void finalizeMultipart() throws RestException {
        if (multipartRequest == null)
            throw new RestException(RestException.ProtocolError, "Can't finalize non-multipart request.");

        HttpPost request = (HttpPost) req;
        HttpEntity entity = multipartRequest.build();
        request.setEntity(entity);
    }

    public HttpRequestBase getRawReq() {
        return req;
    }

    private HttpRequestBase req;
}

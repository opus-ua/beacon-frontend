package org.opus.beacon;

import android.graphics.Bitmap;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.FormBodyPartBuilder;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;

import java.io.ByteArrayOutputStream;
import java.net.HttpRetryException;

public class RestRequest {
    private MultipartEntityBuilder multipartRequest;
    public RestRequest(HttpRequestBase _req) {
        req = _req;
        multipartRequest = MultipartEntityBuilder.create();
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
    public <T> void addPartJson(T obj) throws RestException {
        if (!(req instanceof HttpPost))
            throw new RestException(RestException.ProtocolError, "Can't write to non-POST request.");
        HttpPost request = (HttpPost) req;
        request.setHeader("Content-Type", "multipart/form-data");
        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonStr = mapper.writeValueAsString(obj);
            FormBodyPartBuilder bodyPartBuilder = FormBodyPartBuilder.create("jsonPart", new StringBody(jsonStr, ContentType.APPLICATION_JSON));
            FormBodyPart bodyPart = bodyPartBuilder.build();
            multipartRequest.addPart(bodyPart);
        }
        catch (Exception e) {
            throw new RestException(RestException.JsonError, e.getMessage());
        }

    }

    public void addPartImage (Bitmap image) throws RestException{
        if (!(req instanceof HttpPost))
            throw new RestException(RestException.ProtocolError, "Can't write to non-POST request.");
        HttpPost request = (HttpPost) req;

            ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayBitmapStream);
            byte[] b = byteArrayBitmapStream.toByteArray();
            FormBodyPartBuilder bodyPartBuilder = FormBodyPartBuilder.create("imagePart", new ByteArrayBody(b, "image/jpeg"));
            FormBodyPart bodyPart = bodyPartBuilder.build();
            multipartRequest.addPart(bodyPart);
            HttpEntity entity = multipartRequest.build();
            request.setEntity(entity);
    }

    public HttpRequestBase getRawReq() {
        return req;
    }

    private HttpRequestBase req;
}

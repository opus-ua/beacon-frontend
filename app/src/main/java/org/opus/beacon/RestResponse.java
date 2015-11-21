package org.opus.beacon;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.HttpResponse;

import java.io.IOException;
import java.io.InputStream;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

public class RestResponse {
    public RestResponse(HttpResponse _resp) {
        resp = _resp;
        partIndex = 0;
        multipartBody = null;
    }

    private HttpResponse resp;
    private int partIndex;
    private MimeMultipart multipartBody;

    public int getStatusCode() {
        return resp.getStatusLine().getStatusCode();
    }

    public HttpResponse getRawResp() {
        return resp;
    }

    public <T> T getJson(Class<T> valueType) throws RestException {
        try {
            InputStream content = resp.getEntity().getContent();
            ObjectMapper mapper = new ObjectMapper();
            T res =  mapper.readValue(content, valueType);
            content.close();
            return res;
        } catch (JsonParseException e) {
            throw new RestException(RestException.JsonError, e.getMessage());
        } catch (JsonMappingException e) {
            throw new RestException(RestException.JsonError, e.getMessage());
        } catch (Exception e) {
            throw new RestException(RestException.ProtocolError, e.getMessage());
        }
    }

    public InputStream getPart() throws RestException {
        try {
            if (multipartBody == null) {
                ByteArrayDataSource ds = new ByteArrayDataSource(resp.getEntity().getContent(), "multipart/form-data");
                multipartBody = new MimeMultipart(ds);
            }
            return multipartBody.getBodyPart(partIndex++).getInputStream();
        } catch (IOException e) {
            throw new RestException(RestException.ConnectionError, e.getMessage());
        } catch (MessagingException e) {
            throw new RestException(RestException.ProtocolError, e.getMessage());
        }
    }

    public boolean endOfMultipart() throws RestException {
        if (multipartBody == null) {
            return true;
        }
        try {
            return partIndex == multipartBody.getCount();
        } catch (MessagingException e) {
            throw new RestException(RestException.ProtocolError, e.getMessage());
        }
    }

    public <T> T getPartJson(Class<T> valueType) throws RestException {
        try {
            InputStream content = getPart();
            ObjectMapper mapper = new ObjectMapper();
            T res =  mapper.readValue(content, valueType);
            content.close();
            return res;
        } catch (RestException e) {
            throw e;
        } catch (Exception e) {
            throw new RestException(RestException.JsonError, e.getMessage());
        }
    }

    public Bitmap getPartImage() throws RestException {
        try {
            InputStream content = getPart();
            Bitmap bmp = BitmapFactory.decodeStream(content);
            content.close();
            return bmp;
        } catch (RestException e) {
           throw e;
        } catch (Exception e) {
            throw new RestException(RestException.ProtocolError, e.getMessage());
        }
    }
}

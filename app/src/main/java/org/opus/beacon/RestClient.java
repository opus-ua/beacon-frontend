package org.opus.beacon;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.http.entity.ByteArrayEntity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ResourceBundle;

import javax.mail.BodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;



public class RestClient {
    private class CreateAccountRequest {
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

    private String backendUrl = BuildConfig.SERVER_URL;
    public Thread getThread(String postID) {
        Log.d("RestClient", "Server URL: '" + backendUrl + "'");
        try {
            HttpClient client = new DefaultHttpClient();
            String getURL = URI("beacon", postID);
            HttpGet get = new HttpGet(getURL);
            HttpResponse response = client.execute(get);
            ByteArrayDataSource ds = new ByteArrayDataSource(response.getEntity().getContent(), "multipart/form-data");
            MimeMultipart multipart = new MimeMultipart(ds);
            BodyPart jsonPart = multipart.getBodyPart(0);
            BodyPart imagePart = multipart.getBodyPart(1);
            InputStream imageStream = imagePart.getInputStream();
            Bitmap bmp = BitmapFactory.decodeStream(imageStream);
            InputStream jsonStream =  jsonPart.getInputStream();
            ObjectMapper mapper = new ObjectMapper();
            try {
                Thread result = mapper.readValue(jsonStream, Thread.class);
                result.setImage(bmp);
                imageStream.close();
                jsonStream.close();
                return result;
            } catch (JsonMappingException e) {
                Log.e("Get Thread","MAPPING EXCEPTION",e);
            } catch (IOException e) {
                Log.e("Get Thread","IOException",e);
            }

        } catch (Exception e) {
            Log.e("Get Thread","Unable to connect to server");
        }
        Thread err = new Thread();
        err.setText("Something messed up");
        return err;
    }

    public String heartPost(String postID,Context context) {
        try {
            HttpClient client = new DefaultHttpClient();
            String postURL = URI("heart", postID);
            HttpPost post = new HttpPost(postURL);
            HttpResponse response = client.execute(post);
            int status = response.getStatusLine().getStatusCode();

            if (status == 200){
                return context.getResources().getString(R.string.heart_success);
            }
            else {
                return context.getResources().getString(R.string.heart_failure);
            }
        } catch (Exception e){
            Log.e("Heart Post", "Server Error", e);
            return context.getResources().getString(R.string.server_error);
        }
    }

    public CreateAccountResponse createAccount(String username, String token)
        throws Exception {
        try {
            // String basicAuthStr = Base64Encoder.encode(username + ":" + secret);
            // post.setHeader("Authorization", "Basic " + basicAuthStr);
            HttpClient client = new DefaultHttpClient();
            String postURL = URI("createaccount");
            HttpPost post = new HttpPost(postURL);
            CreateAccountRequest jsonRequest = new CreateAccountRequest(username, token);
            ObjectMapper mapper = new ObjectMapper();
            String jsonStr = mapper.writeValueAsString(jsonRequest);
            HttpEntity entity = new ByteArrayEntity(jsonStr.getBytes("UTF-8"));
            post.setEntity(entity);
            HttpResponse response = client.execute(post);
            int statusCode = response.getStatusLine().getStatusCode(); 
            
            if (statusCode != 200) {
                throw new Exception("Authentication was unsuccessful.");
            }

            String respJson = EntityUtils.toString(response.getEntity());
            CreateAccountResponse resp = mapper.readValue(respJson, CreateAccountResponse.class);
            return resp;
        } catch(Exception e) {
            Log.e("Create Account", "Failed to create account.", e);
            throw e;
        }
    } 

    private String URI(String... elements) {
        String uri = backendUrl;
        for (int i = 0; i < elements.length; i++) {
            uri += "/" + elements[i];
        }
        return uri;
    }
}



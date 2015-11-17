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

import java.io.IOException;
import java.io.InputStream;
import java.util.ResourceBundle;

import javax.mail.BodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

public class RestClient {
    private String backendUrl = BuildConfig.SERVER_URL;
    public Thread getThread(String postID) {
        Log.d("RestClient", "Server URL: '" + backendUrl + "'");
        try {
            HttpClient client = new DefaultHttpClient();
            String getURL = backendUrl + "/beacon/"+postID;
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
        Thread fuckedUp = new Thread();
        fuckedUp.setText("Something messed up");
        return fuckedUp;
    }

    public String heartPost(String postID,Context context) {
        try {
            HttpClient client = new DefaultHttpClient();
            String postURL = backendUrl + "/heart/" + postID;
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
}



package org.opus.beacon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


public class BeaconSubmissionView extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "Beacon Submission View";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static ImageView beaconImage;
    private static Bitmap imageBitmap;
    private static EditText descriptionBox;
    private GoogleApiClient mGoogleApiClient;
    private  Context context;
    private static BeaconRestClient client;
    private static int userId;
    private static boolean pictureTaken;

    private Location mLastLocation;
    private double mLatitude;
    private double mLongitude;




    @Override
    protected void onCreate(Bundle savedInstanceState)  {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.beacon_submission_view);
        Intent intent = getIntent();
        beaconImage = (ImageView) findViewById(R.id.thumbnail);
        descriptionBox = (EditText) findViewById(R.id.descrBox);
        pictureTaken = false;
        context = this;
        try {
            Auth auth = new Auth(context);
            userId = auth.getIntId();
            client = new BeaconRestClient(auth.getId(), auth.getSecret());
        } catch(Exception e) {
            finish();
            return;
        }
        buildGoogleApiClient();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            mLatitude = mLastLocation.getLatitude();
            mLongitude = mLastLocation.getLongitude();

        } else {
            Toast.makeText(this, R.string.no_location_detected, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    public void onTakePicture(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                imageBitmap = (Bitmap) extras.get("data");
                beaconImage.setImageBitmap(imageBitmap);
                pictureTaken = true;
            }
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    public void onSubmitBeacon(View v) {
        if (pictureTaken) {
            JsonMsg.PostBeaconRequest newBeacon = new JsonMsg.PostBeaconRequest(userId, descriptionBox.getText().toString(), (float) mLatitude, (float) mLongitude);
            new SubmitBeacon().execute(newBeacon);
        }
        else {
            Toast toast = Toast.makeText(context, "No picture taken",Toast.LENGTH_SHORT);
        }

    }

    private class SubmitBeacon extends AsyncTask <JsonMsg.PostBeaconRequest, Void, RestException> {
        @Override
        protected RestException doInBackground (JsonMsg.PostBeaconRequest... params){
            try {
                client.postBeacon(params[0], imageBitmap);
                return null;
            } catch(final RestException e) {
                if (e.shouldInformUser()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast toast = Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT);
                            TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                            v.setTextColor(Color.WHITE);
                            v.setBackgroundColor(0x00000000);
                            toast.show();
                        }
                    });
                    finish();
                }

                return e;
            }
        }

        @Override
        protected void onPostExecute (RestException e) {
            if(e != null && e.shouldInformUser()) {
                Toast toast = Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

}
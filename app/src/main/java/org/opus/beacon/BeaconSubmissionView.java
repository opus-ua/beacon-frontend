package org.opus.beacon;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
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
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class BeaconSubmissionView extends Activity
        implements GoogleApiClient.ConnectionCallbacks,
        ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "Beacon Submission View";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static ImageView beaconImage;
    private static Bitmap imageBitmap;
    private static EditText descriptionBox;
    private GoogleApiClient mGoogleApiClient;
    private  Context context;
    private static BeaconRestClient client;
    private static int userId;
    private static int newBeaconId;
    private static boolean pictureTaken;
    private static Uri mImageUri;

    private Location mLastLocation;
    private double mLatitude;
    private double mLongitude;

    private int ACCESS_PERMISSIONS = 127;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beacon_submission_view);
        Intent intent = getIntent();
        beaconImage = (ImageView) findViewById(R.id.thumbnail);
        descriptionBox = (EditText) findViewById(R.id.description_textbox);
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
        onTakePicture();
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

    public void onTakePicture() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    ACCESS_PERMISSIONS);
        } else {
            try {
                takePicture();
            }
            catch (Exception e){
                Toast toast = Toast.makeText(context, "Error taking picture", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    public void takePicture() {
        File photo;
        try {
            photo = this.createTemporaryFile("picture", ".jpg");
            photo.delete();
        }
        catch (Exception e) {
            Log.e(TAG, "Failed to save temporary file.", e);
            Toast toast = Toast.makeText(context, "Failed to get image.", Toast.LENGTH_SHORT);
            toast.show();
            finish();
            return;
        }
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mImageUri = Uri.fromFile(photo);
        takePictureIntent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }

    private File createTemporaryFile(String part, String ext) throws IOException, IllegalArgumentException {
        File tempDir = context.getExternalCacheDir();
        return File.createTempFile(part, ext, tempDir);
    }

    public void grabImage(ImageView imageView) {
        this.getContentResolver().notifyChange(mImageUri, null);
        ContentResolver cr = this.getContentResolver();
        Bitmap bitmap;
        try {
            File f = new File(mImageUri.getPath());
            ExifInterface exif = new ExifInterface(f.getPath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int angle = 0;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                angle = 90;
            }
            else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                angle = 180;
            }
            else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                angle = 270;
            }

            Matrix mat = new Matrix();
            mat.postRotate(angle);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            Bitmap bmp1 = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
            Bitmap bmp = Bitmap.createBitmap(bmp1, 0, 0, bmp1.getWidth(), bmp1.getHeight(), mat, true);
            //bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, mImageUri);
            imageBitmap = scalePic(bmp);
            beaconImage.setImageBitmap(imageBitmap);
            imageBitmap = bmp;
        }
        catch (Exception e) {
            Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Failed to load", e);
        }
    }

    private Bitmap scalePic(Bitmap source) {
        if (source.getWidth() >= 2048 || source.getHeight() >= 2048){
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int targetWidth = metrics.widthPixels;
            int targetHeight = metrics.heightPixels;
            source = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, true);
        }
        return source;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                this.grabImage(beaconImage);
                pictureTaken = true;
            } else if (resultCode == RESULT_CANCELED) {
                finish();
                return;
            } else {
                Log.d(TAG, "Could not retrieve image.");
                Toast.makeText(context, "Could not retrieve image.", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode != ACCESS_PERMISSIONS)
            return;

        if (grantResults.length == 0)
            return;

        for (int result : grantResults) {
           if (result != PackageManager.PERMISSION_GRANTED) {
               Log.d(TAG, "Was not granted permission to take picture.");
               Toast.makeText(context, "Insufficient permissions to take picture.", Toast.LENGTH_SHORT).show();
               return;
           }
        }

        try {
            takePicture();
        } catch (Exception e) {
            Log.e(TAG, "Failed to take picture.", e);
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void onSubmitBeacon(View v) {
        if (pictureTaken) {
            JsonMsg.PostBeaconRequest newBeacon = new JsonMsg.PostBeaconRequest(userId, descriptionBox.getText().toString(), (float) mLatitude, (float) mLongitude);
            new SubmitBeacon().execute(newBeacon);
        }
        else {
            Toast.makeText(context, "No picture taken",Toast.LENGTH_SHORT).show();
        }

    }

    private class SubmitBeacon extends AsyncTask <JsonMsg.PostBeaconRequest, Void, RestException> {
        @Override
        protected RestException doInBackground (JsonMsg.PostBeaconRequest... params){
            try {
                newBeaconId = client.postBeacon(params[0], imageBitmap);
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
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            else {
                Intent launchThread = new Intent(context, ThreadView.class);
                launchThread.putExtra("beaconID", newBeaconId);
                startActivity(launchThread);
                finish();
            }
        }
    }

}
package org.opus.beacon;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

public class AuthenticationActivity extends FragmentActivity implements
    GoogleApiClient.OnConnectionFailedListener,
    View.OnClickListener {

    private static final int RC_GET_TOKEN = 9002;
    private static final String TAG = "AuthenticationActivity";
    private static GoogleApiClient mGoogleApiClient;
    private SharedPreferences mPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        setContentView(R.layout.authentication_view);
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.SERVER_CLIENT_ID)
            .requestEmail()
            .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
            .enableAutoManage(this, this)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build();

        if(authenticated()) {
            Log.d(TAG, "User already authenticated.");
            launchApp();
        }
    }

    private boolean authenticated() {
       return mPreferences.getBoolean(getString(R.string.preference_authenticated), false);
    }

    private void setAuthenticated() {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(getString(R.string.preference_authenticated), true);
        editor.commit();
    }

    private void getIdToken() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_GET_TOKEN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_GET_TOKEN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Log.d(TAG, "onActivityResult:GET_TOKEN:success:" + result.getStatus().isSuccess()); 
            if(result.isSuccess()) {
                GoogleSignInAccount acct = result.getSignInAccount();
                String idToken = acct.getIdToken();
                Log.d(TAG, "idToken: " + idToken);
                RestClient client = new RestClient();
                int id = client.createAccount("testaccount", "SECRETZ", idToken);
                Log.d(TAG, "Received user ID " + id);
                setAuthenticated();
                launchApp();
            }
        }
    }

    private void launchApp() {
        Intent launchMain = new Intent(this, LaunchActivity.class);
        startActivity(launchMain);
        this.finish();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "Clicked.");
        if(v.getId() == R.id.sign_in_button) {
            getIdToken();
        }
    }
}

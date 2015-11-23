package org.opus.beacon;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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

    private EditText mTextEdit;

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

        mTextEdit = (EditText)findViewById(R.id.username);

        if(authenticated()) {
            Log.d(TAG, "User already authenticated.");
            launchApp();
        }
    }

    private boolean authenticated() {
       return mPreferences.getBoolean(getString(R.string.preference_authenticated), false);
    }

    private void setAuthenticated(String username, int id, String secret) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(getString(R.string.preference_authenticated), true);
        editor.putString(getString(R.string.preference_username), username);
        editor.putInt(getString(R.string.preference_user_id), id);
        editor.putString(getString(R.string.preference_secret), secret);
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
                try {
                    GoogleSignInAccount acct = result.getSignInAccount();
                    String idToken = acct.getIdToken();
                    BeaconRestClient client = new BeaconRestClient();
                    JsonMsg.CreateAccountResponse resp = client.createAccount(mTextEdit.getText().toString(), idToken);
                    setAuthenticated(mTextEdit.getText().toString(), resp.getId(), resp.getSecret());
                    launchApp();
                } catch (RestException e) {
                   if (e.getCode() == RestException.UsernameExistsError) {
                      Toast.makeText(getApplicationContext(), "Username already exists.", Toast.LENGTH_LONG).show();
                   } else {
                       Toast.makeText(getApplicationContext(), "Sign-In failed.", Toast.LENGTH_LONG).show();
                   }
                } catch (Exception e) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Sign-In failed.", Toast.LENGTH_SHORT);
                    toast.show();
                }
            } else {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Could not authenticate with Google.\n" +
                                result.getStatus().toString() + "\n" +
                                BuildConfig.SERVER_CLIENT_ID,
                        Toast.LENGTH_SHORT);
                toast.show();
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

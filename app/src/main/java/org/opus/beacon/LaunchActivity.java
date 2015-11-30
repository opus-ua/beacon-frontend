package org.opus.beacon;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class LaunchActivity extends Activity {

    /*
     * The payload that is passed as Intent data to NextActivity.
     */
    public final static String STRING_PAYLOAD = "Started from LaunchActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button launchNextButton = (Button) findViewById(R.id.launch_next_activity_button);

    }

    public void onLaunchButtonClick(View view) {
        Intent launchThreadView = new Intent(this, ThreadView.class);
        launchThreadView.putExtra("beaconID", 1);
        startActivity(launchThreadView);
    }

    public void onBeaconSubmissionView (View view) {
        Intent launchBeaconSubmissionView = new Intent(this, BeaconSubmissionView.class);
        startActivity(launchBeaconSubmissionView);


    }
}


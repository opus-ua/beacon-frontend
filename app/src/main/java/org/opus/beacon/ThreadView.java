package org.opus.beacon;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


public class ThreadView extends Activity {

    private Thread activeThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thread_view);
        Intent activityThatCalled = getIntent();
        double beaconID = activityThatCalled.getExtras().getDouble("beaconID");

        activeThread = getThreadFromServer(beaconID);
    }
}

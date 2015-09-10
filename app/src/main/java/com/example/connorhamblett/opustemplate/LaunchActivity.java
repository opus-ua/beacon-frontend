package com.example.connorhamblett.opustemplate;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.example.connorhamblett.opustemplate.NextActivity;
import com.example.connorhamblett.opustemplate.R;

public class LaunchActivity extends Activity {

    /*
     * The payload that is passed as Intent data to NextActivity.
     */
    public final static String STRING_PAYLOAD = "Started from LaunchActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button launchNextButton = (Button) findViewById(R.id.launch_next_activity_button);
        launchNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(NextActivity.makeIntent(LaunchActivity.this, STRING_PAYLOAD));
                startActivity(NextActivity.makeIntent(LaunchActivity.this, STRING_PAYLOAD));

                finish();
            }
        });
    }
}


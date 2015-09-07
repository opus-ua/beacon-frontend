package com.example.connorhamblett.opustemplate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

/**
 * This activity is started from LaunchActivity. It reads the payload from the given bundle and
 * displays it using a TextView.
 */
public class NextActivity extends Activity {

    /**
     * Extras key for the payload.
     */
    public final static String EXTRAS_PAYLOAD_KEY
            = "com.example.android.testingfun.lesson4.EXTRAS_PAYLOAD_KEY";

    /**
     * Factory method to create a launch Intent for this activity.
     *
     * @param context the context that intent should be bound to
     * @param payload the payload data that should be added for this intent
     * @return a configured intent to launch this activity with a String payload.
     */
    public static Intent makeIntent(Context context, String payload) {
        return new Intent(context, NextActivity.class).putExtra(EXTRAS_PAYLOAD_KEY, payload);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);

        final String stringPayload = getIntent().getStringExtra(EXTRAS_PAYLOAD_KEY);

        if (stringPayload != null) {
            ((TextView) findViewById(R.id.next_activity_info_text_view)).setText(stringPayload);
        }

    }
}

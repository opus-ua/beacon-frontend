package com.example.connorhamblett.opustemplate;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.Shadows;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import android.app.Activity;
import android.content.Intent;
import android.widget.Button;

import static org.junit.Assert.*;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class ApplicationTest {

    @Test
    public void testApplication() throws Exception {
        Activity mainActivity = Robolectric.setupActivity(LaunchActivity.class);
        Button button = (Button) mainActivity.findViewById(R.id.launch_next_activity_button);

        assertNotNull(mainActivity);
        assertNotNull(button);
    }

    @Test
    public void testLaunchNextActivity() {
        Activity mainActivity = Robolectric.setupActivity(LaunchActivity.class);
        Button button = (Button) mainActivity.findViewById(R.id.launch_next_activity_button);

        String expectedButtonText = mainActivity.getString(R.string.label_launch_next); 
        assertEquals(expectedButtonText, button.getText().toString());
    }

    @Test
    public void testLaunchedWithIntent() throws Exception {
        Activity mainActivity = Robolectric.setupActivity(LaunchActivity.class);
        Button button = (Button) mainActivity.findViewById(R.id.launch_next_activity_button);

        ShadowActivity shadowActivity = Shadows.shadowOf(mainActivity);
        button.performClick();
        
        Intent startedIntent = shadowActivity.getNextStartedActivity();        
        assertNotNull(startedIntent);
        assertTrue(shadowActivity.isFinishing());

        String payload = startedIntent.getStringExtra(NextActivity.EXTRAS_PAYLOAD_KEY);
        assertEquals(LaunchActivity.STRING_PAYLOAD, payload);
    }
}

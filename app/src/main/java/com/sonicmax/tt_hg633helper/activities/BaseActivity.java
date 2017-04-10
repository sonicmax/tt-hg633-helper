package com.sonicmax.tt_hg633helper.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.sonicmax.tt_hg633helper.R;
import com.sonicmax.tt_hg633helper.utilities.SharedPreferenceManager;

import java.util.Date;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkReferenceTimestamp();
    }

    @Override
    public void onBackPressed() {
        // Going "backwards" in app slides screen from left to right.
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    /**
     * Checks for existence of reference_timestamp in SharedPreferences and sets value if required.
     */
    private void checkReferenceTimestamp() {

        // Required for MPAndroidChart library to work correctly with UNIX timestamps - we use the
        // result of (currentTime - referenceTime) as the x axis value, and account for this
        // in TimestampAxisValueFormatter when formatting the date string to display.

        if (SharedPreferenceManager.getLong(this, "reference_timestamp") == -1) {
            SharedPreferenceManager.putLong(this, "reference_timestamp", new Date().getTime());
        }
    }
}

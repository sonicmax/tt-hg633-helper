package com.sonicmax.tt_hg633helper.charting;

import android.content.Context;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.sonicmax.tt_hg633helper.utilities.SharedPreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Adds reference_timestamp back to stored value in Entry, then converts to human-readable
 * format to display in X axis of charts.
 */

public class TimestampAxisValueFormatter implements IAxisValueFormatter {
    private final SimpleDateFormat mDateFormat;
    private final long mReferenceTimestamp;

    public TimestampAxisValueFormatter(Context context, long timestamp) {
        mDateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm", Locale.getDefault());
        mReferenceTimestamp = SharedPreferenceManager.getLong(context, "reference_timestamp");
    }

    @Override
    public String getFormattedValue(float value, AxisBase axisBase) {
        long fullTimestamp = ((long) value) + mReferenceTimestamp;
        return mDateFormat.format(new Date(fullTimestamp));
    }
}

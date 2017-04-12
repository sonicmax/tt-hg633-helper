package com.sonicmax.tt_hg633helper.charting;

import android.content.Context;
import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.sonicmax.tt_hg633helper.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Helps to display the response from dslinfo endpoint in a chart by keeping track of data sets
 * and providing various methods to add/get/remove data
 */

public class DslInfoChartHelper {
    // API response keys
    private final String UP_RATE = "UpCurrRate";
    private final String DOWN_RATE = "DownCurrRate";
    private final String UP_NOISE = "ImpulsoNoiseProUs";
    private final String DOWN_NOISE = "ImpulsoNoiseProDs";
    private final String UP_INTERLEAVE = "InterleaveDelayUs";
    private final String DOWN_INTERLEAVE = "InterleaveDelayDs";
    private final String UP_ATTENUATION = "UpAttenuation";
    private final String DOWN_ATTENUATION = "DownAttenuation";
    private final String UP_POWER = "UpPower";
    private final String DOWN_POWER = "DownPower";

    // Strings for LineChart
    private final String UP_RATE_TEXT = "Upstream line rate (kbit/s)";
    private final String DOWN_RATE_TEXT = "Downstream line rate (kbit/s)";
    private final String UP_NOISE_TEXT = "Upstream noise safety coefficient (dB)";
    private final String DOWN_NOISE_TEXT = "Downstream noise safety coefficient (dB)";
    private final String UP_INTERLEAVE_TEXT = "Upstream interleave depth";
    private final String DOWN_INTERLEAVE_TEXT = "Downstream interleave depth";
    private final String UP_ATTENUATION_TEXT = "Upstream line attenuation (dB)";
    private final String DOWN_ATTENUATION_TEXT = "Downstream line attenuation (dB)";
    private final String UP_POWER_TEXT = "Upstream output power (dBm)";
    private final String DOWN_POWER_TEXT = "Downstream output power (dBm)";

    private final int[] LINECHART_COLOURS = {
            Color.rgb(193, 37, 82), Color.rgb(255, 102, 0), Color.rgb(245, 199, 0), Color.rgb(106, 150, 31),
            Color.rgb(179, 100, 53), Color.rgb(217, 80, 138), Color.rgb(254, 149, 7),
            Color.rgb(254, 247, 120), Color.rgb(106, 167, 134), Color.rgb(53, 194, 209)
    };

    private final List<Entry> mUpRate;
    private final List<Entry> mDownRate;
    private final List<Entry> mUpNoise;
    private final List<Entry> mDownNoise;
    private final List<Entry> mUpInterleave;
    private final List<Entry> mDownInterleave;
    private final List<Entry> mUpAttenuation;
    private final List<Entry> mDownAttenuation;
    private final List<Entry> mUpPower;
    private final List<Entry> mDownPower;

    private final long mReferenceTimestamp;

    // Default settings for chart display.
    private boolean mDisplayNoise = true;
    private boolean mDisplayInterleave = false;
    private boolean mDisplayAttenuation = false;
    private boolean mDisplayPower = false;

    public DslInfoChartHelper(long time) {
        mUpRate = new ArrayList<>();
        mDownRate = new ArrayList<>();
        mUpNoise = new ArrayList<>();
        mDownNoise = new ArrayList<>();
        mUpInterleave = new ArrayList<>();
        mDownInterleave = new ArrayList<>();
        mUpAttenuation = new ArrayList<>();
        mDownAttenuation = new ArrayList<>();
        mUpPower = new ArrayList<>();
        mDownPower = new ArrayList<>();

        mReferenceTimestamp = time;
    }

    public void toggleDataSet(int id) {
        switch(id) {
            case R.id.checkbox_noise:
                mDisplayNoise = !mDisplayNoise;
                break;

            case R.id.checkbox_interleave:
                mDisplayInterleave = !mDisplayInterleave;
                break;

            case R.id.checkbox_attenuation:
                mDisplayAttenuation = !mDisplayAttenuation;
                break;

            case R.id.checkbox_power:
                mDisplayPower = !mDisplayPower;
                break;

            default:
                break;
        }
    }

    public void prepareUi(Context context, LineChart chartView) {
        chartView.getDescription().setEnabled(false);
        chartView.getAxisLeft().setEnabled(false);
        chartView.getXAxis().setValueFormatter(new TimestampAxisValueFormatter(context, mReferenceTimestamp));
        chartView.getXAxis().setLabelCount(2, true);
        chartView.getXAxis().setAvoidFirstLastClipping(true);
        chartView.getLegend().setWordWrapEnabled(true);
    }

    public List<Entry> getEntriesFromResponse(JSONObject data, long time) throws JSONException {
        List<Entry> entries = new ArrayList<>();

        mUpRate.add(new Entry(time, data.getInt(UP_RATE)));
        mDownRate.add(new Entry(time, data.getInt(DOWN_RATE)));
        mUpNoise.add(new Entry(time, data.getInt(UP_NOISE)));
        mDownNoise.add(new Entry(time, data.getInt(DOWN_NOISE)));
        mUpInterleave.add(new Entry(time, data.getInt(UP_INTERLEAVE)));
        mDownInterleave.add(new Entry(time, data.getInt(DOWN_INTERLEAVE)));
        mUpAttenuation.add(new Entry(time, data.getInt(UP_ATTENUATION)));
        mDownAttenuation.add(new Entry(time, data.getInt(DOWN_ATTENUATION)));
        mUpPower.add(new Entry(time, data.getInt(UP_POWER)));
        mDownPower.add(new Entry(time, data.getInt(DOWN_POWER)));

        return entries;
    }

    public List<ILineDataSet> getLineDataSets() {
        List<ILineDataSet> datasets = new ArrayList<>();

        /*datasets.add(new LineDataSet(mUpRate, UP_RATE_TEXT));
        datasets.add(new LineDataSet(mDownRate, DOWN_RATE_TEXT));*/

        if (mDisplayNoise) {
            datasets.add(new LineDataSet(mUpNoise, UP_NOISE_TEXT));
            datasets.add(new LineDataSet(mDownNoise, DOWN_NOISE_TEXT));
        }
        if (mDisplayInterleave) {
            datasets.add(new LineDataSet(mUpInterleave, UP_INTERLEAVE_TEXT));
            datasets.add(new LineDataSet(mDownInterleave, DOWN_INTERLEAVE_TEXT));
        }
        if (mDisplayAttenuation) {
            datasets.add(new LineDataSet(mUpAttenuation, UP_ATTENUATION_TEXT));
            datasets.add(new LineDataSet(mDownAttenuation, DOWN_ATTENUATION_TEXT));
        }
        if (mDisplayPower) {
            datasets.add(new LineDataSet(mUpPower, UP_POWER_TEXT));
            datasets.add(new LineDataSet(mDownPower, DOWN_POWER_TEXT));
        }

        int length = datasets.size();

        for (int i = 0; i < length; i++) {
            LineDataSet dataset = (LineDataSet) datasets.get(i);
            dataset.setColor(LINECHART_COLOURS[i]);
            dataset.setDrawValues(false);
            dataset.setDrawCircles(false);
        }

        return datasets;
    }

}

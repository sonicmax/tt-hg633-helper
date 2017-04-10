package com.sonicmax.tt_hg633helper.charting;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Helps to display the response from dslinfo endpoint in a chart by keeping track of data sets
 * and providing various methods to add/get/remove data
 */

public class DslInfoChartHelper {
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

    public DslInfoChartHelper() {
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

    }

    public List<Entry> getEntriesFromResponse(JSONObject data, int time) throws JSONException {
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

        datasets.add(new LineDataSet(mUpNoise, "Upstream output power"));
        datasets.add(new LineDataSet(mDownNoise, "Downstream output power"));

        return datasets;
    }

}

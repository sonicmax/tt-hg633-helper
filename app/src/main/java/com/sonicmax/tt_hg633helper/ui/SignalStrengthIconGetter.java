package com.sonicmax.tt_hg633helper.ui;

import android.net.wifi.WifiManager;

import com.sonicmax.tt_hg633helper.R;

import java.util.HashMap;
import java.util.Map;

public class SignalStrengthIconGetter {
    private static final Map<String, Integer> drawableMap;

    static {
        drawableMap = new HashMap<>();
        drawableMap.put("none", R.drawable.connection_off_100);
        drawableMap.put("low", R.drawable.connection_low_100);
        drawableMap.put("medium", R.drawable.connection_med_100);
        drawableMap.put("high", R.drawable.connection_high_100);
    }

    public static int checkSignal(int rssi) {
        int level = WifiManager.calculateSignalLevel(rssi, 4);

        switch (level) {
            case 0:
                return drawableMap.get("none");

            case 1:
                return drawableMap.get("low");

            case 2:
                return drawableMap.get("medium");

            case 3:
                return drawableMap.get("high");

            default:
                return drawableMap.get("none");
        }
    }

}

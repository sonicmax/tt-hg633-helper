package com.sonicmax.tt_hg633helper.utilities;

import java.util.concurrent.TimeUnit;

public class UptimeFormatter {

    /**
     * Generates human-readable uptime from value in seconds.
     *
     * @param uptime
     * @return Formatted string
     */

    public static String getString(int uptime) {
        String string = "";

        int d = (int) TimeUnit.SECONDS.toDays(uptime);
        long h = TimeUnit.SECONDS.toHours(uptime) - (d * 24);
        long m = TimeUnit.SECONDS.toMinutes(uptime) - (TimeUnit.SECONDS.toHours(uptime) * 60);

        if (d == 0 && h == 0 && m == 0) {
            return "Less than 1 minute";
        }

        else {
            if (d > 0) {
                if (d > 1) {
                    string += d + " Days";
                } else {
                    string += d + " Day";
                }
            }

            if (d > 0 && h > 0) {
                string += ", ";
            }

            if (h > 0) {
                if (h > 1) {
                    string += h + " Hours";
                } else {
                    string += h + " Hour";
                }
            }

            if (h > 0 && m > 0) {
                string += ", ";
            }

            if (m > 0) {
                if (m > 1) {
                    string += m + " Minutes";
                } else {
                    string += m + " Minute";
                }
            }

            return string;
        }
    }
}

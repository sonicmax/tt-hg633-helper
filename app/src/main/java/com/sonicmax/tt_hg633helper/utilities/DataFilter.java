package com.sonicmax.tt_hg633helper.utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DataFilter {
    private static final String START_STRING = "while(1); /*";
    private static final String END_STRING = "*/";

    public static JSONObject getDataAsJson(String data) throws JSONException {
        return new JSONObject(data);
    }

    public static JSONArray getDataAsJsonArray(String data) throws JSONException {
        return new JSONArray(data);
    }

    /**
     * Removes JSON hijacking protection from API responses.
     * @param data API response
     * @return Filtered API response
     */

    public static String filter(String data) {
        String filteredData = data;
        int index = filteredData.indexOf(START_STRING);
        if (index == 0) {
            filteredData = filteredData.substring(START_STRING.length());
        }
        index = filteredData.lastIndexOf(END_STRING);
        if (index + 2 == filteredData.length()) {
            filteredData = filteredData.substring(0, index);
        }

        return filteredData;
    }
}

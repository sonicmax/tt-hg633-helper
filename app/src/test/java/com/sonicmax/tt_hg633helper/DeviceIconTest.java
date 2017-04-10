package com.sonicmax.tt_hg633helper;

import android.util.Log;

import com.sonicmax.tt_hg633helper.ui.DeviceIconGetter;

import org.junit.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DeviceIconTest {

    @Test
    public void hasAllIcons() throws Exception {
        for (String deviceType : DeviceIconGetter.deviceTypes) {
            System.out.println("hasAllIcons() testing for " + deviceType);
            assertNotNull(DeviceIconGetter.drawableMap.get(deviceType));
        }
    }

    @Test
    public void allIconsHaveDeviceType() throws Exception {
        List deviceTypes = Arrays.asList(DeviceIconGetter.deviceTypes);
        Iterator it = DeviceIconGetter.drawableMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            System.out.println("allIconsHaveDeviceType() testing for " + pair.getKey());
            assertTrue(deviceTypes.contains(pair.getKey()));
        }
    }
}

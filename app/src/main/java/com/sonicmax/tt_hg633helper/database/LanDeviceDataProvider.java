package com.sonicmax.tt_hg633helper.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.sonicmax.tt_hg633helper.database.AppDataContract.DeviceEntry;
import com.sonicmax.tt_hg633helper.database.AppDataContract.LanPerformanceEntry;

/**
 * Provides various methods to aid with querying/inserting to device databases
 */

public class LanDeviceDataProvider {
    private final String LOG_TAG = LanDeviceDataProvider.class.getSimpleName();

    private final Context mContext;
    private final DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    public LanDeviceDataProvider(Context context) {
        this.mContext = context;
        mDbHelper = new DatabaseHelper(mContext);
    }

    public LanDeviceDataProvider open() throws SQLException {
        try {
            mDb = mDbHelper.getReadableDatabase();
        }
        catch (SQLException e) {
            Log.e(LOG_TAG, "Error opening database:", e);
        }
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    /**
     * Queries device performance database for all records and returns Cursor over the result set.
     * Results include timestamp, MAC address, RSSI, and device rate.
     *
     * @return Cursor
     */
    public Cursor getAllPerformanceRecords() {
        String[] columns = {LanPerformanceEntry.COLUMN_TIMESTAMP,
                LanPerformanceEntry.COLUMN_PORT, LanPerformanceEntry.COLUMN_MAC_ADDRESS,
                LanPerformanceEntry.COLUMN_BYTES_SENT, LanPerformanceEntry.COLUMN_BYTES_RECEIVED};

        String orderBy = LanPerformanceEntry.COLUMN_TIMESTAMP + " ASC";

        return mDb.query(LanPerformanceEntry.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                orderBy);
    }

    /**
     * Queries device database for performance records associated with given MAC address and
     * returns Cursor over the result set. Results include timestamp, RSSI and device rate.
     *
     * @param port LAN port to check (LAN1, LAN2, LAN3, or LAN4)
     * @return Cursor
     */
    public Cursor getAllPerformanceRecordsForPort(String port) {
        String[] columns = {LanPerformanceEntry.COLUMN_TIMESTAMP, LanPerformanceEntry.COLUMN_MAC_ADDRESS,
                LanPerformanceEntry.COLUMN_BYTES_SENT, LanPerformanceEntry.COLUMN_BYTES_RECEIVED};
        String where = LanPerformanceEntry.COLUMN_PORT + " = ?";
        String[] whereClause = {port};
        String orderBy = LanPerformanceEntry.COLUMN_TIMESTAMP + " ASC";

        return mDb.query(LanPerformanceEntry.TABLE_NAME,
                columns,
                where,
                whereClause,
                null,
                null,
                orderBy);
    }

    public void insertNewPerformanceRecord(long timestamp, String port, String mac, int sent, int received) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(LanPerformanceEntry.COLUMN_TIMESTAMP, timestamp);
        values.put(LanPerformanceEntry.COLUMN_PORT, port);
        values.put(LanPerformanceEntry.COLUMN_MAC_ADDRESS, mac);
        values.put(LanPerformanceEntry.COLUMN_BYTES_SENT, sent);
        values.put(LanPerformanceEntry.COLUMN_BYTES_RECEIVED, received);

        db.insert(LanPerformanceEntry.TABLE_NAME, null, values);
        db.close();
    }
}

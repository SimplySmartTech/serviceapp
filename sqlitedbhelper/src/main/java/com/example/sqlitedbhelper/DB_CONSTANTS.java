package com.example.sqlitedbhelper;

import android.provider.BaseColumns;

/**
 * Created by shailendrapsp on 19/12/16.
 */

public final class DB_CONSTANTS {

    private DB_CONSTANTS() {
    }

    public static class MatrixEntry implements BaseColumns {
        public static final String TABLE_NAME = "matrix_data";
        public static final String TABLE_PRIMARY_KEY = "primary_key";
        public static final String COLUMN_UNIT_ID = "unit_id";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_UTILITY_ID = "utility_id";
        public static final String COLUMN_ICON = "icon";
        public static final String COLUMN_ORDER = "order";
        public static final String COLUMN_MANDATORY = "mandatory";
        public static final String COLUMN_SENSORS = "sensors";
    }

    public static class SensorEntry implements BaseColumns {
        public static final String TABLE_NAME = "sensor_data";
        public static final String TABLE_PRIMARY_KEY = "primary_key";
        public static final String COLUMN_SITE_NAME = "site_name";
        public static final String COLUMN_METRIC = "metric";
        public static final String COLUMN_SENSOR_NAME = "sensor_name";
        public static final String COLUMN_MANDATORY = "mandatory";
        public static final String COLUMN_UNIT = "unit";
        public static final String COLUMN_DATA_TYPE = "data_type";
        public static final String COLUMN_PHOTOGRAPHIC_EVIDENCE = "photographic_evidence";
        public static final String COLUMN_NO_OF_TIMES = "no_of_times";
        public static final String COLUMN_DURATION_UNIT = "duration_unit";
        public static final String COLUMN_DURATION = "duration_type";
        public static final String COLUMN_UTILITY_IDENTIFIER = "utility_identifier";
        public static final String COLUMN_TOOLTIP = "tooltip";
    }

    public static class ReadingsEntry implements BaseColumns{
        public static final String TABLE_NAME = "readings_data";
        public static final String TABLE_PRIMARY_KEY = "primary_key";
        public static final String COLUMN_UTILITY_ID = "utility_id";
        public static final String COLUMN_SENSOR_NAME = "sensor_name";
        public static final String COLUMN_VALUE="value";
        public static final String COLUMN_PHOTOGRAPHIC_EVIDENCE_URL = "photographic_evidence_url";
        public static final String COLUMN_LOCAL_PHOTO_URL="local_photo_url";
        public static final String COLUMN_DATE="date";
        public static final String COLUMN_UNIT = "unit";
        public static final String COLUMN_TARE_WEIGHT= "tare_weight";
        public static final String COLUMN_TIMESTAMP = "timestamp";
        public static final String COLUMN_UNIT_ID="unit_id";
        public static final String COLUMN_UPLOADED_IMAGE="uploadedImage";
    }
}

/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.weather.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import com.example.android.weather.R;

public class WeatherPreferences {

    /*
     * Human readable location string, provided by the API.  Because for styling,
     * "Mountain View" is more recognizable than 94043.
     */
    public static final String PREF_CITY_NAME = "city_name";

    /*
     * Để xác định vị trí cụ thể trên bản đồ khi ta khởi chạy map intent,
     * ta lưu giữ vĩ độ và kinh độ.
     */
    public static final String PREF_COORD_LAT = "coord_lat";
    public static final String PREF_COORD_LONG = "coord_long";

    /*
     * Trước khi thực hiện các phương thức để trả về vị trí thật,
     * chúng tôi cung cấp một số giá trị mặc định để làm việc.
     */
    private static final String DEFAULT_WEATHER_LOCATION = "94043,USA";
    private static final double[] DEFAULT_WEATHER_COORDINATES = {37.4284, 122.0724};

    private static final String DEFAULT_MAP_LOCATION =
            "1600 Amphitheatre Parkway, Mountain View, CA 94043";

    /**
     * Phương thức trợ giúp để xử lý cài đặt chi tiết vị trí trong Preferences
     * (Tên Thành phố, Latitude, Kinh độ)
     */
    static public void setLocationDetails(Context c, String cityName, double lat, double lon) {
        /** Sẽ thêm trong các bài học sau **/
    }

    /**
     * Phương thức trợ giúp để điều chỉnh cài đặt vị trí mới theo preference.
     * Khi điều này xảy ra cơ sở dữ liệu có thể cần phải được xóa.
     */
    static public void setLocation(Context c, String locationSetting, double lat, double lon) {
        /** Sẽ thêm trong các bài học sau **/
    }

    /**
     * Đặt lại tọa độ vị trí lưu trữ.
     */
    static public void resetLocationCoordinates(Context c) {
        /** Sẽ thêm trong các bài học sau **/
    }

    /**
     * Trả về vị trí hiện được đặt trong Preferences. Vị trí mặc định này sẽ trả về
     * là "94043, Hoa Kỳ", đó là Mountain View, California.
     * Mountain View là ngôi nhà của trụ sở chính của Googleplex!
     */
    public static String getPreferredWeatherLocation(Context context) {
        // Hoàn thành (1) Trả về vị trí
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        String keyForLocation = context.getString(R.string.pref_location_key);
        String defaultLocation = context.getString(R.string.pref_location_default);
        return prefs.getString(keyForLocation, defaultLocation);
    }

    /**
     * Trả về true nếu người dùng đã chọn hiển thị nhiệt độ.
     */
    public static boolean isMetric(Context context) {
        // Hoàn thành (2) Trả về true nếu preference đơn vị (unit) của người dùng là metric, nếu khô thì trả về false
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        String keyForUnits = context.getString(R.string.pref_units_key);
        String defaultUnits = context.getString(R.string.pref_units_metric);
        String preferredUnits = prefs.getString(keyForUnits, defaultUnits);
        String metric = context.getString(R.string.pref_units_metric);
        boolean userPrefersMetric;
        if (metric.equals(preferredUnits)) {
            userPrefersMetric = true;
        } else {
            userPrefersMetric = false;
        }
        return userPrefersMetric;
    }

    /**
     * Trả về tọa độ vị trí được liên kết với vị trí. Lưu ý rằng các tọa độ này
     * có thể không được đặt, kết quả là (0,0) sẽ được trả về.
     * (Nhân tiện, 0,0 là ở giữa đại dương ngoài khơi bờ biển phía tây của châu Phi)
     */
    public static double[] getLocationCoordinates(Context context) {
        return getDefaultWeatherCoordinates();
    }

    /**
     * Trả về true nếu giá trị kinh độ và vĩ độ có sẵn. Kinh độ vĩ độ sẽ
     * không có sẵn cho đến bài học API PlacePicker.
     */
    public static boolean isLocationLatLonAvailable(Context context) {
        /** Sẽ thêm trong các bài học sau **/
        return false;
    }

    private static String getDefaultWeatherLocation() {
        /** Sẽ thêm trong các bài học sau **/
        return DEFAULT_WEATHER_LOCATION;
    }

    public static double[] getDefaultWeatherCoordinates() {
        /** Sẽ thêm trong các bài học sau **/
        return DEFAULT_WEATHER_COORDINATES;
    }
}
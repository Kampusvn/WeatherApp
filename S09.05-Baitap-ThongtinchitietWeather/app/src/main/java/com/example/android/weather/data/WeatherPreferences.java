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
import android.preference.PreferenceManager;

import com.example.android.weather.R;

public final class WeatherPreferences {

    /*
     * Để xác định vị trí cụ thể trên bản đồ khi ta khởi chạy map intent,
     * ta lưu giữ vĩ độ và kinh độ.
     */
    public static final String PREF_COORD_LAT = "coord_lat";
    public static final String PREF_COORD_LONG = "coord_long";

    /**
     * Phương thức trợ giúp để xử lý cài đặt chi tiết vị trí trong Preferences
     * (Tên Thành phố, Latitude, Kinh độ)
     */
    public static void setLocationDetails(Context context, double lat, double lon) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        editor.putLong(PREF_COORD_LAT, Double.doubleToRawLongBits(lat));
        editor.putLong(PREF_COORD_LONG, Double.doubleToRawLongBits(lon));
        editor.apply();
    }

    /**
     * Đặt lại vị trí tọa độ trong SharedPreferences.
     *
     * @param context Context dùng để lấy SharedPreferences
     */
    public static void resetLocationCoordinates(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        editor.remove(PREF_COORD_LAT);
        editor.remove(PREF_COORD_LONG);
        editor.apply();
    }

    /**
     * Trả về vị trí hiện được đặt trong Preferences. Vị trí mặc định này sẽ trả về
     * là "94043, Hoa Kỳ", đó là Mountain View, California.
     * Mountain View là ngôi nhà của trụ sở chính của Googleplex!
     */
    public static String getPreferredWeatherLocation(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        String keyForLocation = context.getString(R.string.pref_location_key);
        String defaultLocation = context.getString(R.string.pref_location_default);

        return sp.getString(keyForLocation, defaultLocation);
    }

    /**
     * Trả về true nếu người dùng đã chọn hiển thị nhiệt độ.
     */
    public static boolean isMetric(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        String keyForUnits = context.getString(R.string.pref_units_key);
        String defaultUnits = context.getString(R.string.pref_units_metric);
        String preferredUnits = sp.getString(keyForUnits, defaultUnits);
        String metric = context.getString(R.string.pref_units_metric);

        boolean userPrefersMetric = false;
        if (metric.equals(preferredUnits)) {
            userPrefersMetric = true;
        }

        return userPrefersMetric;
    }

    /**
     * Trả về tọa độ vị trí được liên kết với vị trí. Lưu ý rằng các tọa độ này
     * có thể không được đặt, kết quả là (0,0) sẽ được trả về.
     * (Nhân tiện, 0,0 là ở giữa đại dương ngoài khơi bờ biển phía tây của châu Phi)
     */
    public static double[] getLocationCoordinates(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        double[] preferredCoordinates = new double[2];

        /*
         * Đây là một hack chúng ta phải dùng đến vì bạn không thể lưu trữ double trong SharedPreferences.
         *
		 * Double.doubleToLongBits trả về một số int tương ứng với các bit của giá trị double IEEE 754 đã cho.
         *
         * Double.longBitsToDouble làm ngược lại, sẽ chuyển số long (cũng là một kiểu double)
         * thành kiểu double.
         */
        preferredCoordinates[0] = Double
                .longBitsToDouble(sp.getLong(PREF_COORD_LAT, Double.doubleToRawLongBits(0.0)));
        preferredCoordinates[1] = Double
                .longBitsToDouble(sp.getLong(PREF_COORD_LONG, Double.doubleToRawLongBits(0.0)));

        return preferredCoordinates;
    }

    /**
     * Trả về true nếu giá trị kinh độ và vĩ độ có sẵn. Kinh độ vĩ độ sẽ
     * không có sẵn cho đến bài học API PlacePicker.
     */
    public static boolean isLocationLatLonAvailable(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        boolean spContainLatitude = sp.contains(PREF_COORD_LAT);
        boolean spContainLongitude = sp.contains(PREF_COORD_LONG);

        boolean spContainBothLatitudeAndLongitude = false;
        if (spContainLatitude && spContainLongitude) {
            spContainBothLatitudeAndLongitude = true;
        }

        return spContainBothLatitudeAndLongitude;
    }

    /**
     * Trả về thời điểm cuối cùng mà một thông báo được hiển thị (theo UNIX time)
     *
     * @param context Dùng để truy cập SharedPreferences
     * @return UNIX time khi thông báo cuối cùng được hiển thị
     */
    public static long getLastNotificationTimeInMillis(Context context) {
        /* Key để truy cập thời gian lần cuối cùng ứng dụng hiển thị thông báo */
        String lastNotificationKey = context.getString(R.string.pref_last_notification);

        /* Ta dùng SharedPreferences mặc định để truy cập preferences của người dùng */
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        /*
         * Tại đây, chúng ta sẽ truy lục thời gian bằng mili giây khi thông báo cuối cùng
		 * được hiển thị. Nếu SharedPreferences không có giá trị cho LastNotificationKey, ta sẽ
		 * trả về 0. Lý do việc trả về 0 là bởi vì chúng ta so sánh giá trị được trả về từ
		 * phương thức này với thời gian hệ thống hiện tại. Nếu chênh lệch giữa thời gian
		 * thông báo cuối cùng và thời gian hiện tại lớn hơn một ngày, ta sẽ hiển thị lại thông báo.
		 * Khi so sánh hai giá trị này, ta trừ đi thời gian thông báo cuối cùng từ thời điểm
		 * hiện tại của hệ thống. Nếu thời gian của thông báo cuối cùng là 0, chênh lệch sẽ
		 * luôn luôn lớn hơn số mili giây trong một ngày và ta sẽ hiển thị một thông báo khác.
         */
        long lastNotificationTime = sp.getLong(lastNotificationKey, 0);

        return lastNotificationTime;
    }

    /**
     * Trả về thời gian trôi qua theo mili giây kể từ khi thông báo mới nhất được hiển thị.
     * Hàm này như là một phần của việc kiểm tra xem có nên hiển thị thông báo khác khi
     * thời tiết được cập nhật hay không.
     *
     * @param context Dùng để truy cập SharedPreferences cũng như các phương thức tiện ích khác
     * @return thời gian tính bằng mili giây
     */
    public static long getEllapsedTimeSinceLastNotification(Context context) {
        long lastNotificationTimeMillis =
                WeatherPreferences.getLastNotificationTimeInMillis(context);
        long timeSinceLastNotification = System.currentTimeMillis() - lastNotificationTimeMillis;
        return timeSinceLastNotification;
    }

    /**
     * Lưu thời gian mà một thông báo được hiển thị. Điều này sẽ được sử dụng để tính thời gian
     * đã trôi qua kể từ khi một thông báo được hiển thị.
     *
     * @param context Dùng để truy cập SharedPreferences
     * @param timeOfNotification Thời gian của thông báo cuối cùng (UNIX time)
     */
    public static void saveLastNotificationTime(Context context, long timeOfNotification) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        String lastNotificationKey = context.getString(R.string.pref_last_notification);
        editor.putLong(lastNotificationKey, timeOfNotification);
        editor.apply();
    }
}
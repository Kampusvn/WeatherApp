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
package com.example.android.weather.utilities;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Các tiện ích này sẽ giao tiếp với server
 */
public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String DYNAMIC_WEATHER_URL =
            "https://andfun-weather.udacity.com/weather";

    private static final String STATIC_WEATHER_URL =
            "https://andfun-weather.udacity.com/staticweather";

    private static final String FORECAST_BASE_URL = STATIC_WEATHER_URL;

    /*
     * LƯU Ý: Các giá trị này ảnh hưởng đến phản hồi từ OpenWeatherMap, không phải từ máy chủ
     * giả. Chúng ở đây để cho bạn thấy cách xây dựng một URL nếu sử dụng một API thật.
     * Nếu bạn muốn kết nối ứng dụng của bạn với API của OpenWeatherMap thì hãy làm.
     * Tuy nhiên, trong khóa học này chúng ta sẽ không học cách để làm điều đó.
     */

    /* Định dạng ta muốn API trả về */
    private static final String format = "json";
    /* Đơn vị ta muốn API trả về */
    private static final String units = "metric";
    /* Số ngày ta muốn API trả về */
    private static final int numDays = 14;

    final static String QUERY_PARAM = "q";
    final static String LAT_PARAM = "lat";
    final static String LON_PARAM = "lon";
    final static String FORMAT_PARAM = "mode";
    final static String UNITS_PARAM = "units";
    final static String DAYS_PARAM = "cnt";

    /**
     * Xây dựng URL được sử dụng để giao tiếp với máy chủ thời tiết qua vị trí.
     * Vị trí này dựa trên khả năng truy vấn của nhà cung cấp thời tiết mà ta đang sử dụng.
     */
    public static URL buildUrl(String locationQuery) {
        Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAM, locationQuery)
                .appendQueryParameter(FORMAT_PARAM, format)
                .appendQueryParameter(UNITS_PARAM, units)
                .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    /**
     * Xây dựng URL được sử dụng để giao tiếp với máy chủ bằng cách sử dụng tọa độ của vị trí.
     */
    public static URL buildUrl(Double lat, Double lon) {
        /** Chỗ này sẽ được thêm trong các bài học sau **/
        return null;
    }

    /**
     * Phương thức này trả về toàn bộ kết quả từ phản hồi HTTP.
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
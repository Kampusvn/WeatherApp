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

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

/**
 * Utility functions to handle OpenWeatherMap JSON data.
 */
public final class OpenWeatherJsonUtils {

    /* Thông tin vị trí */
    private static final String OWM_CITY = "city";
    private static final String OWM_CITY_NAME = "name";
    private static final String OWM_COORD = "coord";

    /* Thông tin tọa độ */
    private static final String OWM_LATITUDE = "lat";
    private static final String OWM_LONGITUDE = "lon";

    /* Thông tin thời tiết. Thông tin dự báo mỗi ngày là một phần của mảng "list" */
    private static final String OWM_LIST = "list";

    private static final String OWM_PRESSURE = "pressure";
    private static final String OWM_HUMIDITY = "humidity";
    private static final String OWM_WINDSPEED = "speed";
    private static final String OWM_WIND_DIRECTION = "deg";

    private static final String OWM_TEMPERATURE = "temp";

    private static final String OWM_MAX = "max";
    private static final String OWM_MIN = "min";

    private static final String OWM_WEATHER = "weather";
    private static final String OWM_WEATHER_ID = "id";

    private static final String OWM_MESSAGE_CODE = "cod";

    /**
     * Phương thức này phân tích cú pháp JSON từ phản hồi web và trả về một mảng mô tả
     * thời tiết trong nhiều ngày từ dự báo. Sau đó, phân tích cú pháp JSON vào dữ liệu
     * có cấu trúc bên trong hàm getFullWeatherDataFromJson, tận dụng dữ liệu đã lưu trữ
     * trong JSON. Bây giờ, ta chỉ cần chuyển đổi JSON thành chuỗi để người dùng có thể đọc.
     */
    public static String[] getSimpleWeatherStringsFromJson(Context context, String forecastJsonStr)
            throws JSONException {

        String[] parsedWeatherData;

        JSONObject forecastJson = new JSONObject(forecastJsonStr);

        /* Kiểm tra xem có lỗi hay không */
        if (forecastJson.has(OWM_MESSAGE_CODE)) {
            int errorCode = forecastJson.getInt(OWM_MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    /* Vị trí không hợp lý */
                    return null;
                default:
                    /* Server down */
                    return null;
            }
        }

        JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

        parsedWeatherData = new String[weatherArray.length()];

        long startDay = WeatherDateUtils.getNormalizedUtcDateForToday();

        for (int i = 0; i < weatherArray.length(); i++) {
            String date;
            String highAndLow;

            /* Các giá trị sẽ được thu thập */
            long dateTimeMillis;
            double high;
            double low;

            int weatherId;
            String description;

            /* Nhận đối tượng JSON đại diện cho ngày */
            JSONObject dayForecast = weatherArray.getJSONObject(i);

            /*
             * Bỏ qua tất cả các giá trị datetime được nhúng trong JSON và cho rằng
             * các giá trị được trả về theo thứ tự hàng ngày (điều này không đảm bảo là đúng).
             */
            dateTimeMillis = startDay + WeatherDateUtils.DAY_IN_MILLIS * i;
            date = WeatherDateUtils.getFriendlyDateString(context, dateTimeMillis, false);

            JSONObject weatherObject =
                    dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);

            weatherId = weatherObject.getInt(OWM_WEATHER_ID);
            description = WeatherUtils.getStringForWeatherCondition(context, weatherId);

            JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
            high = temperatureObject.getDouble(OWM_MAX);
            low = temperatureObject.getDouble(OWM_MIN);
            highAndLow = WeatherUtils.formatHighLows(context, high, low);

            parsedWeatherData[i] = date + " - " + description + " - " + highAndLow;
        }

        return parsedWeatherData;
    }
}
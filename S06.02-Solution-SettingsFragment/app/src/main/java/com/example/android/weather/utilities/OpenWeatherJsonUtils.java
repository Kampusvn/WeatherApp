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

import android.content.ContentValues;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

/**
 * Utility functions to handle OpenWeatherMap JSON data.
 */
public final class OpenWeatherJsonUtils {

    /**
     * Phương thức này phân tích cú pháp JSON từ phản hồi web và trả về một mảng mô tả
     * thời tiết trong nhiều ngày từ dự báo. Sau đó, phân tích cú pháp JSON vào dữ liệu
     * có cấu trúc bên trong hàm getFullWeatherDataFromJson, tận dụng dữ liệu đã lưu trữ
     * trong JSON. Bây giờ, ta chỉ cần chuyển đổi JSON thành chuỗi để người dùng có thể đọc.
     */
    public static String[] getSimpleWeatherStringsFromJson(Context context, String forecastJsonStr)
            throws JSONException {

        /* Thông tin thời tiết. Thông tin dự báo mỗi ngày là một phần của mảng "list" */
        final String OWM_LIST = "list";

        final String OWM_TEMPERATURE = "temp";

        /* Nhiệt độ cao và thấp nhất trong ngày */
        final String OWM_MAX = "max";
        final String OWM_MIN = "min";

        final String OWM_WEATHER = "weather";
        final String OWM_DESCRIPTION = "main";

        final String OWM_MESSAGE_CODE = "cod";

        /* Mảng String để lưu trữ chuỗi thời tiết mỗi ngày */
        String[] parsedWeatherData = null;

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

        long localDate = System.currentTimeMillis();
        long utcDate = WeatherDateUtils.getUTCDateFromLocal(localDate);
        long startDay = WeatherDateUtils.normalizeDate(utcDate);

        for (int i = 0; i < weatherArray.length(); i++) {
            String date;
            String highAndLow;

            /* Các giá trị sẽ được thu thập */
            long dateTimeMillis;
            double high;
            double low;
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
            description = weatherObject.getString(OWM_DESCRIPTION);

            JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
            high = temperatureObject.getDouble(OWM_MAX);
            low = temperatureObject.getDouble(OWM_MIN);
            highAndLow = WeatherUtils.formatHighLows(context, high, low);

            parsedWeatherData[i] = date + " - " + description + " - " + highAndLow;
        }

        return parsedWeatherData;
    }

    /**
     * Phân tích cú pháp JSON và chuyển đổi nó thành ContentValues để có thể chèn vào cơ sở dữ liệu.
     */
    public static ContentValues[] getFullWeatherDataFromJson(Context context, String forecastJsonStr) {
        /** Sẽ được thêm trong các bài học sau **/
        return null;
    }
}
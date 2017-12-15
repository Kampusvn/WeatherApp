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
package com.example.android.weather.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.text.format.DateUtils;

import com.example.android.weather.data.WeatherPreferences;
import com.example.android.weather.data.WeatherContract;
import com.example.android.weather.utilities.NetworkUtils;
import com.example.android.weather.utilities.NotificationUtils;
import com.example.android.weather.utilities.OpenWeatherJsonUtils;

import java.net.URL;

public class WeatherSyncTask {

    /**
     * Thực hiện yêu cầu mạng về thời tiết đã cập nhật, phân tích cú pháp của JSON từ yêu cầu đó và chèn thông
     * tin thời tiết mới vào ContentProvider. Sẽ thông báo cho người dùng rằng thời tiết mới đã được tải nếu
     * người dùng không được thông báo về thời tiết trong ngày cuối cùng VÀ họ không tắt thông báo trong màn
     * hình tùy chọn.
     *
     * @param context Được sử dụng để truy cập các phương thức hữu ích và ContentResolver
     */
    synchronized public static void syncWeather(Context context) {

        try {
            /*
             * Phương thức getUrl sẽ trả lại URL mà chúng ta cần để có được dự báo thời tiết cho JSON.
			 * Nó sẽ quyết định có nên tạo ra một URL dựa trên vĩ độ và kinh độ của một vị trí đơn giản
			 * dưới dạng một String hay không.
             */
            URL weatherRequestUrl = NetworkUtils.getUrl(context);

            /* Dùng URL để lấy JSON */
            String jsonWeatherResponse = NetworkUtils.getResponseFromHttpUrl(weatherRequestUrl);

            /* Phân tích JSON thành danh sách các giá trị thời tiết */
            ContentValues[] weatherValues = OpenWeatherJsonUtils
                    .getWeatherContentValuesFromJson(context, jsonWeatherResponse);

            /*
             * Trong trường hợp JSON của chúng ta chứa code lỗi, getWeatherContentValuesFromJson sẽ trả về null.
			 * Chúng ta cần phải kiểm tra các trường hợp ở đây để ngăn chặn bất kỳ NullPointerExceptions nào bị
			 * ném ra. Ta cũng không có lý do để chèn dữ liệu mới nếu không có bất kỳ dữ liệu nào để chèn.
             */
            if (weatherValues != null && weatherValues.length != 0) {
                /* Lấy trình xử lý trên ContentResolver để xóa và chèn dữ liệu */
                ContentResolver weatherContentResolver = context.getContentResolver();

                /* Xóa dữ liệu thời tiết cũ vì ta không cần giữ dữ liệu nhiều ngày */
                weatherContentResolver.delete(
                        WeatherContract.WeatherEntry.CONTENT_URI,
                        null,
                        null);

                /* Chèn dữ liệu mới vào ContentProvider */
                weatherContentResolver.bulkInsert(
                        WeatherContract.WeatherEntry.CONTENT_URI,
                        weatherValues);

                // Kiểm tra xem đã bật thông báo chưa
                boolean notificationsEnabled = WeatherPreferences.areNotificationsEnabled(context);

                long timeSinceLastNotification = WeatherPreferences
                        .getEllapsedTimeSinceLastNotification(context);

                boolean oneDayPassedSinceLastNotification = false;

                // Kiểm tra thời gian từ thông báo cuối cùng đã được 1 ngày chưa
                if (timeSinceLastNotification >= DateUtils.DAY_IN_MILLIS) {
                    oneDayPassedSinceLastNotification = true;
                }

                // Nếu 2 điều kiện trên thỏa mãn thì thông báo cho người dùng
                if (notificationsEnabled && oneDayPassedSinceLastNotification) {
                    NotificationUtils.notifyUserOfNewWeather(context);
                }

            /* Nếu code chạy đến đây, chúng ta đã thực hiện thành công quá trình đồng bộ hóa */

            }

        } catch (Exception e) {
            /* Server không hợp lệ */
            e.printStackTrace();
        }
    }
}
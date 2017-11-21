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
import android.text.format.DateUtils;

import com.example.android.weather.R;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

public final class WeatherDateUtils {

    public static final long SECOND_IN_MILLIS = 1000;
    public static final long MINUTE_IN_MILLIS = SECOND_IN_MILLIS * 60;
    public static final long HOUR_IN_MILLIS = MINUTE_IN_MILLIS * 60;
    public static final long DAY_IN_MILLIS = HOUR_IN_MILLIS * 24;

    /**
     * Phương thức này trả về số ngày kể từ 01 tháng 1 năm 1970, 12:00 Giờ giữa đêm UTC
     * theo giờ UTC đến ngày hiện tại.
     */
    public static long getDayNumber(long date) {
        TimeZone tz = TimeZone.getDefault();
        long gmtOffset = tz.getOffset(date);
        return (date + gmtOffset) / DAY_IN_MILLIS;
    }

    /**
     * Để dễ dàng truy vấn chính xác ngày, chúng tôi sẽ normalize tất cả
     * các ngày đưa vào cơ sở dữ liệu để bắt đầu trong ngày UTC
     */
    public static long normalizeDate(long date) {
        long retValNew = date / DAY_IN_MILLIS * DAY_IN_MILLIS;
        return retValNew;
    }

    /**
     * Vì tất cả các ngày tháng từ cơ sở dữ liệu là UTC, chúng ta phải chuyển đổi ngày đã cho
     * (trong múi giờ UTC) sang ngày trong múi giờ địa phương. Chức năng này thực hiện chuyển
     * đổi đó bằng cách sử dụng TimeZone offset
     */
    public static long getLocalDateFromUTC(long utcDate) {
        TimeZone tz = TimeZone.getDefault();
        long gmtOffset = tz.getOffset(utcDate);
        return utcDate - gmtOffset;
    }

    /**
     * Vì tất cả các ngày tháng từ cơ sở dữ liệu là UTC, chúng ta phải chuyển đổi ngày địa
     * phương sang ngày theo giờ UTC. Chức năng này thực hiện chuyển đổi đó bằng cách
     * sử dụng TimeZone offset.
     */
    public static long getUTCDateFromLocal(long localDate) {
        TimeZone tz = TimeZone.getDefault();
        long gmtOffset = tz.getOffset(localDate);
        return localDate + gmtOffset;
    }

    /**
     * Phương thức trợ giúp để chuyển đổi cơ sở dữ liệu đại diện của ngày thành một thứ để hiển thị
     * cho người dùng.
     */
    public static String getFriendlyDateString(Context context, long dateInMillis, boolean showFullDate) {

        long localDate = getLocalDateFromUTC(dateInMillis);
        long dayNumber = getDayNumber(localDate);
        long currentDayNumber = getDayNumber(System.currentTimeMillis());

        if (dayNumber == currentDayNumber || showFullDate) {
            /*
             * Nếu ngày chúng ta xây dựng chuỗi là ngày hôm nay, định dạng sẽ là "Today, June 24"
             */
            String dayName = getDayName(context, localDate);
            String readableDate = getReadableDateString(context, localDate);
            if (dayNumber - currentDayNumber < 2) {
                String localizedDayName = new SimpleDateFormat("EEEE").format(localDate);
                return readableDate.replace(localizedDayName, dayName);
            } else {
                return readableDate;
            }
        } else if (dayNumber < currentDayNumber + 7) {
            /* Nếu ngày ít hơn 1 tuần tới, ta trả về tên ngày. */
            return getDayName(context, localDate);
        } else {
            int flags = DateUtils.FORMAT_SHOW_DATE
                    | DateUtils.FORMAT_NO_YEAR
                    | DateUtils.FORMAT_ABBREV_ALL
                    | DateUtils.FORMAT_SHOW_WEEKDAY;

            return DateUtils.formatDateTime(context, localDate, flags);
        }
    }

    /**
     * Trả lại chuỗi ngày ở định dạng đã chỉ định, hiển thị ngày,
     * không có chữ viết tắt, hiển thị ngày trong tuần đầy đủ.
     */
    private static String getReadableDateString(Context context, long timeInMillis) {
        int flags = DateUtils.FORMAT_SHOW_DATE
                | DateUtils.FORMAT_NO_YEAR
                | DateUtils.FORMAT_SHOW_WEEKDAY;

        return DateUtils.formatDateTime(context, timeInMillis, flags);
    }

    /**
     * Cho một ngày, chỉ trả về tên ngày đó.
     *   VD "today", "tomorrow", "Wednesday".
     */
    private static String getDayName(Context context, long dateInMillis) {
        /*
         * Nếu là ngày hôm nay thì chỉ hiển thị "Today"
         */
        long dayNumber = getDayNumber(dateInMillis);
        long currentDayNumber = getDayNumber(System.currentTimeMillis());
        if (dayNumber == currentDayNumber) {
            return context.getString(R.string.today);
        } else if (dayNumber == currentDayNumber + 1) {
            return context.getString(R.string.tomorrow);
        } else {
            /*
             * Nếu không phải hôm nay thì hiển thị tên ngày
             * (e.g "Wednesday")
             */
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
            return dayFormat.format(dateInMillis);
        }
    }
}
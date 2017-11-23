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
import java.util.concurrent.TimeUnit;

public final class WeatherDateUtils {

    /* Mili giây trong một ngày */
    public static final long DAY_IN_MILLIS = TimeUnit.DAYS.toMillis(1);

    /**
     * Phương thức này trả về số ngày kể từ 01 tháng 1 năm 1970, 12:00 Giờ giữa đêm UTC
     * theo giờ UTC đến ngày hiện tại.
     */
    public static long getNormalizedUtcDateForToday() {

        long utcNowMillis = System.currentTimeMillis();
        TimeZone currentTimeZone = TimeZone.getDefault();
        long gmtOffsetMillis = currentTimeZone.getOffset(utcNowMillis);
        long timeSinceEpochLocalTimeMillis = utcNowMillis + gmtOffsetMillis;
        long daysSinceEpochLocal = TimeUnit.MILLISECONDS.toDays(timeSinceEpochLocalTimeMillis);
        long normalizedUtcMidnightMillis = TimeUnit.DAYS.toMillis(daysSinceEpochLocal);
        return normalizedUtcMidnightMillis;
    }

    /**
     * Phương thức trả về số ngày từ 01/01/1970 đến nay, tính theo thời gian UTC.
     *
     * @param utcDate Thời gian một ngày tính bằng mili giây.
     *
     * @return Số ngày kể từ 01/01/1970 đến nay.
     */
    private static long elapsedDaysSinceEpoch(long utcDate) {
        return TimeUnit.MILLISECONDS.toDays(utcDate);
    }

    public static long normalizeDate(long date) {
        long daysSinceEpoch = elapsedDaysSinceEpoch(date);
        long millisFromEpochToTodayAtMidnightUtc = daysSinceEpoch * DAY_IN_MILLIS;
        return millisFromEpochToTodayAtMidnightUtc;
    }

    /**
     * Vì tất cả các ngày tháng từ cơ sở dữ liệu là UTC, chúng ta phải chuyển đổi ngày đã cho
     * (trong múi giờ UTC) sang ngày trong múi giờ địa phương. Chức năng này thực hiện chuyển
     * đổi đó bằng cách sử dụng TimeZone offset
     */
    public static boolean isDateNormalized(long millisSinceEpoch) {
        boolean isDateNormalized = false;
        if (millisSinceEpoch % DAY_IN_MILLIS == 0) {
            isDateNormalized = true;
        }

        return isDateNormalized;
    }

    /**
     * Vì tất cả các ngày tháng từ cơ sở dữ liệu là UTC, chúng ta phải chuyển đổi ngày địa
     * phương sang ngày theo giờ UTC. Chức năng này thực hiện chuyển đổi đó bằng cách
     * sử dụng TimeZone offset.
     */
    private static long getLocalMidnightFromNormalizedUtcDate(long normalizedUtcDate) {
        TimeZone timeZone = TimeZone.getDefault();
        long gmtOffset = timeZone.getOffset(normalizedUtcDate);
        long localMidnightMillis = normalizedUtcDate - gmtOffset;
        return localMidnightMillis;
    }

    /**
     * Phương thức trợ giúp để chuyển đổi cơ sở dữ liệu đại diện của ngày thành một thứ để hiển thị
     * cho người dùng.
     */
    public static String getFriendlyDateString(Context context, long normalizedUtcMidnight, boolean showFullDate) {

        long localDate = getLocalMidnightFromNormalizedUtcDate(normalizedUtcMidnight);

        long daysFromEpochToProvidedDate = elapsedDaysSinceEpoch(localDate);

        long daysFromEpochToToday = elapsedDaysSinceEpoch(System.currentTimeMillis());

        if (daysFromEpochToProvidedDate == daysFromEpochToToday || showFullDate) {
            /*
             * Nếu ngày chúng ta xây dựng chuỗi là ngày hôm nay, định dạng sẽ là "Today, June 24"
             */
            String dayName = getDayName(context, localDate);
            String readableDate = getReadableDateString(context, localDate);
            if (daysFromEpochToProvidedDate - daysFromEpochToToday < 2) {
                String localizedDayName = new SimpleDateFormat("EEEE").format(localDate);
                return readableDate.replace(localizedDayName, dayName);
            } else {
                return readableDate;
            }
        } else if (daysFromEpochToProvidedDate < daysFromEpochToToday + 7) {
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
        long daysFromEpochToProvidedDate = elapsedDaysSinceEpoch(dateInMillis);
        long daysFromEpochToToday = elapsedDaysSinceEpoch(System.currentTimeMillis());

        int daysAfterToday = (int) (daysFromEpochToProvidedDate - daysFromEpochToToday);

        switch (daysAfterToday) {
            case 0:
                return context.getString(R.string.today);
            case 1:
                return context.getString(R.string.tomorrow);

            default:
                SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
                return dayFormat.format(dateInMillis);
        }
    }
}
package com.example.android.weather.utilities;

import android.content.ContentValues;
import android.content.Context;

import com.example.android.weather.data.WeatherContract;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.example.android.weather.data.WeatherContract.WeatherEntry;

public class FakeDataUtils {

    private static int [] weatherIDs = {200,300,500,711,900,962};

    /**
     * Tạo một đối tượng ContentValues duy nhất với dữ liệu thời tiết ngẫu nhiên cho ngày được cung cấp
     * @param ngày đã được normalize
     * @return ContentValues đối tượng với dữ liệu thời tiết ngẫu nhiên
     */
    private static ContentValues createTestWeatherContentValues(long date) {
        ContentValues testWeatherValues = new ContentValues();
        testWeatherValues.put(WeatherEntry.COLUMN_DATE, date);
        testWeatherValues.put(WeatherEntry.COLUMN_DEGREES, Math.random()*2);
        testWeatherValues.put(WeatherEntry.COLUMN_HUMIDITY, Math.random()*100);
        testWeatherValues.put(WeatherEntry.COLUMN_PRESSURE, 870 + Math.random()*100);
        int maxTemp = (int)(Math.random()*100);
        testWeatherValues.put(WeatherEntry.COLUMN_MAX_TEMP, maxTemp);
        testWeatherValues.put(WeatherEntry.COLUMN_MIN_TEMP, maxTemp - (int) (Math.random()*10));
        testWeatherValues.put(WeatherEntry.COLUMN_WIND_SPEED, Math.random()*10);
        testWeatherValues.put(WeatherEntry.COLUMN_WEATHER_ID, weatherIDs[(int)(Math.random()*10)%5]);
        return testWeatherValues;
    }

    /**
     * Tạo dữ liệu thời tiết ngẫu nhiên cho 7 ngày, kể từ ngày hôm nay
     * @param context
     */
    public static void insertFakeData(Context context) {
        //Lấy ngày normalize hôm nay
        long today = WeatherDateUtils.normalizeDate(System.currentTimeMillis());
        List<ContentValues> fakeValues = new ArrayList<ContentValues>();
        //Lặp qua 7 ngày kể từ hôm nay
        for(int i=0; i<7; i++) {
            fakeValues.add(FakeDataUtils.createTestWeatherContentValues(today + TimeUnit.DAYS.toMillis(i)));
        }
        // Bulk Insert dữ liệu thời tiết mới vào Database của Weather
        context.getContentResolver().bulkInsert(
                WeatherContract.WeatherEntry.CONTENT_URI,
                fakeValues.toArray(new ContentValues[7]));
    }
}
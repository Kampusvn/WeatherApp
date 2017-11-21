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
package com.example.android.weather;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.android.weather.data.WeatherPreferences;
import com.example.android.weather.utilities.NetworkUtils;
import com.example.android.weather.utilities.OpenWeatherJsonUtils;

import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private TextView mWeatherTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        /*
         * Sử dụng findViewById, ta nhận được một tham chiếu đến TextView từ xml. Điều này
         * cho phép ta làm những việc như đặt text cho TextView.
         */
        mWeatherTextView = (TextView) findViewById(R.id.tv_weather_data);

        /* Khi tất cả các view đã được cài đặt, ta có thể load dữ liệu thời tiết. */
        loadWeatherData();
    }

    /**
     * Phương thức này sẽ lấy vị trí người dùng mong muốn, sau đó cho
     * một số phương thức chạy nền để có được dữ liệu thời tiết.
     */
    private void loadWeatherData() {
        String location = WeatherPreferences.getPreferredWeatherLocation(this);
        new FetchWeatherTask().execute(location);
    }

    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            String location = params[0];
            URL weatherRequestUrl = NetworkUtils.buildUrl(location);

            try {
                String jsonWeatherResponse = NetworkUtils
                        .getResponseFromHttpUrl(weatherRequestUrl);

                String[] simpleJsonWeatherData = OpenWeatherJsonUtils
                        .getSimpleWeatherStringsFromJson(MainActivity.this, jsonWeatherResponse);

                return simpleJsonWeatherData;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] weatherData) {
            if (weatherData != null) {
                for (String weatherString : weatherData) {
                    mWeatherTextView.append((weatherString) + "\n\n\n");
                }
            }
        }
    }

    // TODO (2) Tạo menu resource ở res/menu/ called forecast.xml
    // TODO (3) Thêm một item vào menu với ID là action_refresh
    // TODO (4) Đặt tiêu đề cho item đó là "Refresh" trong strings.xml

    // TODO (5) Override phương thức onCreateOptionsMenu để inflate menu cho Activity
    // TODO (6) Trả về true để hiển thị menu

    // TODO (7) Override phương thức onOptionsItemSelected để quản lí nút refresh
}
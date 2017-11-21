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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.weather.data.WeatherPreferences;
import com.example.android.weather.utilities.NetworkUtils;
import com.example.android.weather.utilities.OpenWeatherJsonUtils;

import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private TextView mWeatherTextView;

    // Hoàn thành (6) Thêm biến TextView hiển thị thông báo lỗi
    private TextView mErrorMessageDisplay;

    // Hoàn thành (16) Thêm biến ProgressBar
    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        /*
         * Sử dụng findViewById, ta nhận được một tham chiếu đến TextView từ xml. Điều này
         * cho phép ta làm những việc như đặt text cho TextView.
         */
        mWeatherTextView = (TextView) findViewById(R.id.tv_weather_data);

        // Hoàn thành (7) Tìm TextView báo lỗi bằng findViewById
        /* TextView này dùng để hiển thị lỗi và sẽ được ẩn đi nếu không có lỗi */
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        // Hoàn thành (17) Tìm ProgressBar bằng findViewById

        /*
         * ProgressBar hiển thị tiến trình loading và sẽ được ẩn khi không load data
         *
         * Chú ý: Mặc dù gọi là "ProgressBar" nhưng mặc định nó không phải dạng thanh (bar)
         * mà là hình tròn.
         */
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        /* Khi tất cả các view đã được cài đặt, ta có thể load dữ liệu thời tiết. */
        loadWeatherData();
    }

    /**
     * Phương thức này sẽ lấy vị trí người dùng mong muốn, sau đó cho
     * một số phương thức chạy nền để có được dữ liệu thời tiết.
     */
    private void loadWeatherData() {
        // Hoàn thành (20) Gọi phương thức showWeatherDataView trước khi thực thi AsyncTask
        showWeatherDataView();

        String location = WeatherPreferences.getPreferredWeatherLocation(this);
        new FetchWeatherTask().execute(location);
    }

    // Hoàn thành (8) Viết phương thức showWeatherDataView để ẩn thông báo lỗi và hiển thị data
    /**
     * Phương thức này sẽ hiển thị View cho weather data và ẩn thông báo lỗi
     *
     */
    private void showWeatherDataView() {
        /* Đầu tiên ẩn error message*/
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Sau đó hiển thị dữ liệu về thời tiết*/
        mWeatherTextView.setVisibility(View.VISIBLE);
    }

    // Hoàn thành (9) Viết phương thức showErrorMessage để ẩn data và hiển thị thông báo lỗi
    /**
     * This method will make the error message visible and hide the weather
     * View.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mWeatherTextView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

        // Hoàn thành (18) Trong AsyncTask, override phương thức onPreExecute và hiển thị loading indicator
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

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
            // Hoàn thành (19) Sau khi load xong data, ẩn loading indicator
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (weatherData != null) {
                // Hoàn thành (11) Nếu data không phải là null, hãy chắc chắn data view được hiển thị
                showWeatherDataView();
                /*
                 * Iterate through the array and append the Strings to the TextView. The reason why we add
                 * the "\n\n\n" after the String is to give visual separation between each String in the
                 * TextView. Later, we'll learn about a better way to display lists of data.
                 */
                for (String weatherString : weatherData) {
                    mWeatherTextView.append((weatherString) + "\n\n\n");
                }
            } else {
                // TODO (10) Nếu data null, hiển thị thông báo lỗi
                showErrorMessage();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.forecast, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            mWeatherTextView.setText("");
            loadWeatherData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
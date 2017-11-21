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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

    // Hoàn thành (33) Xóa mWeatherTextView
    // Hoàn thành (34) Add a private RecyclerView variable called mRecyclerView
    private RecyclerView mRecyclerView;
    // Hoàn thành (35) Add a private ForecastAdapter variable called mForecastAdapter
    private ForecastAdapter mForecastAdapter;

    private TextView mErrorMessageDisplay;

    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        // Hoàn thành (36) Xóa dòng lấy tham chiếu đến mWeatherTextView

        // Hoàn thành (37) Sử dụng findViewById để lấy tham chiếu của RecyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_forecast);
        /* TextView này dùng để hiển thị lỗi và sẽ được ẩn đi nếu không có lỗi */
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        // Hoàn thành (38) Viết layoutManager, một LinearLayoutManager với orientation là vertical và shouldReverseLayout == false

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        // Hoàn thành (41) Đặt layoutManager trong mRecyclerView
        mRecyclerView.setLayoutManager(layoutManager);

        // Hoàn thành (40) Sử dụng setHasFixedSize(true) trên mRecyclerView để tất cả các item trong danh sách sẽ có cùng kích thước
        mRecyclerView.setHasFixedSize(true);

        // Hoàn thành (43) Đặt mForecastAdapter bằng ForecastAdapter mới
        mForecastAdapter = new ForecastAdapter();

        // Hoàn thành (44) Sử dụng mRecyclerView.setAdapter và chuyền vào mForecastAdapter
        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mRecyclerView.setAdapter(mForecastAdapter);

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
        showWeatherDataView();

        String location = WeatherPreferences.getPreferredWeatherLocation(this);
        new FetchWeatherTask().execute(location);
    }

    /**
     * Phương thức này sẽ hiển thị View cho weather data và ẩn thông báo lỗi
     *
     */
    private void showWeatherDataView() {
        /* Đầu tiên ẩn error message*/
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        // Hoàn thành (44) Hiển thị mRecyclerView, không phải mWeatherTextView
        /* Sau đó hiển thị dữ liệu về thời tiết*/
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * Phương thức hiển thị error message và ẩn weather view.
     */
    private void showErrorMessage() {
        // Hoàn thành (44) Ẩn mRecyclerView, không phải mWeatherTextView
        /* Đầu tiên là ẩn dữ liệu*/
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Sau đó hiển thị error message*/
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

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
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (weatherData != null) {
                showWeatherDataView();
                // Hoàn thành (45) Thay vì lặp qua tất cả các chuỗi, hãy sử dụng mForecastAdapter.setWeatherData và và chuyền vào dữ liệu
                mForecastAdapter.setWeatherData(weatherData);
            } else {
                showErrorMessage();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Sử dụng phương thức GetMenuInflater của AppCompatActivity để có được một trình xử lý trên menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Dùng phương thức inflate của inflater để inflate menu layout vào menu*/
        inflater.inflate(R.menu.forecast, menu);
        /* Trả về true để menu hiển thị trên toolbar*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            // Hoàn thành (46) Instead of setting the text to "", set the adapter to null before refreshing
            mForecastAdapter.setWeatherData(null);
            loadWeatherData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
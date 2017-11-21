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

import android.content.Context;
import android.content.Intent;
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

import com.example.android.weather.ForecastAdapter.ForecastAdapterOnClickHandler;
import com.example.android.weather.data.WeatherPreferences;
import com.example.android.weather.utilities.NetworkUtils;
import com.example.android.weather.utilities.OpenWeatherJsonUtils;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements ForecastAdapterOnClickHandler {

    private RecyclerView mRecyclerView;
    private ForecastAdapter mForecastAdapter;

    private TextView mErrorMessageDisplay;

    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);


        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_forecast);

        /* TextView này dùng để hiển thị lỗi và sẽ được ẩn đi nếu không có lỗi */
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        /*
         * LinearLayoutManager hỗ trợ cả hướng dọc và ngang. Thông số reverse layout
         * rất hữu ích cho layout hướng ngang khi phải dùng với các ngôn ngữ viết từ phải qua trái.
         */
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);

        /*
         * Sử dụng cài đặt này để cải thiện hiệu suất nếu những thay đổi trong
         * nội dung sẽ không thay đổi kích thước layout con trong RecyclerView
         */
        mRecyclerView.setHasFixedSize(true);

        /*
         * ForecastAdapter chịu trách nhiệm liên kết dữ liệu thời tiết với
         * các view và kết quả là hiển thị dữ liệu về thời tiết lên màn hình thiết bị
         */
        mForecastAdapter = new ForecastAdapter(this);

        /* Thiết lập cho adapter gắn với RecyclerView trong layout. */
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
    @Override
    public void onClick(String weatherForDay) {
        Context context = this;
        Class destinationClass = DetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        // TODO (1) Chuyền thông tin thời tiết vào DetailActivity
        startActivity(intentToStartDetailActivity);
    }

    /**
     * Phương thức hiển thị data và ẩn error message.
     */
    private void showWeatherDataView() {
        /* Đầu tiên ẩn error message*/
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Sau đó hiển thị dữ liệu về thời tiết*/
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * Phương thức hiển thị error message và ẩn weather view.
     */
    private void showErrorMessage() {
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
            mForecastAdapter.setWeatherData(null);
            loadWeatherData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

// TODO (1) Implement giao diện LoaderCallbacks và các phương thức của giao diện này
public class MainActivity extends AppCompatActivity implements ForecastAdapterOnClickHandler {

    private static final String TAG = MainActivity.class.getSimpleName();

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

        // TODO (7) Xóa code cho AsyncTask và khỏi tạo AsyncTaskLoader
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

    // TODO (2) Trong onCreateLoader, trả về AsyncTaskLoader mới cũng giống như FetchWeatherTask.
    // TODO (3) Cache dữ liệu thời tiết trong một biến thành viên và gửi nó trong onStartLoading.

    // TODO (4) Khi load xong, hiển thị dữ liệu về thời tiết hoặc báo lỗi nếu không có dữ liệu

    /**
     * Phương thức này sẽ hiển thị View cho weather data và ẩn thông báo lỗi
     *
     */
    @Override
    public void onClick(String weatherForDay) {
        Context context = this;
        Class destinationClass = DetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra(Intent.EXTRA_TEXT, weatherForDay);
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

    // TODO (6) Xóa tất cả code trong MainActivity có liên quan đến FetchWeatherTask
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

    /**
     * Phương thức này sử dụng lược đồ URI để hiển thị vị trí được tìm thấy trên bản đồ.
     * Mục đích siêu tiện dụng này được nêu chi tiết trong trang "Mục đích Chung" của trang web dành cho nhà phát triển Android:

     */
    private void openLocationInMap() {
        String addressString = "1600 Ampitheatre Parkway, CA";
        Uri geoLocation = Uri.parse("geo:0,0?q=" + addressString);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d(TAG, "Couldn't call " + geoLocation.toString()
                    + ", no receiving apps installed!");
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

        // TODO (5) Refactor chức năng refresh để làm việc với AsyncTaskLoader
        if (id == R.id.action_refresh) {
            mForecastAdapter.setWeatherData(null);
            loadWeatherData();
            return true;
        }

        if (id == R.id.action_map) {
            openLocationInMap();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
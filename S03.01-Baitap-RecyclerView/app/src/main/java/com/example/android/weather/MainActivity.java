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

    // Trong forecast_list_item.xml: //////////////////////////////////////////////////////////////
    // TODO (5) Thêm layout cho một item trong danh sách, đặt tên là forecast_list_item.xml
    // TODO (6) Đặt view gốc là LinearLayout theo hướng dọc
    // TODO (7) Đặt width của LinearLayout là match_parent và height là wrap_content

    // TODO (8) Thêm TextView với id là @+id/tv_weather_data
    // TODO (9) Đặt text size bằng 22sp
    // TODO (10) Đặt width và height là wrap_content
    // TODO (11) Đặt padding của TextView là 16dp

    // TODO (12) Thêm View vào layout với width là match_parent và height bằng 1dp
    // TODO (13) ĐẶt màu nền là #dadada
    // TODO (14) Đặt margins trái phải là 8dp
    // Trong forecast_list_item.xml //////////////////////////////////////////////////////////////


    // Trong ForecastAdapter.java /////////////////////////////////////////////////////////////////
    // TODO (15) Thêm lớp ForecastAdapter
    // TODO (22) Extend RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder> ở tên lớp

    // TODO (23) Tạo biến private string array và đặt tên là mWeatherData

    // TODO (47) Viết constructor mặc định (Ta sẽ chuyền tham số vào sau)

    // TODO (16) Thêm lớp ForecastAdapterViewHolder trong ForecastAdapter
    // TODO (17) Extend RecyclerView.ViewHolder ở tên lớp

    // Trong ForecastAdapterViewHolder ///////////////////////////////////////////////////////////
    // TODO (18) Tạo biến public final TextView tên là mWeatherTextView

    // TODO (19) Viết constructor nhận tham số là một View
    // TODO (20) Gọi super(view) trong constructor cho ForecastAdapterViewHolder
    // TODO (21) Dùng view.findViewById, lấy tham chiếu đến TextView của bố cục này và lưu nó vào mWeatherTextView
    // Trong ForecastAdapterViewHolder ///////////////////////////////////////////////////////////


    // TODO (24) Override onCreateViewHolder
    // TODO (25) Trong onCreateViewHolder, inflate danh sách item xml vào một view
    // TODO (26) Trong onCreateViewHolder, trả về ForecastAdapterViewHolder với view được chuyền vào bằng tham số

    // TODO (27) Override onBindViewHolder
    // TODO (28) Đặt text của TextView vào vị trí trong danh sách

    // TODO (29) Override getItemCount
    // TODO (30) Trả về 0 nếu mWeatherData bằng null, hoặc trả về kích cỡ của mWeatherData nếu không null

    // TODO (31) Viết phương thức setWeatherData lưu trữ weatherData vào mWeatherData
    // TODO (32) Sau khi lưu mWeatherData, gọi phương thức notifyDataSetChanged
    // Trong ForecastAdapter.java /////////////////////////////////////////////////////////////////


    // TODO (33) Xóa mWeatherTextView
    private TextView mWeatherTextView;

    // TODO (34) Thêm biến private RecyclerView tên là mRecyclerView
    // TODO (35) Thêm biến private ForecastAdapter tên là mForecastAdapter

    private TextView mErrorMessageDisplay;

    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        // TODO (36) Xóa dòng lấy tham chiếu đến mWeatherTextView
        /*
         * Sử dụng findViewById, ta nhận được một tham chiếu đến TextView từ xml. Điều này
         * cho phép ta làm những việc như đặt text cho TextView.
         */
        mWeatherTextView = (TextView) findViewById(R.id.tv_weather_data);

        // TODO (37) Sử dụng findViewById để lấy tham chiếu của RecyclerView
        /* TextView này dùng để hiển thị lỗi và sẽ được ẩn đi nếu không có lỗi */
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        // TODO (38) Viết layoutManager, một LinearLayoutManager với orientation là vertical và shouldReverseLayout == false

        // TODO (39) Đặt layoutManager trong mRecyclerView

        // TODO (40) Sử dụng setHasFixedSize(true) trên mRecyclerView để tất cả các item trong danh sách sẽ có cùng kích thước

        // TODO (41) Đặt mForecastAdapter bằng ForecastAdapter mới

        // TODO (42) Sử dụng mRecyclerView.setAdapter và chuyền vào mForecastAdapter

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
        // TODO (43) Hiển thị mRecyclerView, không phải mWeatherTextView
        /* Sau đó hiển thị dữ liệu về thời tiết*/
        mWeatherTextView.setVisibility(View.VISIBLE);
    }

    /**
     * Phương thức hiển thị error message và ẩn weather view.
     */
    private void showErrorMessage() {
        // TODO (44) Ẩn mRecyclerView, không phải mWeatherTextView
        /* Đầu tiên là ẩn dữ liệu*/
        mWeatherTextView.setVisibility(View.INVISIBLE);
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
                // TODO (45) Thay vì lặp qua tất cả các chuỗi, hãy sử dụng mForecastAdapter.setWeatherData và và chuyền vào dữ liệu
                /*
                 * Lặp lại thông qua các mảng và thêm Strings vào TextView. Ta tạm dùng
                 * "\n\n\n" để ngăn cách các string. Trong các bài sau ta sẽ dùng cách khác
				 * để làm việc này.
                 */
                for (String weatherString : weatherData) {
                    mWeatherTextView.append((weatherString) + "\n\n\n");
                }
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
            // TODO (46) Instead of setting the text to "", set the adapter to null before refreshing
            mWeatherTextView.setText("");
            loadWeatherData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
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
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
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

import com.example.android.weather.data.WeatherPreferences;
import com.example.android.weather.utilities.NetworkUtils;
import com.example.android.weather.utilities.OpenWeatherJsonUtils;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements
        ForecastAdapter.ForecastAdapterOnClickHandler,
        LoaderCallbacks<String[]> {

    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private ForecastAdapter mForecastAdapter;

    private TextView mErrorMessageDisplay;

    private ProgressBar mLoadingIndicator;

    private static final int FORECAST_LOADER_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        /*
         * Sử dụng findViewById, ta nhận được một tham chiếu đến RecyclerView từ xml. Điều này cho
		 * phép ta làm những việc như thiết lập bộ adapter của RecyclerView và bật / tắt hiển thị.
         */
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_forecast);

        /* TextView này dùng để hiển thị lỗi và sẽ được ẩn đi nếu không có lỗi */
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        /*
         * LinearLayoutManager hỗ trợ cả hướng dọc và ngang. Thông số reverse layout
         * rất hữu ích cho layout hướng ngang khi phải dùng với các ngôn ngữ viết từ phải qua trái.
         */
        int recyclerViewOrientation = LinearLayoutManager.VERTICAL;

        /*
         *  Giá trị này phải là true nếu bạn muốn đảo ngược layout của mình. Nói chung, điều này
		 * chỉ đúng với các danh sách ngang cần hỗ trợ layout từ phải sang trái.
         */
        boolean shouldReverseLayout = false;
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, recyclerViewOrientation, shouldReverseLayout);
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

        /*
         * ID này sẽ xác định Loader. Chúng ta có thể sử dụng nó để có được một
		 * xử lý trên Loader sau này thông qua LoaderManager hỗ trợ.
         */
        int loaderId = FORECAST_LOADER_ID;

        /*
         * Từ MainActivity, chúng ta đã triển khai giao diện LoaderCallbacks với kiểu của mảng String
		 * (thực hiện LoaderCallbacks <String []>)
		 * Biến callback được chuyển đến lời gọi initLoader bên dưới. Điều này có nghĩa là bất cứ khi
		 * nào loaderManager có thông báo, nó sẽ làm như vậy thông qua callback này.
         */
        LoaderCallbacks<String[]> callback = MainActivity.this;

        /*
         * Tham số thứ hai của phương thức initLoader dưới đây là một Bundle. Bạn có thể
		 * chuyền một Bundle vào initLoader mà bạn có thể truy cập từ bên trong callback onCreateLoader.
		 * Nhưng trong trường hợp này ta không sử dụng Bundle.
         */
        Bundle bundleForLoader = null;

        /*
         * Đảm bảo laoder được khởi tạo và hoạt động. Nếu loader không tồn tại, tạo một loader và
		 * (nếu activity / fragment hiện đang được khởi động) bắt đầu trình nạp. Nếu không, loader
		 * cuối cùng được tạo ra sẽ được sử dụng lại.
         */
        getSupportLoaderManager().initLoader(loaderId, bundleForLoader, callback);

        Log.d(TAG, "onCreate: registering preference changed listener");
    }

    /**
     * Khởi chạy và trả về loader mới cho ID nhất định.
     *
     * @param id ID có loader được tạo ra.
     * @param loaderArgs Bất kỳ đối số nào được cung cấp bởi caller.
     *
     * @return Trả về thể hiện của Loader mới sẵn sàng làm việc.
     */
    @Override
    public Loader<String[]> onCreateLoader(int id, final Bundle loaderArgs) {

        return new AsyncTaskLoader<String[]>(this) {

            /* Chuỗi String này sẽ lưu trữ và cache dữ liệu thời tiết */
            String[] mWeatherData = null;

            /**
             * Các lớp phụ của AsyncTaskLoader phải thực hiện việc này để tải dữ liệu của chúng.
             */
            @Override
            protected void onStartLoading() {
                if (mWeatherData != null) {
                    deliverResult(mWeatherData);
                } else {
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            /**
             * Đây là phương thức của AsyncTaskLoader sẽ tải và phân tích cú pháp dữ liệu JSON từ OpenWeatherMap ở nền.
             *
             * @return Dữ liệu thời tiết từ OpenWeatherMap dưới dạng một dãy các chuỗi.
             * null nếu xảy ra lỗi
             */
            @Override
            public String[] loadInBackground() {

                String locationQuery = WeatherPreferences
                        .getPreferredWeatherLocation(MainActivity.this);

                URL weatherRequestUrl = NetworkUtils.buildUrl(locationQuery);

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

            /**
             * Gửi kết quả load cho listener đã đăng ký.
             *
             * @param data Kết quả load
             */
            public void deliverResult(String[] data) {
                mWeatherData = data;
                super.deliverResult(data);
            }
        };
    }

    /**
     * Gọi khi một loader tạo ra trước đó đã hoàn tất công việc của nó.
     *
     * @param loader Loader đã hoàn tất.
     * @param data Dữ liệu tạo ra bởi Loader.
     */
    @Override
    public void onLoadFinished(Loader<String[]> loader, String[] data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mForecastAdapter.setWeatherData(data);
        if (null == data) {
            showErrorMessage();
        } else {
            showWeatherDataView();
        }
    }

    /**
     * Được gọi là khi loader đã tạo trước đây được reset, và do đó làm cho dữ liệu của nó
     * không khả dụng. Lúc này ứng dụng sẽ loại bỏ bất kỳ references nào có dữ liệu của Loader.
     *
     * @param loader Loader đang được reset
     */
    @Override
    public void onLoaderReset(Loader<String[]> loader) {
        /*
         * Chúng ta không sử dụng phương thức này trong ứng dụng nhưng sẽ override nó để triển khai
		 * giao diện LoaderCallbacks<String>
         */
    }

    /**
     * Phương thức này được sử dụng khi đặt lại dữ liệu, do đó tại một thời điểm trong
     * quá trình làm mới dữ liệu, bạn có thể thấy không có dữ liệu hiển thị.
     */
    private void invalidateData() {
        mForecastAdapter.setWeatherData(null);
    }

    /**
     * Phương thức này sử dụng lược đồ URI để hiển thị vị trí trên bản đồ kết hợp với implicit Intent.
     * Mục đích siêu tiện dụng này được nêu chi tiết trong trang "Common Intents" của trang web
     * dành cho nhà phát triển Android:
     *
     * @see "http://developer.android.com/guide/components/intents-common.html#Maps"
     *
     * Protip: Giữ Command trên Mac hoặc Control trên Windows và nhấp vào liên kết để tự động mở
     * trang Common Intents
     */
    private void openLocationInMap() {

        String addressString = "1600 Ampitheatre Parkway, CA";
        Uri geoLocation = Uri.parse("geo:0,0?q=" + addressString);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d(TAG, "Couldn't call " + geoLocation.toString() + ", no receiving apps installed!");
        }
    }

    /**
     * Phương thức này để phản hồi khi item trong danh sách được bấm.
     *
     * @param weatherForDay Chuỗi mô tả chi tiết thời tiết cho một ngày cụ thể
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
            invalidateData();
            getSupportLoaderManager().restartLoader(FORECAST_LOADER_ID, null, this);
            return true;
        }

        if (id == R.id.action_map) {
            openLocationInMap();
            return true;
        }

        // Hoàn thành (6) Khởi chạy SettingsActivity khi bấm vào Settings
        if (id == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
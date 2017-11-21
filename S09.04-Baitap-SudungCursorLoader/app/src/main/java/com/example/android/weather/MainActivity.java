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
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
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
import com.example.android.weather.utilities.FakeDataUtils;
import com.example.android.weather.utilities.NetworkUtils;
import com.example.android.weather.utilities.OpenWeatherJsonUtils;

import java.net.URL;


public class MainActivity extends AppCompatActivity implements
//      TODO (15) Xóa các khai báo implement để SharedPreferences thay đổi listener và các phương thức
//      TODO (20) Implement LoaderCallbacks<Cursor> thay vì String[]
        ForecastAdapter.ForecastAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<String[]>,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private final String TAG = MainActivity.class.getSimpleName();

//  TODO (16) Tạo một mảng String có chứa tên của các cột dữ liệu bạn muốn từ ContentProvider

//  TODO (17) Tạo các hằng giá trị int biểu diễn vị trí của mỗi cột ở trên

    //  TODO (37) Xóa TextView báo lỗi
    private TextView mErrorMessageDisplay;

    /*
     * ID này sẽ được sử dụng để xác định Loader chịu trách nhiệm tải dự báo thời tiết. Trong một
	 * số trường hợp, một Activity có thể làm việc với nhiều Loader. Tuy nhiên, trong trường hợp
	 * này thì chỉ có một. Ta vẫn sẽ sử dụng ID này để khởi tạo loader và tạo loader để thực hành.
	 * Số 44 ở đây được chọn ngẫu nhiên. Bạn có thể sử dụng bất cứ số nào bạn thích, miễn là nó là
	 * duy nhất và nhất quán.
     */
    private static final int ID_FORECAST_LOADER = 44;

    private ForecastAdapter mForecastAdapter;
    private RecyclerView mRecyclerView;
    private int mPosition = RecyclerView.NO_POSITION;

    private ProgressBar mLoadingIndicator;

    //  TODO (35) Xóa cờ preference change
    private static boolean PREFERENCES_HAVE_BEEN_UPDATED = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);
        getSupportActionBar().setElevation(0f);



        FakeDataUtils.insertFakeData(this);

        /*
         * Sử dụng findViewById, ta nhận được một tham chiếu đến RecyclerView từ xml. Điều này cho
		 * phép ta làm những việc như thiết lập bộ adapter của RecyclerView và bật / tắt hiển thị.
         */
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_forecast);

//      TODO (36) Xóa bỏ lời gọi findViewById cho TextView báo lỗi
        /* TextView này dùng để hiển thị lỗi và sẽ được ẩn đi nếu không có lỗi */
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        /*
         * LinearLayoutManager hỗ trợ cả hướng dọc và ngang. Thông số reverse layout
         * rất hữu ích cho layout hướng ngang khi phải dùng với các ngôn ngữ viết từ phải qua trái.
         */
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        /*
         * Giá trị này phải là true nếu bạn muốn đảo ngược layout của mình. Nói chung, điều này
		 * chỉ đúng với các danh sách ngang cần hỗ trợ layout từ phải sang trái.
         */
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        /* setLayoutManager liên kết LayoutManager chúng tôi đã tạo ở trên với RecyclerView của chúng tôi */
        mRecyclerView.setLayoutManager(layoutManager);

        /*
         * Sử dụng cài đặt này để cải thiện hiệu suất nếu những thay đổi trong
         * nội dung sẽ không thay đổi kích thước layout con trong RecyclerView
         */
        mRecyclerView.setHasFixedSize(true);

//      TODO (4) Chuyền this như một ForecastAdapter
        /*
         * ForecastAdapter chịu trách nhiệm liên kết dữ liệu thời tiết với
         * các view và kết quả là hiển thị dữ liệu về thời tiết lên màn hình thiết bị
         */
        mForecastAdapter = new ForecastAdapter(this);

        /* Thiết lập cho adapter gắn với RecyclerView trong layout. */
        mRecyclerView.setAdapter(mForecastAdapter);

//      TODO (18) Gọi phương thức showLoading

        /*
         * ID này sẽ xác định Loader. Chúng ta có thể sử dụng nó để có được một
		 * xử lý trên Loader sau này thông qua LoaderManager hỗ trợ.
         */
        getSupportLoaderManager().initLoader(ID_FORECAST_LOADER, null, this);



//      TODO (19) Loại bỏ statement đăng ký activity như một preference changed listener
        Log.d(TAG, "onCreate: registering preference changed listener");
        /*
         * Đăng ký MainActivity là một OnSharedPreferenceChangedListener ở onCreate
         */
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    private void openPreferredLocationInMap() {
        double[] coords = WeatherPreferences.getLocationCoordinates(this);
        String posLat = Double.toString(coords[0]);
        String posLong = Double.toString(coords[1]);
        Uri geoLocation = Uri.parse("geo:" + posLat + "," + posLong);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d(TAG, "Couldn't call " + geoLocation.toString() + ", no receiving apps installed!");
        }
    }

//  TODO (21) Refactor onCreateLoader để trả về Loader<Cursor> thay vì not Loader<String[]>
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

//      TODO (23) Loại bỏ khai báo phương thức onStartLoading
//      TODO (24) Xóa khai báo phương thức loadInBackground
//      TODO (25) Xóa bỏ khai báo phương thức deliverResult
//          TODO (22) Nếu loader yêu cầu loader dự báo, hãy trả về CursorLoader thích hợp
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

                URL weatherRequestUrl = NetworkUtils.getUrl(MainActivity.this);

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

//  TODO (26) Thay đổi tham số onLoadFinished thành Loader<Cursor> thay vì Loader<String []>
    /**
     * Gọi khi một loader tạo ra trước đó đã hoàn tất công việc của nó.
     *
     * @param loader Loader đã hoàn tất.
     * @param data Dữ liệu tạo ra bởi Loader.
     */
    @Override
    public void onLoadFinished(Loader<String[]> loader, String[] data) {
        //      TODO (27) Xóa thân phương thức onLoadFinished
        //      TODO (28) Gọi phương thức swapCursor của mForecastAdapter và chuyền vào Cursor mới
        //      TODO (29) Nếu mPosition bằng RecyclerView.NO_POSITION, đặt là 0
        //      TODO (30) Chuyển các RecyclerView thành mPosition
        //      TODO (31) Nếu kích thước con trỏ khác 0, gọi showWeatherDataView
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
//      TODO (32) Gọi phương thức swapCursor của mForecastAdapter và truyền vào null
        /*
         * Chúng ta không sử dụng phương thức này trong ứng dụng nhưng sẽ override nó để triển khai
		 * giao diện LoaderCallbacks<String>
         */
    }

    /**
     * Phương thức này là để đáp ứng cho khi bấm vào danh sách.
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
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        /* Sau đó hiển thị dữ liệu về thời tiết*/
        mRecyclerView.setVisibility(View.VISIBLE);
    }

//  TODO (33) Xóa showErrorMessage
    /**
     * Phương thức hiển thị error message và ẩn weather view.
     */
    private void showErrorMessage() {
        /* Đầu tiên là ẩn dữ liệu*/
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Sau đó hiển thị error message*/
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

//  TODO (34) Viết phương thức showLoading cho thấy chỉ số loading và ẩn dữ liệu

    /**
     * Đây là nơi inflate và thiết lập menu cho Activity này.
     *
     * @param menu Menu option trong đó đặt các item.
     *
     * @return Trả về true cho menu được hiển thị; nếu trả về false nó sẽ không được hiển thị.
     *
     * @xem #onPrepareOptionsMenu
     * @xem #onOptionsItemSelected
     */
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

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        if (id == R.id.action_map) {
            openPreferredLocationInMap();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        PREFERENCES_HAVE_BEEN_UPDATED = true;
    }
}
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

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
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

import com.example.android.weather.data.WeatherPreferences;
import com.example.android.weather.data.WeatherContract;
import com.example.android.weather.sync.WeatherSyncUtils;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        ForecastAdapter.ForecastAdapterOnClickHandler {

    private final String TAG = MainActivity.class.getSimpleName();

    /*
     * Các cột dữ liệu mà ta muốn hiển thị trong danh sách dữ liệu thời tiết của MainActivity.
     */
    public static final String[] MAIN_FORECAST_PROJECTION = {
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
    };

    /*
     * Lưu trữ các chỉ số của các giá trị trong mảng các String phía trên để có thể truy cập dữ liệu
	 * nhanh chóng từ truy vấn. Nếu thứ tự của Strings ở trên thay đổi, các chỉ số này phải được điều
	 * chỉnh để phù hợp với thứ tự của chuỗi.
     */
    public static final int INDEX_WEATHER_DATE = 0;
    public static final int INDEX_WEATHER_MAX_TEMP = 1;
    public static final int INDEX_WEATHER_MIN_TEMP = 2;
    public static final int INDEX_WEATHER_CONDITION_ID = 3;


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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);
        getSupportActionBar().setElevation(0f);

        /*
         * Sử dụng findViewById, ta nhận được một tham chiếu đến RecyclerView từ xml. Điều này cho
		 * phép ta làm những việc như thiết lập bộ adapter của RecyclerView và bật / tắt hiển thị.
         */
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_forecast);

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

        /*
         * ForecastAdapter chịu trách nhiệm liên kết dữ liệu thời tiết với
         * các view và kết quả là hiển thị dữ liệu về thời tiết lên màn hình thiết bị
         */
        mForecastAdapter = new ForecastAdapter(this, this);

        /* Thiết lập cho adapter gắn với RecyclerView trong layout. */
        mRecyclerView.setAdapter(mForecastAdapter);


        showLoading();

        /*
         * ID này sẽ xác định Loader. Chúng ta có thể sử dụng nó để có được một
		 * xử lý trên Loader sau này thông qua LoaderManager hỗ trợ.
         */
        getSupportLoaderManager().initLoader(ID_FORECAST_LOADER, null, this);

        WeatherSyncUtils.initialize(this);

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

    /**
     * Khởi chạy và trả về loader mới cho ID nhất định.
     *
     * @param id ID có loader được tạo ra.
     * @param loaderArgs Bất kỳ đối số nào được cung cấp bởi caller.
     *
     * @return Trả về thể hiện của Loader mới sẵn sàng làm việc.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {


        switch (loaderId) {

            case ID_FORECAST_LOADER:
                /* URI cho tất cả các hàng dữ liệu thời tiết trong bảng */
                Uri forecastQueryUri = WeatherContract.WeatherEntry.CONTENT_URI;
                /* Thứ tự sắp xếp: Tăng dần theo ngày */
                String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
                /*
				 * Một SELECTION trong SQL khai báo những hàng bạn muốn trả về. Trong trường hợp
				 * này, ta muốn tất cả dữ liệu thời tiết từ ngày hôm nay trở đi được lưu trữ trong
				 * bảng thời tiết. Ta tạo ra một phương pháp hữu ích để làm điều đó trong lớp WeatherEntry.
                 */
                String selection = WeatherContract.WeatherEntry.getSqlSelectForTodayOnwards();

                return new CursorLoader(this,
                        forecastQueryUri,
                        MAIN_FORECAST_PROJECTION,
                        selection,
                        null,
                        sortOrder);

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    /**
     * Gọi khi một loader tạo ra trước đó đã hoàn tất công việc của nó.
     *
     * @param loader Loader đã hoàn tất.
     * @param data Dữ liệu tạo ra bởi Loader.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {


        mForecastAdapter.swapCursor(data);
        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
        mRecyclerView.smoothScrollToPosition(mPosition);
        if (data.getCount() != 0) showWeatherDataView();
    }

    /**
     * Được gọi là khi loader đã tạo trước đây được reset, và do đó làm cho dữ liệu của nó
     * không khả dụng. Lúc này ứng dụng sẽ loại bỏ bất kỳ references nào có dữ liệu của Loader.
     *
     * @param loader Loader đang được reset
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        /*
         * Chúng ta không sử dụng phương thức này trong ứng dụng nhưng sẽ override nó để triển khai
		 * giao diện LoaderCallbacks<String>
         */
        mForecastAdapter.swapCursor(null);
    }

    /**
     * Phương thức này là để đáp ứng cho khi bấm vào danh sách.
     *
     * @param weatherForDay Chuỗi mô tả chi tiết thời tiết cho một ngày cụ thể
     */
    @Override
    public void onClick(long date) {
        Intent weatherDetailIntent = new Intent(MainActivity.this, DetailActivity.class);
        Uri uriForDateClicked = WeatherContract.WeatherEntry.buildWeatherUriWithDate(date);
        weatherDetailIntent.setData(uriForDateClicked);
        startActivity(weatherDetailIntent);
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

    /**
     * Phương thức này sẽ làm cho loading indicator hiển thị và ẩn weather View sau đó thông báo lỗi.
     */
    private void showLoading() {
        /* Ẩn dữ liệu thời tiết */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Hiển thị loading indicator */
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

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
}
